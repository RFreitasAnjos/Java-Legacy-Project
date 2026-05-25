package br.com.empresa.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.empresa.entity.Produto;
import br.com.empresa.repository.ProdutoRepository;
import br.com.empresa.utils.Validators;

public class ProdutoServiceTest {

    @Mock
    private ProdutoRepository repository;

    @Mock
    private Validators validator;

    @InjectMocks
    private ProdutoService service;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void deveSalvarProduto() {
        Produto produto = new Produto();
        produto.setNome("Notebook");
        produto.setPreco(new BigDecimal("3500.00"));
        produto.setQuantidade(10);

        service.salvar(produto);

        verify(validator, times(1)).validar(produto);
        verify(repository, times(1)).salvar(produto);
    }

    @Test
    public void deveAtualizarProduto() {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Mouse");
        produto.setPreco(new BigDecimal("150.00"));
        produto.setQuantidade(5);

        service.atualizar(produto);

        verify(validator, times(1)).validar(produto);
        verify(repository, times(1)).atualizar(produto);
    }

    @Test
    public void deveRemoverProduto() {
        service.remover(1L);

        verify(repository, times(1)).remover(1L);
    }

    @Test(expected = RuntimeException.class)
    public void deveLancarExcecaoAoRemoverComIdNulo() {
        service.remover(null);
    }

    @Test
    public void deveBuscarTodosProdutos() {
        Produto p1 = new Produto();
        p1.setNome("Teclado");
        Produto p2 = new Produto();
        p2.setNome("Monitor");
        when(repository.buscarTodos()).thenReturn(Arrays.asList(p1, p2));

        List<Produto> resultado = service.buscarTodos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(repository, times(1)).buscarTodos();
    }

    @Test
    public void deveBuscarProdutoPorId() {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Headset");
        when(repository.buscarPorId(1L)).thenReturn(produto);

        Produto resultado = service.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(Long.valueOf(1L), resultado.getId());
        assertEquals("Headset", resultado.getNome());
        verify(repository, times(1)).buscarPorId(1L);
    }

    @Test(expected = RuntimeException.class)
    public void deveLancarExcecaoAoBuscarComIdNulo() {
        service.buscarPorId(null);
    }
}