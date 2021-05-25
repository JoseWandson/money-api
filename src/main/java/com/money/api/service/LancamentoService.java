package com.money.api.service;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.money.api.dto.LancamentoEstatisticaCategoria;
import com.money.api.dto.LancamentoEstatisticaDia;
import com.money.api.dto.LancamentoEstatisticaPessoa;
import com.money.api.mail.Mailer;
import com.money.api.model.Lancamento;
import com.money.api.model.Lancamento_;
import com.money.api.model.Pessoa;
import com.money.api.model.Usuario;
import com.money.api.repository.LancamentoRepository;
import com.money.api.repository.PessoaRepository;
import com.money.api.repository.UsuarioRepository;
import com.money.api.repository.filter.LancamentoFilter;
import com.money.api.repository.projection.ResumoLancamento;
import com.money.api.service.exception.PessoaInexistenteOuInativaException;
import com.money.api.storage.S3;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Slf4j
@Service
public class LancamentoService {

	private static final String DESTINATARIOS = "ROLE_PESQUISAR_LANCAMENTO";

	@Autowired
	private LancamentoRepository lancamentoRepository;

	@Autowired
	private PessoaRepository pessoaRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private Mailer mailer;

	@Autowired
	private S3 s3;

	public Page<Lancamento> pesquisar(LancamentoFilter lancamentoFilter, Pageable pageable) {
		return lancamentoRepository.filtrar(lancamentoFilter, pageable);
	}

	public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable) {
		return lancamentoRepository.resumir(lancamentoFilter, pageable);
	}

	public Optional<Lancamento> buscarPeloCodigo(Long codigo) {
		return lancamentoRepository.findById(codigo);
	}

	public Lancamento salvar(Lancamento lancamento) {
		validarPessoa(lancamento);

		if (StringUtils.hasText(lancamento.getAnexo())) {
			s3.salvar(lancamento.getAnexo());
		}

		return lancamentoRepository.save(lancamento);
	}

	public void remover(Long codigo) {
		lancamentoRepository.deleteById(codigo);
	}

	public Lancamento atualizar(Long codigo, Lancamento lancamento) {
		var lancamentoSalvo = buscarPeloCodigo(codigo).orElseThrow(IllegalArgumentException::new);
		if (!lancamento.getPessoa().equals(lancamentoSalvo.getPessoa())) {
			validarPessoa(lancamento);
		}

		if (!StringUtils.hasText(lancamento.getAnexo()) && StringUtils.hasText(lancamentoSalvo.getAnexo())) {
			s3.remover(lancamentoSalvo.getAnexo());
		} else if (StringUtils.hasText(lancamento.getAnexo())
				&& !lancamento.getAnexo().equals(lancamentoSalvo.getAnexo())) {
			s3.substituir(lancamentoSalvo.getAnexo(), lancamento.getAnexo());
		}

		BeanUtils.copyProperties(lancamento, lancamentoSalvo, Lancamento_.codigo.getName());

		return lancamentoRepository.save(lancamentoSalvo);
	}

	public List<LancamentoEstatisticaCategoria> porCategoria(LocalDate mesReferencia) {
		return lancamentoRepository.porCategoria(mesReferencia);
	}

	public List<LancamentoEstatisticaDia> porDia(LocalDate mesReferencia) {
		return lancamentoRepository.porDia(mesReferencia);
	}

	public byte[] relatorioPorPessoa(LocalDate inicio, LocalDate fim) throws IOException, JRException {
		List<LancamentoEstatisticaPessoa> dados = lancamentoRepository.porPessoa(inicio, fim);

		Map<String, Object> parametros = new HashMap<>();
		parametros.put("DT_INICIO", Date.valueOf(inicio));
		parametros.put("DT_FIM", Date.valueOf(fim));
		parametros.put("REPORT_LOCALE", new Locale("pt", "BR"));

		try (var inputStream = getClass().getResourceAsStream("/relatorios/lancamentos-por-pessoa.jasper")) {
			var jasperPrint = JasperFillManager.fillReport(inputStream, parametros,
					new JRBeanCollectionDataSource(dados));

			return JasperExportManager.exportReportToPdf(jasperPrint);
		}
	}

	@Scheduled(cron = "0 0 6 * * *")
	public void avisarSobreLancamentosVencidos() {
		if (log.isDebugEnabled()) {
			log.debug("Preparando envio de e-mails de aviso de lançamentos vencidos.");
		}

		List<Lancamento> vencidos = lancamentoRepository
				.findByDataVencimentoLessThanEqualAndDataPagamentoIsNull(LocalDate.now());
		if (vencidos.isEmpty()) {
			log.info("Sem lançamentos vencidos para aviso.");
			return;
		}
		log.info("Existem {} lançamentos vencidos.", vencidos.size());

		List<Usuario> destinatarios = usuarioRepository.findByPermissoesDescricao(DESTINATARIOS);
		if (destinatarios.isEmpty()) {
			log.warn("Existem lançamentos vencidos, mas o sistema não encontrou destinatários.");
			return;
		}

		mailer.avisarSobreLancamentosVencidos(vencidos, destinatarios);
		log.info("Envio de e-mail de aviso concluído.");
	}

	private void validarPessoa(Lancamento lancamento) {
		if (Objects.isNull(lancamento.getPessoa().getCodigo())) {
			return;
		}

		Optional<Pessoa> pessoaOptional = pessoaRepository.findById(lancamento.getPessoa().getCodigo());
		if (!pessoaOptional.isPresent() || pessoaOptional.get().isInativo()) {
			throw new PessoaInexistenteOuInativaException();
		}
	}

}
