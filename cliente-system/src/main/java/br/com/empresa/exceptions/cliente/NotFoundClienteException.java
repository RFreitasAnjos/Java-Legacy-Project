package br.com.empresa.exceptions.cliente;

import br.com.empresa.exceptions.Exceptions;

public class NotFoundClienteException extends Exceptions {
  public NotFoundClienteException(String message) {
    super(message);
  }
}
