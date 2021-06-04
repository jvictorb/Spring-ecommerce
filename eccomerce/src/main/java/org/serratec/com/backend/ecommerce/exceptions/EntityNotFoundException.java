package org.serratec.com.backend.ecommerce.exceptions;

public class EntityNotFoundException extends Exception {

	private static final long serialVersionUID = -7586785837020747782L;


	private String msg;

	public EntityNotFoundException() {
		super();
	}

	public EntityNotFoundException(String message) {
		super(message);
		this.msg = message;
	}

	public String getMsg() {
		return msg;
	}
}
