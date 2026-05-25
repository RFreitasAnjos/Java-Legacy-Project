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
            ? System.getenv("APP_BASE_URL") : "http://localhost:8080/cliente-system";
    private static final int WAIT_SECONDS = 15;
    private static final String TEST_NOME = "Produto_E2E_" + System.currentTimeMillis();

    @BeforeClass
    public static void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu");
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }

    @AfterClass
    public static void tearDown() {
        if (driver != null) { driver.quit(); }
    }

    @Test
    public void test01_deveCarregarPaginaLista() {
        driver.get(BASE_URL + "/content/produtos/listarProdutos.xhtml");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SECONDS));
        wait.until(ExpectedConditions.titleContains("Produtos"));
        assertTrue(driver.getTitle().contains("Produtos"));
        assertTrue(driver.getPageSource().contains("Produtos Cadastrados"));
    }

    @Test
    public void test02_deveAbrirFormularioCadastro() {
        driver.get(BASE_URL + "/content/produtos/criarProduto.xhtml");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SECONDS));
        wait.until(ExpectedConditions.titleContains("Produtos - Criar"));
        assertTrue(driver.getTitle().contains("Produtos - Criar"));
        assertTrue(driver.findElements(By.id("formCadastro:nome")).size() > 0);
    }

    @Test
    public void test03_deveCadastrarNovoProdutoComSucesso() {
        driver.get(BASE_URL + "/content/produtos/criarProduto.xhtml");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SECONDS));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("formCadastro:nome")));
        driver.findElement(By.id("formCadastro:nome")).sendKeys(TEST_NOME);
        driver.findElement(By.id("formCadastro:preco")).sendKeys("99.90");
        driver.findElement(By.id("formCadastro:quantidade")).sendKeys("10");
        driver.findElement(By.id("formCadastro:descricao")).sendKeys("Descricao E2E");
        driver.findElement(By.cssSelector("#formCadastro input[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("listarProdutos"));
        assertTrue(driver.getCurrentUrl().contains("listarProdutos"));
        assertTrue(driver.getPageSource().contains(TEST_NOME));
    }

    @Test
    public void test04_deveAbrirFormularioEdicao() {
        driver.get(BASE_URL + "/content/produtos/listarProdutos.xhtml");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SECONDS));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("(//button[contains(.,'Editar')])[1]")));
        driver.findElement(By.xpath("(//button[contains(.,'Editar')])[1]")).click();
        wait.until(ExpectedConditions.titleContains("Produtos - Editar"));
        assertTrue(driver.getTitle().contains("Produtos - Editar"));
        assertTrue(driver.findElements(By.id("formEdicao:nome")).size() > 0);
    }
}

