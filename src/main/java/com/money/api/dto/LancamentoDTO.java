package com.money.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.money.api.model.Categoria;
import com.money.api.model.Pessoa;
import com.money.api.model.TipoLancamento;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LancamentoDTO {

	private String descricao;
	private LocalDate dataVencimento;
	private BigDecimal valor;
	private TipoLancamento tipo;
	private Categoria categoria;
	private Pessoa pessoa;

}
