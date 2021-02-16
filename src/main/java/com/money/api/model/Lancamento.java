package com.money.api.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "lancamento")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Lancamento {

	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;

	private String descricao;

	@Column(name = "data_vencimento")
	private LocalDate dataVencimento;

	@Column(name = "data_pagamento")
	private LocalDate dataPagamento;

	private BigDecimal valor;

	private String observacao;

	@Enumerated(EnumType.STRING)
	private TipoLancamento tipo;

	@ManyToOne
	@JoinColumn(name = "codigo_categoria")
	private Categoria categoria;

	@ManyToOne
	@JsonIgnoreProperties("contatos")
	@JoinColumn(name = "codigo_pessoa")
	private Pessoa pessoa;

	private String anexo;

	@Transient
	private String urlAnexo;

	@JsonIgnore
	public boolean isReceita() {
		return TipoLancamento.RECEITA.equals(tipo);
	}

}
