package br.com.empresa.controller;

import br.com.empresa.entity.Cliente;
import br.com.empresa.service.ClienteService;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@ViewScoped
public class ClienteBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ClienteService service;

    private Cliente cliente = new Cliente();
    private Cliente clienteSelecionado;
    private List<Cliente> clientes;
    private Long clienteId;

    @PostConstruct
    public void init() {
        carregarClientes();
    }

    public void carregar() {
        if (clienteId != null) {
            cliente = service.buscarPorId(clienteId);
        }
    }

    public String irParaLista() {
        return "/content/clientes/listarClientes.xhtml?faces-redirect=true";
    }

    public String salvar() {
        try {
            service.salvar(cliente);
            return "/content/clientes/listarClientes.xhtml?faces-redirect=true";
        } catch (Exception e) {
            addMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao salvar: " + e.getMessage());
            return null;
        }
    }

    public String atualizar() {
        try {
            service.atualizar(cliente);
            return "/content/clientes/listarClientes.xhtml?faces-redirect=true";
        } catch (Exception e) {
            addMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao atualizar: " + e.getMessage());
            return null;
        }
    }

    public void remover(Long id) {
        try {
            service.remover(id);
            addMensagem(FacesMessage.SEVERITY_INFO, "Cliente removido com sucesso.");
            carregarClientes();
        } catch (Exception e) {
            addMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao remover: " + e.getMessage());
        }
    }

    public void prepararRemocao(Cliente cliente) {
        this.clienteSelecionado = cliente;
    }

    public void confirmarRemocao() {
        if (clienteSelecionado != null) {
            remover(clienteSelecionado.getId());
            clienteSelecionado = null;
        }
    }

    public String prepararEdicao(Cliente cliente) {
        return "/content/clientes/editarCliente.xhtml?faces-redirect=true&clienteId=" + cliente.getId();
    }

    private void carregarClientes() {
        this.clientes = service.buscarTodos();
    }

    private void addMensagem(FacesMessage.Severity severity, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, texto, null));
    }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public List<Cliente> getClientes() { return clientes; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Cliente getClienteSelecionado() { return clienteSelecionado; }
    public void setClienteSelecionado(Cliente clienteSelecionado) { this.clienteSelecionado = clienteSelecionado; }
}