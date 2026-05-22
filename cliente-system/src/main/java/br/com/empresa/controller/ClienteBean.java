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
    private List<Cliente> clientes;
    private Long clienteId;

    @PostConstruct
    public void init() {
        carregarClientes();
    }

    /**
     * Chamado pelo f:viewAction na pagina editarCliente.xhtml.
     * Recebe o clienteId via f:viewParam e carrega o cliente do banco.
     */
    public void carregar() {
        if (clienteId != null) {
            cliente = service.buscarPorId(clienteId);
        }
    }

    /**
     * Rota de entrada: index.xhtml redireciona para a lista via este metodo.
     */
    public String irParaLista() {
        return "/content/clientes/listarClientes.xhtml?faces-redirect=true";
    }

    /**
     * Salva novo cliente e redireciona para a lista (padrao PRG).
     */
    public String salvar() {
        try {
            service.salvar(cliente);
            return "/content/clientes/listarClientes.xhtml?faces-redirect=true";
        } catch (Exception e) {
            addMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao salvar: " + e.getMessage());
            return null;
        }
    }

    /**
     * Atualiza cliente existente e redireciona para a lista (padrao PRG).
     */
    public String atualizar() {
        try {
            service.atualizar(cliente);
            return "/content/clientes/listarClientes.xhtml?faces-redirect=true";
        } catch (Exception e) {
            addMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao atualizar: " + e.getMessage());
            return null;
        }
    }

    /**
     * Remove cliente e recarrega a lista na mesma view.
     */
    public void remover(Long id) {
        try {
            service.remover(id);
            addMensagem(FacesMessage.SEVERITY_INFO, "Cliente removido com sucesso.");
            carregarClientes();
        } catch (Exception e) {
            addMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao remover: " + e.getMessage());
        }
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
}