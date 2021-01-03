package com.money.api.service.exception;

import javax.mail.MessagingException;

public class MailerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MailerException(String message, MessagingException cause) {
		super(message, cause);
	}

}
