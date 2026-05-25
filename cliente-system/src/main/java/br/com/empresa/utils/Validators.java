package br.com.empresa.utils;

import java.math.BigDecimal;

import javax.enterprise.context.ApplicationScoped;

import br.com.empresa.entity.Cliente;
import br.com.empresa.entity.Produto;

@ApplicationScoped
public class Validators {

    private static final int NOME_MIN = 3;
    private static final int NOME_MAX = 100;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    public void validar(Cliente cliente) {
        validarNome(cliente.getNome());
        validarEmail(cliente.getEmail());
    }

    public void validar(Produto produto) {
        validarProduto(produto);
    }

    private void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do cliente e obrigatorio.");
        }
        if (nome.length() < NOME_MIN || nome.length() > NOME_MAX) {
            throw new IllegalArgumentException(
                "O nome do cliente deve conter entre " + NOME_MIN + " e " + NOME_MAX + " caracteres."
            );
        }
    }

    private void validarEmail(String email) {
        if (email == null || !email.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("O email do cliente e invalido.");
        }
    }

    private void validarProduto(Produto produto) {
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do produto e obrigatorio.");
        }
        if (produto.getPreco() == null || produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O preco do produto deve ser maior que zero.");
        }
        if (produto.getQuantidade() == null || produto.getQuantidade() < 0) {
            throw new IllegalArgumentException("A quantidade do produto nao pode ser negativa.");
        }
    }
}