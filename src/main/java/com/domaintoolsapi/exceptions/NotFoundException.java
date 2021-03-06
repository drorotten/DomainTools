package com.domaintoolsapi.exceptions;

public class NotFoundException extends DomainToolsException {

	private static final long serialVersionUID = 1L;

	public NotFoundException(String message){
		super(message);
	}

	@Override
	public int getCode() {
		return 404;
	}
}