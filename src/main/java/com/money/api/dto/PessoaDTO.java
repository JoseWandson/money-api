package com.money.api.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.money.api.model.Contato;
import com.money.api.model.Endereco;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PessoaDTO {

	@NotBlank
	private String nome;

	private Endereco endereco;

	@NotNull
	private Boolean ativo;

	@Valid
	private List<Contato> contatos;

}
