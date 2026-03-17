import unittest
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from pyunitreport import HTMLTestRunner

class loginTest(unittest.TestCase):
    
    # Esta funcion se ejecuta antes de CADA prueba
    def setUp(self):
        self.driver = webdriver.Chrome()

    # Esta funcion se ejecuta solo una vez antes de TODAS las prubeas
    # @classmethod
    # def setUpClass(cls):
    #     cls.driver = webdriver.Chrome()
    
    def test_login(self):
        driver = self.driver
        # Se dirige a la pagina
        driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login")
        driver.implicitly_wait(10)

        # Obtiene el nodo html
        inputUsername = driver.find_element(By.NAME, "username")

        # Asigna valor al nodo
        inputUsername.send_keys("Admin")
        inputPassword = driver.find_element(By.NAME, "password")
        inputPassword.send_keys("admin123")

        # Este submit es como hacer un enter en el input de contraseña
        inputPassword.submit()
        
        # Este wait espera 10 segundos o hasta que la url contenga '/dashboard' si llegaran
        # a pasar mas de 10 segundos lanzaria un error TimeoutException
        WebDriverWait(driver, 10).until(EC.url_contains("/dashboard"))
        
        self.assertIn("/dashboard", driver.current_url)
        
    def test_login_invalid(self):
        driver = self.driver
        driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login")
        driver.implicitly_wait(10)
        inputUsername = driver.find_element(By.NAME, "username")
        inputUsername.send_keys("Admon")
        inputPassword = driver.find_element(By.NAME, "password")
        inputPassword.send_keys("admin456")
        inputPassword.submit()
        
        self.assertIn("/login", driver.current_url)
        
        
    # Esta funcion se ejecuta despues de cada prueba
    def tearDown(self):
        self.driver.quit()

    # Esta funcion se ejecuta al final de TODAS las pruebas
    # @classmethod
    # def tearDownClass(cls):
    #     cls.driver.quit()

# Esto es importante no olvidarlo ya que es lo que ejecuta las pruebas en si,
# si lo omites solo tienes una clase.
if(__name__=="__main__"):
    unittest.main(testRunner=HTMLTestRunner(output='Login'))