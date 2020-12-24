package com.money.api.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum TipoLancamento {

	RECEITA("Receita"), DESPESA("Despesa");

	private final String descricao;

}
