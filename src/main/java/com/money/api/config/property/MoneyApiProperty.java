package com.money.api.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@ConfigurationProperties("money")
public class MoneyApiProperty {

	@Setter
	private String originPermitida = "http://localhost:4200";

	private final Seguranca seguranca;

	private final Mail mail;

	private final S3 s3;

	public MoneyApiProperty() {
		seguranca = new Seguranca();
		mail = new Mail();
		s3 = new S3();
	}

	@Getter
	@Setter
	public static class Seguranca {
		private boolean enableHttps;
	}

	@Getter
	@Setter
	public static class Mail {
		private String host;
		private Integer port;
		private String username;
		private String password;
	}

	@Getter
	@Setter
	public static class S3 {
		private String accessKeyId;
		private String secretAccessKey;
	}

}
