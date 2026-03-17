import unittest
from selenium import webdriver
from selenium.webdriver.common.by import By
from pyunitreport import HTMLTestRunner
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support.ui import Select
from selenium.webdriver.support import expected_conditions as EC


class addUserTest(unittest.TestCase):

  def setUp(self):
    self.driver = webdriver.Chrome()
    driver = self.driver
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login")
    # login
    usernameInput = driver.find_element(By.NAME, "username")
    usernameInput.send_keys("Admin")
    passwordInput = driver.find_element(By.NAME, "password")
    passwordInput.send_keys("admin123")
    passwordInput.submit()
    WebDriverWait(driver, 10).until(EC.url_contains("/dashboard"))
    pimButton = driver.find_element(By.XPATH, "//a[contains(@href,'viewPimModule')]")
    pimButton.click()
  
  def test_create_user(self):
    driver = self.driver
    userRoleSelect = driver.find_element(By.XPATH, "//*[@id='app']/div[1]/div[2]/div[2]/div/div/form/div[1]/div/div[1]/div/div[2]")
    


  def tearDown(self):
    return super().tearDown()