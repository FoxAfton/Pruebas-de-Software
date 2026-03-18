import unittest
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from pyunitreport import HTMLTestRunner

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
        
    
    def tearDown(self):
        self.driver.quit()
        
if(__name__=="__main__"):
   unittest.main(testRunner=HTMLTestRunner(output='./'))