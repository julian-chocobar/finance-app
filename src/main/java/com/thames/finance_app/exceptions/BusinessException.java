package com.thames.finance_app.exceptions;

public class BusinessException extends RuntimeException {
    
	private static final long serialVersionUID = 1L; // ID de versión para la serialización

	
	public BusinessException(String message) {
        super(message);
    }
}
