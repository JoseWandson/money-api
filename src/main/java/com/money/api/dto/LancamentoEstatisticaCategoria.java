package com.money.api.dto;

import java.math.BigDecimal;

import com.money.api.model.Categoria;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LancamentoEstatisticaCategoria {

	private Categoria categoria;
	private BigDecimal total;

}
