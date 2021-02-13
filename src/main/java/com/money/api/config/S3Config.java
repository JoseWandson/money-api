package com.money.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.money.api.config.property.MoneyApiProperty;

@Configuration
public class S3Config {

	@Bean
	public AmazonS3 amazonS3(MoneyApiProperty property) {
		AWSCredentials credenciais = new BasicAWSCredentials(property.getS3().getAccessKeyId(),
				property.getS3().getSecretAccessKey());
		return AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credenciais)).build();
	}

}
