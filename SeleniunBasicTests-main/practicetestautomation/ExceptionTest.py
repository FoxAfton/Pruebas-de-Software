import unittest
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from pyunitreport import HTMLTestRunner
from selenium.common.exceptions import TimeoutException


class ExceptionTest (unittest.TestCase):
    def setUp(self):
        self.driver = webdriver.Chrome()
        
    def test_exception_NoSuchElementException(self):
        driver = self.driver
        driver.get("https://practicetestautomation.com/practice-test-exceptions/")
        driver.implicitly_wait(10)
        
        driver.find_element(By.NAME, "Add").click()
        
        Verifiy = WebDriverWait(driver, 100).until(
            EC.visibility_of_element_located((By.ID, "row2"))
        )
        self.assertTrue(Verifiy.is_enabled())
        
    def test_ElementNotInteractableException(self):
        self.driver.get("https://practicetestautomation.com/practice-test-exceptions/")
        self.driver.implicitly_wait(10)
        self.driver.find_element(By.ID, "add_btn").click()

        Verifiy = WebDriverWait(self.driver, 100).until(
        EC.visibility_of_element_located((By.ID, "row2"))
        )
        self.driver.find_element(By.CSS_SELECTOR,"#row2 .input-field").send_keys("Hamburguesas")
        self.driver.find_element(By.CSS_SELECTOR, "#row2 #save_btn").click()
        confirmation = WebDriverWait(self.driver, 10).until(
            EC.visibility_of_element_located((By.ID, "confirmation"))
        )
        self.assertTrue(Verifiy.is_enabled())

    def test_InvalidElementStateException(self):
        self.driver.get("https://practicetestautomation.com/practice-test-exceptions/")
        self.driver.implicitly_wait(10)
        self.driver.find_element(By.ID, "edit_btn").click()
        self.driver.find_element(By.CSS_SELECTOR, "#row1 .input-field").clear()
        self.driver.find_element(By.CSS_SELECTOR, "#row1 .input-field").send_keys("Soda")
        self.driver.find_element(By.ID, "save_btn").click()
        self.driver.implicitly_wait(10)
        confirmation = WebDriverWait(self.driver, 10).until(EC.visibility_of_element_located((By.ID, "confirmation")))
        self.assertTrue(confirmation.is_enabled())

    def test_StaleElementReferenceException(self):
        self.driver.get("https://practicetestautomation.com/practice-test-exceptions/")

        instructions = self.driver.find_element(By.ID, "instructions")

        self.driver.find_element(By.ID, "add_btn").click()

        WebDriverWait(self.driver, 10).until(
            EC.invisibility_of_element_located((By.ID, "instructions"))
        )

    def test_TimeoutException(self):
        self.driver.get("https://practicetestautomation.com/practice-test-exceptions/")
        self.driver.implicitly_wait(3)
        try:
            self.driver.find_element(By.ID, "add_btn").click()
            WebDriverWait(self.driver, 3).until(EC.visibility_of_element_located((By.ID, "row2")))
        except TimeoutException:
            print("TimeoutException tardi demaciado XD es demaciado poco tiempo we necesitamas tiempo no mames, que te pasa weon ,no me pagan los suficiente para anadar haciendo estas coas porfavor subamen el sueldo un 25% no sean gachos ,con amor el qa <3")
    def tearDown(self):
        self.driver.quit()
        
if(__name__=="__main__"):
   unittest.main(testRunner=HTMLTestRunner(output='./'))