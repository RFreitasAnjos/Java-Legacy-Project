package br.com.empresa.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.empresa.entity.Produto;
import br.com.empresa.service.ProdutoService;

@Named
@ViewScoped
public class ProdutoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProdutoService service;

    private Produto produto = new Produto();
    private Produto produtoSelecionado;
    private List<Produto> produtos;
    private Long produtoId;

    @PostConstruct
    public void init() {
        carregarProdutos();
    }

    public void carregar() {
        if (produtoId != null) {
            produto = service.buscarPorId(produtoId);
        }
    }

    public String irParaLista() {
        return "/content/produtos/listarProdutos.xhtml?faces-redirect=true";
    }

    public String salvar() {
        try {
            service.salvar(produto);
            return "/content/produtos/listarProdutos.xhtml?faces-redirect=true";
        } catch (Exception e) {
            addMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao salvar: " + e.getMessage());
            return null;
        }
    }

    public String atualizar() {
        try {
            service.atualizar(produto);
            return "/content/produtos/listarProdutos.xhtml?faces-redirect=true";
        } catch (Exception e) {
            addMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao atualizar: " + e.getMessage());
            return null;
        }
    }

    public void prepararRemocao(Produto produto) {
        this.produtoSelecionado = produto;
    }

    public void confirmarRemocao() {
        if (produtoSelecionado != null) {
            try {
                service.remover(produtoSelecionado.getId());
                addMensagem(FacesMessage.SEVERITY_INFO, "Produto removido com sucesso.");
                carregarProdutos();
            } catch (Exception e) {
                addMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao remover: " + e.getMessage());
            } finally {
                produtoSelecionado = null;
            }
        }
    }

    public String prepararEdicao(Produto produto) {
        return "/content/produtos/editarProduto.xhtml?faces-redirect=true&produtoId=" + produto.getId();
    }

    public void carregarProdutos() {
        produtos = service.buscarTodos();
    }

    private void addMensagem(FacesMessage.Severity severity, String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, message, null));
    }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public List<Produto> getProdutos() { return produtos; }

    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }

    public Produto getProdutoSelecionado() { return produtoSelecionado; }
    public void setProdutoSelecionado(Produto produtoSelecionado) { this.produtoSelecionado = produtoSelecionado; }
}