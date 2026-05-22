# cliente-system

CRUD de clientes construido com **Java EE 8**, **JSF 2.3** e **WildFly 25**.

---

## Tecnologias Java

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 8 |
| Plataforma | Java EE 8 (Jakarta EE 8) |
| Interface Web | JSF 2.3 (Facelets) |
| Injecao de dependencia | CDI 2.0 (Weld) |
| Transacoes | EJB 3.2 Stateless + JTA |
| Persistencia | JPA 2.2 / Hibernate 5 |
| Banco de dados | PostgreSQL 15 |
| Servidor de aplicacao | WildFly 25 |
| Build | Maven 3.x (packaging WAR) |

---

## Estrutura do Projeto

```
src/
+-- main/
    +-- java/
    |   +-- br/com/empresa/
    |       +-- entity/
    |       |   +-- Cliente.java          # @Entity JPA - mapeamento da tabela clientes
    |       +-- repository/
    |       |   +-- ClienteRepository.java # @Stateless EJB - acesso a dados via EntityManager
    |       +-- service/
    |       |   +-- ClienteService.java   # @Stateless EJB - regras de negocio
    |       +-- controller/
    |           +-- ClienteBean.java      # @Named @ViewScoped CDI - ponte JSF / Service
    +-- resources/
    |   +-- META-INF/
    |       +-- persistence.xml           # Unidade de persistencia JPA (datasource JNDI, Hibernate)
    +-- webapp/
        +-- content/clientes/             # Paginas Facelets (listar, criar, editar)
        +-- templates/                    # Layout base (Facelets templates)
        +-- WEB-INF/
            +-- beans.xml                 # Ativa CDI: bean-discovery-mode=all
            +-- faces-config.xml          # Configuracao JSF (navegacao, converters)
            +-- web.xml                   # Servlet mapping: FacesServlet -> *.xhtml
```

---

## Arquitetura em Camadas

```
Browser
   |
   v  HTTP *.xhtml
[JSF - FacesServlet]
   |
   | Expression Language: #{clienteBean.salvar()}
   v
[ClienteBean]  @Named @ViewScoped  (CDI Controller)
   |
   | @Inject
   v
[ClienteService]  @Stateless  (EJB - gerencia transacoes JTA)
   |
   | @Inject
   v
[ClienteRepository]  @Stateless  (EJB - acesso a dados via EntityManager)
   |
   | @PersistenceContext
   v
[JPA / Hibernate]
   |
   | JDBC
   v
[PostgreSQL 15]
```

### Responsabilidades de cada camada

| Classe | Anotacoes | Responsabilidade |
|---|---|---|
| `ClienteBean` | `@Named` `@ViewScoped` | Recebe eventos JSF, delega ao Service, controla estado da view |
| `ClienteService` | `@Stateless` | Regras de negocio, demarcacao de transacao JTA |
| `ClienteRepository` | `@Stateless` | Operacoes CRUD via `EntityManager` |
| `Cliente` | `@Entity` | Mapeamento objeto-relacional da tabela `clientes` |

---

## Ciclo de Vida do JSF (6 Fases)

Cada requisicao `POST` de formulario percorre obrigatoriamente as 6 fases:

```
1. Restore View        -> JSF reconstroi a arvore de componentes (ComponentTree) da pagina
2. Apply Request Values -> valores do formulario HTML sao aplicados nos componentes
3. Process Validations  -> @NotNull, required="true" e validadores customizados sao executados
                           (falha aqui volta direto para a fase 6 sem chamar o bean)
4. Update Model Values  -> valores validados sao escritos nas propriedades do bean via EL
5. Invoke Application   -> action listener e chamado: #{clienteBean.salvar()}
6. Render Response      -> JSF serializa a ComponentTree para HTML e envia ao browser
```

> Em caso de erro de validacao na fase 3, as fases 4 e 5 sao ignoradas e o JSF
> renderiza novamente a pagina com as mensagens de erro (fase 6 diretamente).

---

## CDI e Escopos JSF

O `ClienteBean` usa `@ViewScoped` do CDI (`javax.faces.view.ViewScoped`):

| Escopo | Duracao | Quando usar |
|---|---|---|
| `@RequestScoped` | Uma unica requisicao HTTP | Operacoes sem estado entre requisicoes |
| `@ViewScoped` | Enquanto o usuario estiver na mesma pagina | CRUD com tabelas, modais, AJAX na mesma view |
| `@SessionScoped` | Sessao HTTP do usuario | Dados de login, carrinho de compras |
| `@ApplicationScoped` | Vida da aplicacao | Caches, configuracoes globais |

---

## JPA e Gerenciamento de Transacoes

O `persistence.xml` configura a unidade de persistencia com `transaction-type="JTA"`,
delegando o controle de transacao ao container WildFly:

```xml
<persistence-unit name="clientePU" transaction-type="JTA">
    <jta-data-source>java:jboss/datasources/ClienteDS</jta-data-source>
</persistence-unit>
```

O EJB `@Stateless` recebe automaticamente uma transacao JTA por metodo publico.
O `EntityManager` injetado via `@PersistenceContext` participa dessa transacao.

### Estados de uma Entidade JPA

```
new (transient)
   |
   | entityManager.persist()
   v
managed  <-----> entityManager.merge()  <-- detached
   |
   | fim da transacao (commit)
   v
detached
   |
   | entityManager.remove()
   v
removed
```

---

## Mapeamento da Entidade

```java
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 20)
    private String telefone;
}
```

O Hibernate cria/atualiza a tabela automaticamente via `hibernate.hbm2ddl.auto=update`.

---

## Problemas Conhecidos

### WELD-001414: Bean name is ambiguous

Ocorre quando dois arquivos `.java` definem classes com o mesmo nome simples e anotacao `@Named`.
O CDI (Weld) usa o nome simples da classe como nome do bean por padrao.

**Causa:** existencia de `src/main/java/ClienteBean.java` e
`src/main/java/br/com/empresa/controller/ClienteBean.java` simultaneamente.

**Solucao:** apenas `br/com/empresa/controller/ClienteBean.java` deve conter a implementacao.
O arquivo raiz deve ser removido ou conter somente comentarios.

### BOM (Byte Order Mark) em arquivos Java

O PowerShell com `Set-Content -Encoding UTF8` adiciona BOM (`\xEF\xBB\xBF`) que quebra
o compilador Java (`javac` nao reconhece o caracter de BOM no inicio do arquivo `.java`).

**Solucao:** escrever arquivos sem BOM:

```powershell
$noBom = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllText($path, $content, $noBom)
```

### LazyInitializationException (JPA)

Ocorre quando um relacionamento `FetchType.LAZY` e acessado fora do contexto de persistencia
(fora da transacao EJB). No JSF, o `@ViewScoped` pode tentar acessar colecoes lazy durante
a fase `Render Response`, que ocorre apos o fim da transacao do EJB.

**Solucao:** usar `FetchType.EAGER` para colecoes acessadas na view, ou abrir uma nova
transacao com `@Transactional` / `EntityManager.merge()` antes de acessar a colecao.