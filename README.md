# cliente-system

CRUD de clientes construido com **Java EE 8**, **JSF 2.x** e **WildFly 25**, utilizando Docker para orquestrar toda a infraestrutura.

---

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 8 |
| Plataforma | Java EE 8 (Jakarta EE 8) |
| Interface Web | JSF 2.3 (Facelets) |
| Injecao de dependencia | CDI (Weld) |
| Transacoes | EJB Stateless |
| Persistencia | JPA 2.2 / Hibernate |
| Banco de dados | PostgreSQL 15 |
| Servidor de aplicacao | WildFly 25 |
| Build | Maven 3.x |
| Infraestrutura | Docker + Docker Compose |

---

## Estrutura do Projeto

`
cliente-system/
+-- pom.xml                          # Descritores Maven (dependencias, build, packaging WAR)
+-- docker/
|   +-- docker-compose.yml           # Orquestracao dos containers
|   +-- wildfly/
|       +-- Dockerfile               # Imagem WildFly customizada com modulo PostgreSQL
|       +-- configure-datasource.cli # CLI do WildFly: registra driver e datasource via embed-server
+-- src/
    +-- main/
        +-- java/
        |   +-- br/com/empresa/
        |       +-- entity/
        |       |   +-- Cliente.java          # Entidade JPA (@Entity)
        |       +-- repository/
        |       |   +-- ClienteRepository.java # EJB Stateless + EntityManager (@PersistenceContext)
        |       +-- service/
        |       |   +-- ClienteService.java   # EJB Stateless com regras de negocio
        |       +-- controller/
        |           +-- ClienteBean.java      # CDI Bean (@Named @ViewScoped) - ponte JSF/Service
        +-- resources/
        |   +-- META-INF/
        |       +-- persistence.xml           # Configuracao JPA (datasource JNDI, Hibernate)
        +-- webapp/
            +-- index.xhtml                   # Pagina CRUD (Facelets/JSF)
            +-- WEB-INF/
                +-- beans.xml                 # Ativa CDI com bean-discovery-mode=all
                +-- faces-config.xml          # Configuracao JSF
                +-- web.xml                   # Servlet mapping (FacesServlet -> *.xhtml)
                +-- jboss-web.xml             # Context root: /cliente-system
                +-- jboss-deployment-structure.xml # Modulos WildFly (JSF)
`

---

## Arquitetura em Camadas

`
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
   | JDBC (modulo WildFly: org.postgresql)
   v
[PostgreSQL 15]
`

---

## Fluxo de uma Requisicao JSF (6 Fases)

1. **Restore View** - JSF reconstroi a arvore de componentes da pagina
2. **Apply Request Values** - valores do formulario sao aplicados nos componentes
3. **Process Validations** - 
equired="true" e validadores sao executados
4. **Update Model Values** - valores validos sao escritos no bean (cliente.nome, etc.)
5. **Invoke Application** - metodo da action e chamado (#{clienteBean.salvar})
6. **Render Response** - JSF renderiza o HTML de resposta

---

## Configuracao do Docker

### Containers

| Container | Imagem | Porta |
|---|---|---|
| postgres_cliente_system | postgres:15 | 5432 |
| cliente_system_builder | maven:3.9.6-eclipse-temurin-8 | - |
| wildfly_cliente_system | docker-wildfly (build local) | 8080, 9990 |

### Dependencias de inicializacao

`
postgres (healthcheck: pg_isready)
   |
   +-- builder (mvn clean package) - service_completed_successfully
   |
   +-- wildfly - aguarda postgres healthy + builder concluido
`

### Volumes Docker

| Volume | Proposito |
|---|---|
| maven_cache | Cache do repositorio Maven local (~/.m2) entre rebuilds |
| wildfly_xml_history | Historico de configuracoes XML do WildFly (evita WFLYCTL0056) |

---

## Como Executar

### Pre-requisitos

- Docker com suporte a WSL2 (ou Docker Desktop)
- Java 8 e Maven instalados localmente (opcional, o builder container supre)

### 1. Build e subir todos os containers

`ash
# No WSL ou terminal com Docker disponivel
cd docker
docker compose up -d --build
`

O --build reconstroi a imagem do WildFly (necessario na primeira vez ou apos mudancas no Dockerfile).

### 2. Rebuild apos alteracoes no codigo Java

`ash
docker compose up -d --force-recreate
`

Isso recria todos os containers: roda o Maven novamente e reinicia o WildFly com o novo WAR.

### 3. Verificar status dos containers

`ash
docker compose ps
docker logs wildfly_cliente_system
`

### 4. Acessar a aplicacao

| URL | Descricao |
|---|---|
| http://localhost:8080/cliente-system | Aplicacao CRUD |
| http://localhost:9990 | Console Admin WildFly |

---

## Datasource WildFly

O datasource e configurado via docker/wildfly/configure-datasource.cli usando o modo embed-server do CLI do WildFly durante o build da imagem Docker:

1. O driver PostgreSQL e baixado via curl e instalado como **modulo WildFly** (org.postgresql)
2. O driver e registrado no subsistema datasources
3. O datasource ClienteDS e criado com JNDI java:jboss/datasources/ClienteDS
4. O persistence.xml referencia esse JNDI com 	ransaction-type="JTA"

Isso permite que os EJBs (@Stateless) gerenciem transacoes automaticamente via container JTA sem nenhuma configuracao adicional no codigo.

---

## Banco de Dados

A tabela clientes e criada/atualizada automaticamente pelo Hibernate (hibernate.hbm2ddl.auto=update).

`sql
CREATE TABLE clientes (
    id       BIGSERIAL PRIMARY KEY,
    nome     VARCHAR(100) NOT NULL,
    email    VARCHAR(100) NOT NULL UNIQUE,
    telefone VARCHAR(20)
);
`

---

## Parar os Containers

`ash
docker compose down          # para e remove os containers
docker compose down -v       # para, remove containers E volumes
`

---

## Problemas Conhecidos e Solucoes

### BOM (Byte Order Mark) em arquivos Java/XML

O PowerShell com Set-Content -Encoding UTF8 adiciona BOM (\xEF\xBB\xBF) que quebra o compilador Java e o WildFly CLI. Use sempre:

`powershell
$noBom = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllText($path, $content, $noBom)
`

Ou remova o BOM existente via WSL:

`ash
find src/ -name "*.java" -exec sed -i 's/\xef\xbb\xbf//' {} ;
`

### WELD-001414: Bean name is ambiguous

Ocorre quando dois arquivos .java definem classes com o mesmo nome e anotacao @Named. Garanta que apenas src/main/java/br/com/empresa/controller/ClienteBean.java contenha a definicao da classe. O arquivo src/main/java/ClienteBean.java deve conter apenas comentarios.

### WFLYCTL0056: Could not rename xml_history

WildFly nao consegue arquivar o historico de configuracoes XML quando o diretorio current/ ja tem conteudo de boots anteriores. Resolvido com o volume Docker wildfly_xml_history.

---

## Executando os Testes Localmente

> **Pre-requisito:** todos os comandos abaixo devem ser executados dentro do WSL,
> pois o Docker e os containers rodam no ambiente Linux do WSL2.
>
> Acesse o WSL e navegue ate o modulo:
> ```bash
> wsl
> cd /mnt/c/Users/<seu-usuario>/Documents/Projetos-Pessoais/CURD-JSF/cliente-system
> ```

---

### Lint (Checkstyle · PMD · SpotBugs)

Cada ferramenta pode ser executada isoladamente ou em sequencia.
Nenhum container e necessario — a analise ocorre apenas sobre o codigo-fonte.

```bash
# Checkstyle: convencoes de estilo (regras em checkstyle.xml)
mvn checkstyle:check

# PMD: analise estatica de codigo (regras em pmd.xml)
mvn pmd:check

# SpotBugs: deteccao de padroes de bug (threshold: High)
mvn spotbugs:check
```

Em caso de violacao, o build falha e o motivo e impresso no console.
Para apenas gerar o relatorio sem falhar o build, substitua `check` por `checkstyle:checkstyle`, `pmd:pmd` e `spotbugs:spotbugs`.

---

### Testes Unitarios

Executados pelo Maven Surefire. Incluem todos os arquivos `*Test.java`
e excluem `*IT.java` (reservados para E2E). Nenhum container e necessario.

```bash
# Roda os testes e gera o relatorio de cobertura JaCoCo
mvn clean test jacoco:report
```

| Artefato | Localizacao |
|---|---|
| Resultado dos testes | `target/surefire-reports/` |
| Relatorio de cobertura HTML | `target/site/jacoco/index.html` |

Para abrir o relatorio de cobertura no navegador (Windows, a partir do PowerShell):

```powershell
Start-Process "target\site\jacoco\index.html"
```

---

### Testes E2E (Selenium + WildFly + PostgreSQL)

Os testes E2E sobem o ambiente completo via Docker Compose e usam Selenium
para interagir com a aplicacao no navegador. O perfil Maven `e2e` aciona o
Failsafe Plugin, que executa apenas os arquivos `*IT.java`.

#### 1. Subir o ambiente

```bash
cd docker
docker compose up -d --build
```

Aguarde a aplicacao estar disponivel antes de rodar os testes:

```bash
until curl -sf http://localhost:8080/cliente-system/content/clientes/listarClientes.xhtml \
  -o /dev/null; do
  echo "Aguardando aplicacao..."; sleep 5
done
echo "Aplicacao disponivel."
```

#### 2. Executar os testes E2E

```bash
# Volte para a raiz do modulo
cd ..

APP_BASE_URL=http://localhost:8080/cliente-system \
  mvn verify -Pe2e -Dsurefire.skip=true
```

A variavel `APP_BASE_URL` informa ao Selenium qual endpoint apontar.

| Artefato | Localizacao |
|---|---|
| Resultado Failsafe | `target/failsafe-reports/` |
| Screenshots de falha | `target/screenshots/` |

#### 3. Derrubar o ambiente apos os testes

```bash
cd docker
docker compose down -v
```

---

### Resumo dos Comandos

| Etapa | Comando | Container necessario |
|---|---|---|
| Checkstyle | `mvn checkstyle:check` | Nao |
| PMD | `mvn pmd:check` | Nao |
| SpotBugs | `mvn spotbugs:check` | Nao |
| Testes unitarios | `mvn clean test jacoco:report` | Nao |
| E2E | `mvn verify -Pe2e -Dsurefire.skip=true` | Sim (docker compose up) |