package com.money.api.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@ConfigurationProperties("money")
public class MoneyApiProperty {

	@Setter
	private String originPermitida = "http://localhost:8000";

	private final Seguranca seguranca = new Seguranca();

	@Getter
	@Setter
	public static class Seguranca {
		private boolean enableHttps;
	}

}
