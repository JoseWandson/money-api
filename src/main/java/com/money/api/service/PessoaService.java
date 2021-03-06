package com.money.api.service;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.money.api.model.Pessoa;
import com.money.api.repository.PessoaRepository;

@Service
public class PessoaService {

	@Autowired
	private PessoaRepository pessoaRepository;

	public Page<Pessoa> pesquisar(String nome, Pageable pageable) {
		return pessoaRepository.findByNomeContaining(nome, pageable);
	}

	public Pessoa salvar(Pessoa pessoa) {
		pessoa.getContatos().forEach(c -> c.setPessoa(pessoa));
		return pessoaRepository.save(pessoa);
	}

	public Optional<Pessoa> buscarPeloCodigo(Long codigo) {
		return pessoaRepository.findById(codigo);
	}

	public void remover(Long codigo) {
		pessoaRepository.deleteById(codigo);
	}

	public Pessoa atualizar(Long codigo, Pessoa pessoa) {
		Pessoa pessoaSalva = buscarPessoaPeloCodigo(codigo);

		pessoaSalva.getContatos().clear();
		pessoaSalva.getContatos().addAll(pessoa.getContatos());
		pessoaSalva.getContatos().forEach(c -> c.setPessoa(pessoaSalva));

		BeanUtils.copyProperties(pessoa, pessoaSalva, "codigo", "contatos");
		return pessoaRepository.save(pessoaSalva);
	}

	public void atualizarPropriedadeAtivo(Long codigo, Boolean ativo) {
		Pessoa pessoaSalva = buscarPessoaPeloCodigo(codigo);
		pessoaSalva.setAtivo(ativo);
		pessoaRepository.save(pessoaSalva);
	}

	private Pessoa buscarPessoaPeloCodigo(Long codigo) {
		return buscarPeloCodigo(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
	}

}
