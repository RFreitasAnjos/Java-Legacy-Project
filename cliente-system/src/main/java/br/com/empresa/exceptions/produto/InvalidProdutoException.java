package br.com.empresa.exceptions.produto;

import br.com.empresa.exceptions.Exceptions;

public class InvalidProdutoException extends Exceptions{
  public InvalidProdutoException(String message) {
    super(message);
  }
}
