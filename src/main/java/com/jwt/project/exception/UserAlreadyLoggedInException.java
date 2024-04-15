package com.jwt.project.exception;

public class UserAlreadyLoggedInException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;
    public UserAlreadyLoggedInException() {
        super();
    }

    public UserAlreadyLoggedInException(String message) {
        super(message);
    }

    public UserAlreadyLoggedInException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAlreadyLoggedInException(Throwable cause) {
        super(cause);
    }

    protected UserAlreadyLoggedInException(String message, Throwable cause,
                        boolean enableSuppression,
                        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
