package com.money.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.money.api.model.TipoLancamento;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LancamentoEstatisticaDia {

	private TipoLancamento tipo;
	private LocalDate dia;
	private BigDecimal total;

}
