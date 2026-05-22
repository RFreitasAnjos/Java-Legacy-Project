package br.com.empresa.service;

import br.com.empresa.entity.Cliente;
import br.com.empresa.repository.ClienteRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteService service;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void deveSalvarCliente() {
        Cliente cliente = new Cliente();
        cliente.setNome("Joao Silva");
        cliente.setEmail("joao@empresa.com");

        service.salvar(cliente);

        verify(repository, times(1)).salvar(cliente);
    }

    @Test
    public void deveAtualizarCliente() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Maria Souza");
        cliente.setEmail("maria@empresa.com");

        service.atualizar(cliente);

        verify(repository, times(1)).atualizar(cliente);
    }

    @Test
    public void deveRemoverCliente() {
        service.remover(1L);

        verify(repository, times(1)).remover(1L);
    }

    @Test
    public void deveBuscarTodosClientes() {
        Cliente c1 = new Cliente();
        c1.setNome("Ana");
        Cliente c2 = new Cliente();
        c2.setNome("Carlos");
        when(repository.buscarTodos()).thenReturn(Arrays.asList(c1, c2));

        List<Cliente> resultado = service.buscarTodos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(repository, times(1)).buscarTodos();
    }

    @Test
    public void deveBuscarClientePorId() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Pedro");
        when(repository.buscarPorId(1L)).thenReturn(cliente);

        Cliente resultado = service.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(Long.valueOf(1L), resultado.getId());
        assertEquals("Pedro", resultado.getNome());
    }

    @Test
    public void deveBuscarClientePorIdInexistenteRetornandoNull() {
        when(repository.buscarPorId(99L)).thenReturn(null);

        Cliente resultado = service.buscarPorId(99L);

        assertNull(resultado);
    }

    @Test
    public void deveBuscarTodosRetornandoListaVazia() {
        when(repository.buscarTodos()).thenReturn(Arrays.asList());

        List<Cliente> resultado = service.buscarTodos();

        assertNotNull(resultado);
        assertEquals(0, resultado.size());
    }
}