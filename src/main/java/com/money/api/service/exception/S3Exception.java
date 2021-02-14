package com.money.api.service.exception;

import java.io.IOException;

public class S3Exception extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public S3Exception(String message, IOException cause) {
		super(message, cause);
	}

}
