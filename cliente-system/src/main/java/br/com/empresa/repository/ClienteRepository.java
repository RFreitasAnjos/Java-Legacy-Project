package br.com.empresa.repository;

import br.com.empresa.entity.Cliente;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ClienteRepository {

    @PersistenceContext(unitName = "ClientePU")
    private EntityManager em;

    public void salvar(Cliente cliente) {
        em.persist(cliente);
    }

    public void atualizar(Cliente cliente) {
        em.merge(cliente);
    }

    public void remover(Long id) {
        Cliente cliente = em.find(Cliente.class, id);
        if (cliente != null) {
            em.remove(cliente);
        }
    }

    public Cliente buscarPorId(Long id) {
        return em.find(Cliente.class, id);
    }

    public List<Cliente> buscarTodos() {
        return em.createQuery("SELECT c FROM Cliente c ORDER BY c.nome", Cliente.class)
                 .getResultList();
    }
}
