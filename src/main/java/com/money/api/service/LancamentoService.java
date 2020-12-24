package com.money.api.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.money.api.dto.LancamentoEstatisticaCategoria;
import com.money.api.dto.LancamentoEstatisticaDia;
import com.money.api.model.Lancamento;
import com.money.api.model.Lancamento_;
import com.money.api.model.Pessoa;
import com.money.api.repository.LancamentoRepository;
import com.money.api.repository.PessoaRepository;
import com.money.api.repository.filter.LancamentoFilter;
import com.money.api.repository.projection.ResumoLancamento;
import com.money.api.service.exception.PessoaInexistenteOuInativaException;

@Service
public class LancamentoService {

	@Autowired
	private LancamentoRepository lancamentoRepository;

	@Autowired
	private PessoaRepository pessoaRepository;

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

	private void validarPessoa(Lancamento lancamento) {
		Optional<Pessoa> pessoaOptional = pessoaRepository.findById(lancamento.getPessoa().getCodigo());
		if (!pessoaOptional.isPresent() || pessoaOptional.get().isInativo()) {
			throw new PessoaInexistenteOuInativaException();
		}
	}

}
