package com.money.api.dto;

import java.math.BigDecimal;

import com.money.api.model.Pessoa;
import com.money.api.model.TipoLancamento;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LancamentoEstatisticaPessoa {

	private TipoLancamento tipo;
	private Pessoa pessoa;
	private BigDecimal total;

}
