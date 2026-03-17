import unittest
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from pyunitreport import HTMLTestRunner

# Prueba mas compleja de agregar un empleado a PIM
class addPIMTest(unittest.TestCase):

    def setUp(self):
        self.driver = webdriver.Chrome()
        self.driver.implicitly_wait(10)
        driver = self.driver
        driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login")
        # login
        usernameInput = driver.find_element(By.NAME, "username")
        usernameInput.send_keys("Admin")
        passwordInput = driver.find_element(By.NAME, "password")
        passwordInput.send_keys("admin123")
        passwordInput.submit()
        WebDriverWait(driver, 10).until(EC.url_contains("/dashboard"))

        # Se dirige a la seccion de PIM
        pimButton = driver.find_element(By.XPATH, "//a[contains(@href,'viewPimModule')]")
        pimButton.click()
        
        # Busca con XPath el boton de añadir y le da click
        driver.find_element(
        By.XPATH,
            "//button[contains(.,'Add')]"
        ).click()

    def test_addPIM(self):
        driver = self.driver
        firstName = "Brianda"
        lastName = "Campoy"

        # Ya en el formulario busca los inputs e inserta los datos
        firstNameInput = driver.find_element(By.NAME, "firstName")
        firstNameInput.send_keys(firstName)
        
        lastNameInput = driver.find_element(By.NAME, "lastName")
        lastNameInput.send_keys(lastName)

        # Busca el boton de submit y le da click
        driver.find_element(By.XPATH, "//button[@type='submit']").click()

        WebDriverWait(driver,15).until(
            EC.url_contains("/viewPersonalDetails")
        )

        # Ya en la vista de PersonalDetails se asegura que haya un componente que señala que se
        # trata del usuario creado
        self.assertIn("/viewPersonalDetails", driver.current_url)     
        nameuser = driver.find_element(By.XPATH, f"//h6[text()='{firstName} {lastName}']")
        self.assertIsNotNone(nameuser)

    def test_addPIMNoValues(self):
        driver = self.driver
        driver.find_element(By.XPATH, "//button[@type='submit']").click()

        # Al haber fallado el formulario el navegador debe permanecer en la misma pagina
        self.assertIn("/addEmployee", driver.current_url)     

    def test_addPIMWithLoginDetails(self):
        driver = self.driver
        firstName = "Brianda"
        lastName = "Campoy"

        # Ya en el formulario busca los inputs e inserta los datos
        firstNameInput = driver.find_element(By.NAME, "firstName")
        firstNameInput.send_keys(firstName)
        
        lastNameInput = driver.find_element(By.NAME, "lastName")
        lastNameInput.send_keys(lastName)

        checkbox = driver.find_element(By.XPATH, "//*[@id='app']/div[1]/div[2]/div[2]/div/div/form/div[1]/div[2]/div[2]/div/label")
        if not checkbox.is_selected():
            checkbox.click()
        
        # Busca el boton de submit y le da click
        driver.find_element(By.XPATH, "//button[@type='submit']").click()

    def tearDown(self):
        self.driver.quit()

if __name__ == "__main__":
    unittest.main(testRunner=HTMLTestRunner(output='AddPIM'))