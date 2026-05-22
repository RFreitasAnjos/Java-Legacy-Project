package br.com.empresa.service;

import br.com.empresa.entity.Cliente;
import br.com.empresa.repository.ClienteRepository;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class ClienteService {

    @Inject
    private ClienteRepository repository;

    public void salvar(Cliente cliente) {
        repository.salvar(cliente);
    }

    public void atualizar(Cliente cliente) {
        repository.atualizar(cliente);
    }

    public void remover(Long id) {
        repository.remover(id);
    }

    public Cliente buscarPorId(Long id) {
        return repository.buscarPorId(id);
    }

    public List<Cliente> buscarTodos() {
        return repository.buscarTodos();
    }
}
