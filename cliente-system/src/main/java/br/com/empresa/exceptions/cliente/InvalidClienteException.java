package br.com.empresa.exceptions.cliente;

import br.com.empresa.exceptions.Exceptions;

public class InvalidClienteException extends Exceptions {
  public InvalidClienteException(String message) {
    super(message);
  }
}
