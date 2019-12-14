package com.money.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.money.api.model.Lancamento;
import com.money.api.model.Pessoa;
import com.money.api.repository.LancamentoRepository;
import com.money.api.repository.PessoaRepository;
import com.money.api.repository.filter.LancamentoFilter;
import com.money.api.service.exception.PessoaInexistenteOuInativaException;

@Service
public class LancamentoService {

	@Autowired
	private LancamentoRepository lancamentoRepository;

	@Autowired
	private PessoaRepository pessoaRepository;

	public List<Lancamento> pesquisar(LancamentoFilter lancamentoFilter) {
		return lancamentoRepository.filtrar(lancamentoFilter);
	}

	public Optional<Lancamento> buscarPeloCodigo(Long codigo) {
		return lancamentoRepository.findById(codigo);
	}

	public Lancamento criar(Lancamento lancamento) {
		Optional<Pessoa> pessoaOptional = pessoaRepository.findById(lancamento.getPessoa().getCodigo());
		if (!pessoaOptional.isPresent() || pessoaOptional.get().isInativo()) {
			throw new PessoaInexistenteOuInativaException();
		}
		return lancamentoRepository.save(lancamento);
	}

	public void remover(Long codigo) {
		lancamentoRepository.deleteById(codigo);
	}

}
