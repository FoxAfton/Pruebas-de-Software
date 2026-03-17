import unittest
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from pyunitreport import HTMLTestRunner

class LoginTest (unittest.TestCase):
    
    def setUp(self):
        self.driver = webdriver.Chrome()
        
    def test_login_Exito (self):
        driver = self.driver
        driver.get("https://practicetestautomation.com/practice-test-login/")
        driver.implicitly_wait(10)
    
        driver.find_element(By.NAME, "username").send_keys("student")
        driver.find_element(By.NAME, "password").send_keys("Password123")
        
        
        driver.find_element(By.ID, "submit").click()
        self.assertIn("logged-in-successfully", driver.current_url)
        
        
    def test_login_IncorrectUser(self):
        driver = self.driver
        driver.get("https://practicetestautomation.com/practice-test-login/")
        driver.implicitly_wait(10)
        
        driver.find_element(By.NAME, "username").send_keys("no lo se")
        driver.find_element(By.NAME, "password").send_keys("Password123")
        
        
        driver.find_element(By.ID, "submit").click()
        
        error = WebDriverWait(driver, 10).until(
            EC.visibility_of_element_located((By.ID, "error"))
        )
        self.assertTrue(error.is_displayed())
        
    def test_login_IncorrectPassword(self):
        driver = self.driver
        driver.get("https://practicetestautomation.com/practice-test-login/")
        driver.implicitly_wait(10)
        
        driver.find_element(By.NAME, "username").send_keys("student")
        driver.find_element(By.NAME, "password").send_keys("no lo se")
        
        
        driver.find_element(By.ID, "submit").click()
        
        error = WebDriverWait(driver, 10).until(
            EC.visibility_of_element_located((By.ID, "error"))
        )
        self.assertTrue(error.is_displayed())
        
    def tearDown(self):
        self.driver.quit()
        
if(__name__=="__main__"):
   unittest.main(testRunner=HTMLTestRunner(output='./'))