package com.example.todolist.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, code=HttpStatus.NOT_FOUND, reason="Element not found.")
public class NotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

}
