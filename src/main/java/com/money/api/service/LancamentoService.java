package com.money.api.service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

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

	public Page<Lancamento> pesquisar(LancamentoFilter lancamentoFilter, Pageable pageable) {
		return lancamentoRepository.filtrar(lancamentoFilter, pageable);
	}

	public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable) {
		return lancamentoRepository.resumir(lancamentoFilter, pageable);
	}

	public Optional<Lancamento> buscarPeloCodigo(Long codigo) {
		return lancamentoRepository.findById(codigo);
	}

	public Lancamento criar(Lancamento lancamento) {
		validarPessoa(lancamento);
		return lancamentoRepository.save(lancamento);
	}

	public void remover(Long codigo) {
		lancamentoRepository.deleteById(codigo);
	}

	public Lancamento atualizar(Long codigo, Lancamento lancamento) {
		Lancamento lancamentoSalvo = buscarPeloCodigo(codigo).orElseThrow(IllegalArgumentException::new);
		if (!lancamento.getPessoa().equals(lancamentoSalvo.getPessoa())) {
			validarPessoa(lancamento);
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

		try (InputStream inputStream = getClass().getResourceAsStream("/relatorios/lancamentos-por-pessoa.jasper")) {
			JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, parametros,
					new JRBeanCollectionDataSource(dados));

			return JasperExportManager.exportReportToPdf(jasperPrint);
		}
	}

	@Scheduled(cron = "0 0 6 * * *")
	public void avisarSobreLancamentosVencidos() {
		List<Lancamento> vencidos = lancamentoRepository
				.findByDataVencimentoLessThanEqualAndDataPagamentoIsNull(LocalDate.now());
		List<Usuario> destinatarios = usuarioRepository.findByPermissoesDescricao(DESTINATARIOS);

		mailer.avisarSobreLancamentosVencidos(vencidos, destinatarios);
	}

	private void validarPessoa(Lancamento lancamento) {
		Optional<Pessoa> pessoaOptional = pessoaRepository.findById(lancamento.getPessoa().getCodigo());
		if (!pessoaOptional.isPresent() || pessoaOptional.get().isInativo()) {
			throw new PessoaInexistenteOuInativaException();
		}
	}

}
