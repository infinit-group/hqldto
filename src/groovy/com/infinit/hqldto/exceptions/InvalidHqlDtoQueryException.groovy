package com.infinit.hqldto.exceptions

/**
 * Exception thrown by the hqldto plugin when parsing a HQL query that cannot be used to generate a HqlDto
 */
class InvalidHqlDtoQueryException extends RuntimeException {
	InvalidHqlDtoQueryException() {
		super()
	}

	InvalidHqlDtoQueryException(String message) {
		super(message)
	}

	InvalidHqlDtoQueryException(String message, Throwable cause) {
		super(message, cause)
	}

	InvalidHqlDtoQueryException(Throwable cause) {
		super(cause)
	}
}
