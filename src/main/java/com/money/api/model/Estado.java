package com.money.api.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "estado")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Estado {

	@Id
	@EqualsAndHashCode.Include
	private Long codigo;
	private String nome;

}
