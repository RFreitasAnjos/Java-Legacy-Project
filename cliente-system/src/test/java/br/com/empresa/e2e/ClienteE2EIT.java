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

import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ClienteE2EIT {

    private static WebDriver driver;

    private static final String BASE_URL = System.getenv("APP_BASE_URL") != null
            ? System.getenv("APP_BASE_URL")
            : "http://localhost:8080/cliente-system";

    private static final int WAIT_SECONDS = 15;

    private static final String TEST_EMAIL = "e2e_" + System.currentTimeMillis() + "@empresa.com";

    @BeforeClass
    public static void setUp() {
        try {
            WebDriverManager.chromedriver().setup();
        } catch (Exception e) {
            System.out.println("[E2E] WebDriverManager falhou, usando driver do sistema: " + e.getMessage());
        }
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu");
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
        driver.get(BASE_URL + "/content/clientes/listarClientes.xhtml");

        WebDriverWait wait = new WebDriverWait(driver, WAIT_SECONDS);
        wait.until(ExpectedConditions.titleContains("Clientes"));

        assertTrue("Titulo deve conter 'Clientes'", driver.getTitle().contains("Clientes"));
        assertTrue("Pagina deve conter link 'Novo Cliente'",
                driver.getPageSource().contains("Novo Cliente"));
    }

    @Test
    public void test02_deveAbrirFormularioCadastro() {
        driver.get(BASE_URL + "/content/clientes/criarCliente.xhtml");

        WebDriverWait wait = new WebDriverWait(driver, WAIT_SECONDS);
        wait.until(ExpectedConditions.titleContains("Novo Cliente"));

        assertTrue("Titulo deve conter 'Novo Cliente'", driver.getTitle().contains("Novo Cliente"));
        assertTrue("Formulario de cadastro deve estar presente",
                driver.findElements(By.id("formCadastro:nome")).size() > 0);
    }

    @Test
    public void test03_deveCadastrarNovoClienteComSucesso() {
        driver.get(BASE_URL + "/content/clientes/criarCliente.xhtml");

        WebDriverWait wait = new WebDriverWait(driver, WAIT_SECONDS);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("formCadastro:nome")));

        driver.findElement(By.id("formCadastro:nome")).sendKeys("Cliente E2E");
        driver.findElement(By.id("formCadastro:email")).sendKeys(TEST_EMAIL);
        driver.findElement(By.id("formCadastro:telefone")).sendKeys("11999990001");
        driver.findElement(By.cssSelector("#formCadastro input[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("listarClientes"));

        assertTrue("Deve redirecionar para lista apos salvar",
                driver.getCurrentUrl().contains("listarClientes"));
        assertTrue("Cliente cadastrado deve aparecer na lista",
                driver.getPageSource().contains("Cliente E2E"));
    }

    @Test
    public void test04_deveAbrirFormularioEdicao() {
        driver.get(BASE_URL + "/content/clientes/listarClientes.xhtml");

        WebDriverWait wait = new WebDriverWait(driver, WAIT_SECONDS);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText("Editar")));

        driver.findElement(By.linkText("Editar")).click();

        wait.until(ExpectedConditions.titleContains("Editar Cliente"));

        assertTrue("Titulo deve conter 'Editar Cliente'",
                driver.getTitle().contains("Editar Cliente"));
        assertTrue("Formulario de edicao deve estar presente",
                driver.findElements(By.id("formEdicao:nome")).size() > 0);
    }
}