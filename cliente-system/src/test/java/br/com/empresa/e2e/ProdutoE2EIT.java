package br.com.empresa.e2e;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProdutoE2EIT {

    private static WebDriver driver;

    private static final String BASE_URL = System.getenv("APP_BASE_URL") != null
            ? System.getenv("APP_BASE_URL")
            : "http://localhost:8080/cliente-system";

    private static final int WAIT_SECONDS = 15;

    private static final String TEST_NOME = "Produto E2E " + System.currentTimeMillis();

    @BeforeClass
    public static void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--headless=new",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu");

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }

    @AfterClass
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void test01_deveCarregarPaginaLista() {
        driver.get(BASE_URL + "/content/produtos/listarProdutos.xhtml");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SECONDS));
        wait.until(ExpectedConditions.titleContains("Produtos"));

        assertTrue("Titulo deve conter 'Produtos'", driver.getTitle().contains("Produtos"));
        assertTrue("Pagina deve conter link 'Novo Produto'",
                driver.getPageSource().contains("Novo Produto"));
    }

    @Test
    public void test02_deveAbrirFormularioCadastro() {
        driver.get(BASE_URL + "/content/produtos/criarProduto.xhtml");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SECONDS));
        wait.until(ExpectedConditions.titleContains("Criar"));

        assertTrue("Titulo deve conter 'Criar'", driver.getTitle().contains("Criar"));
        assertTrue("Formulario de cadastro deve estar presente",
                driver.findElements(By.id("formCadastro:nome")).size() > 0);
    }

    @Test
    public void test03_deveCadastrarNovoProdutoComSucesso() {
        driver.get(BASE_URL + "/content/produtos/criarProduto.xhtml");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SECONDS));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("formCadastro:nome")));

        driver.findElement(By.id("formCadastro:nome")).sendKeys(TEST_NOME);
        driver.findElement(By.id("formCadastro:preco")).sendKeys("99.90");
        driver.findElement(By.id("formCadastro:quantidade")).sendKeys("10");
        driver.findElement(By.id("formCadastro:descricao")).sendKeys("Produto criado via E2E");
        driver.findElement(By.cssSelector("#formCadastro input[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("listarProdutos"));

        assertTrue("Deve redirecionar para lista apos salvar",
                driver.getCurrentUrl().contains("listarProdutos"));
        assertTrue("Produto cadastrado deve aparecer na lista",
                driver.getPageSource().contains(TEST_NOME));
    }

    @Test
    public void test04_deveAbrirFormularioEdicao() {
        driver.get(BASE_URL + "/content/produtos/listarProdutos.xhtml");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SECONDS));
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("(//button[contains(.,'Editar')])[1]")));

        driver.findElement(By.xpath("(//button[contains(.,'Editar')])[1]")).click();

        wait.until(ExpectedConditions.titleContains("Editar"));

        assertTrue("Titulo deve conter 'Editar'",
                driver.getTitle().contains("Editar"));
        assertTrue("Formulario de edicao deve estar presente",
                driver.findElements(By.id("formEdicao:nome")).size() > 0);
    }
}