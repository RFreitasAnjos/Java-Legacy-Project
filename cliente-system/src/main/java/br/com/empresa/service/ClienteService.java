package br.com.empresa.service;

import br.com.empresa.entity.Cliente;
import br.com.empresa.repository.ClienteRepository;
import br.com.empresa.utils.Validators;

import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class ClienteService {

    @Inject
    private ClienteRepository repository;

    @Inject
    private Validators validator;

    public void salvar(Cliente cliente) {
        validator.validar(cliente);
        repository.salvar(cliente);
    }

    public void atualizar(Cliente cliente) {
        validator.validar(cliente);
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
