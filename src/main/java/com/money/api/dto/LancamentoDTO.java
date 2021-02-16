package com.money.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.money.api.model.Categoria;
import com.money.api.model.Pessoa;
import com.money.api.model.TipoLancamento;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LancamentoDTO {

	@NotBlank
	private String descricao;

	@NotNull
	private LocalDate dataVencimento;

	private LocalDate dataPagamento;

	@NotNull
	private BigDecimal valor;

	private String observacao;

	@NotNull
	private TipoLancamento tipo;

	@NotNull
	private Categoria categoria;

	@NotNull
	private Pessoa pessoa;

	private String anexo;

}
