# Guia de Contribuicao

Obrigado por dedicar seu tempo a contribuir com este projeto!
Este guia explica o processo para propor mudancas de forma organizada.

---

## Pre-requisitos

- Java 8 (JDK 8)
- Maven 3.6+
- Docker + Docker Compose
- Git

---

## Como Contribuir

### 1. Fork e clone

Faca um fork do repositorio e clone localmente:

```bash
git clone https://github.com/seu-usuario/CURD-JSF.git
cd CURD-JSF/cliente-system
```

### 2. Crie uma branch

Use nomes descritivos seguindo o padrao:

```
feat/nome-da-funcionalidade
fix/descricao-do-bug
refactor/nome-da-melhoria
test/nome-do-teste
```

```bash
git checkout -b feat/adicionar-campo-cpf
```

### 3. Implemente as mudancas

Siga as convencoes do projeto descritas abaixo antes de commitar.

### 4. Execute os testes

```bash
mvn clean test
```

Nenhum teste existente pode quebrar. Novas funcionalidades devem vir acompanhadas de testes.

### 5. Abra um Pull Request

- Descreva claramente o que foi alterado e o motivo
- Referencie a issue relacionada, se houver (`Closes #42`)
- Aguarde o review

---

## Convencoes de Codigo

### Estrutura de pacotes

```
br.com.empresa.entity      -> @Entity JPA
br.com.empresa.repository  -> @Stateless EJB (acesso a dados)
br.com.empresa.service     -> @Stateless EJB (regras de negocio)
br.com.empresa.controller  -> @Named @ViewScoped CDI (ponte JSF/Service)
br.com.empresa.dto         -> objetos de transferencia (sem anotacoes JPA)
```

Nunca chame o `Repository` diretamente a partir do `Controller`.
O fluxo obrigatorio e: `Controller -> Service -> Repository`.

### Nomenclatura

| Elemento | Convencao | Exemplo |
|---|---|---|
| Classes | PascalCase | `ClienteService` |
| Metodos | camelCase, verbo + substantivo | `buscarPorEmail()` |
| Variaveis | camelCase | `clienteSelecionado` |
| Constantes | UPPER_SNAKE_CASE | `EMAIL_MAX_LENGTH` |
| Paginas Facelets | camelCase | `listarClientes.xhtml` |

### Regras Java EE

- `@Stateless` EJBs nao devem guardar estado entre invocacoes (sem campos de instancia mutaveis)
- Use `@Inject` para injetar dependencias, nunca instancie servicos com `new`
- Transacoes sao gerenciadas pelo container JTA: nao use `EntityTransaction` manualmente
- Escopos CDI: prefira `@ViewScoped` para beans de pagina; evite `@SessionScoped` sem necessidade clara

### Encoding

Todos os arquivos devem ser salvos em **UTF-8 sem BOM**.
No PowerShell, use sempre:

```powershell
$noBom = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllText($caminho, $conteudo, $noBom)
```

---

## Relatar Bugs

Abra uma issue descrevendo:

1. O que voce tentou fazer
2. O que era esperado
3. O que aconteceu (mensagem de erro, stack trace)
4. Versao do Java, Maven e Docker utilizados