package com.money.api.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

public class CategoriaDTO {

	@Getter
	@Setter
	@NotBlank
	@Size(min = 3, max = 20)
	private String nome;

}
