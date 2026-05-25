package br.com.empresa.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.empresa.entity.Produto;
import br.com.empresa.repository.ProdutoRepository;
import br.com.empresa.utils.Validators;

@Stateless
public class ProdutoService {
  
  @Inject
  private ProdutoRepository repository;

  @Inject
  private Validators validator;

  public void salvar(Produto produto) {
    validator.validar(produto);
    repository.salvar(produto);
  }

  public void atualizar(Produto produto) {
    validator.validar(produto);
    repository.atualizar(produto);
  }

  public void remover(Long id) {
    try{
      if(id == null){
        throw new IllegalArgumentException("ID do produto n?o pode ser nulo");
      }
      repository.remover(id);
    } catch (Exception e) {
      throw new RuntimeException("Erro ao remover produto com ID: " + id, e);
    }
  }

  public Produto buscarPorId(Long id) {
    try{
      if(id == null){
        throw new IllegalArgumentException("ID do produto n?o pode ser nulo");
      }
      return repository.buscarPorId(id);
    } catch (Exception e) {
      throw new RuntimeException("Erro ao buscar produto por ID: " + id, e);
    }
  }

  public List<Produto> buscarTodos() {
    return repository.buscarTodos();
  }
}
