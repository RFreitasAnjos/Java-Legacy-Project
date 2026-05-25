package br.com.empresa.exceptions;

public abstract class Exceptions extends Exception {

    public Exceptions() {
        super();
    }

    public Exceptions(String message) {
        super(message);
    }

    public Exceptions(String message, Throwable cause) {
        super(message, cause);
    }
}