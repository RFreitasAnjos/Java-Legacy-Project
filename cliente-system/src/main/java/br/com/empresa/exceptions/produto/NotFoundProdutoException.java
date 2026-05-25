package br.com.empresa.exceptions.produto;

import br.com.empresa.exceptions.Exceptions;

public class NotFoundProdutoException extends Exceptions {
  public NotFoundProdutoException(String message) {
    super(message);
  }
}