package br.com.empresa.repository;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.empresa.entity.Produto;

@Stateless
public class ProdutoRepository {

    @PersistenceContext(unitName = "ClientePU")
    private EntityManager entityManager;

    public void salvar(Produto produto) {
        entityManager.persist(produto);
    }

    public void atualizar(Produto produto) {
        entityManager.merge(produto);
    }

    public void remover(Long id) {
        Produto produto = entityManager.find(Produto.class, id);
        if (produto != null) {
            entityManager.remove(produto);
        }
    }

    public Produto buscarPorId(Long id) {
        return entityManager.find(Produto.class, id);
    }

    public List<Produto> buscarTodos() {
        return entityManager.createQuery("SELECT p FROM Produto p ORDER BY p.nome", Produto.class)
            .getResultList();
    }
}