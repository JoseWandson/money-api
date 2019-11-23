package com.money.api.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

public class CategoriaDTO {

	@Getter
	@Setter
	@NotNull
	@Size(min = 3, max = 20)
	private String nome;

}
