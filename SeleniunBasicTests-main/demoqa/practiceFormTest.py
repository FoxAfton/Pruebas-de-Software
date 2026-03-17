import unittest
from selenium import webdriver
from selenium.webdriver.common.by import By
from pyunitreport import HTMLTestRunner
from selenium.webdriver.support.ui import WebDriverWait as wait
from selenium.webdriver.support.ui import Select
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.keys import Keys

class addUserTest(unittest.TestCase):

  def setUp(self):
    self.driver = webdriver.Chrome()
    driver = self.driver
    driver.get("https://demoqa.com/automation-practice-form")
  
  def test_create_user(self):
    driver = self.driver
    firstName = driver.find_element(By.ID, "firstName")
    firstName.send_keys("Brianda")
    lastName = driver.find_element(By.ID, "lastName")
    lastName.send_keys("Campoy")
    email = driver.find_element(By.ID, "userEmail")
    email.send_keys("brianda.pekachu@gmail.com")
    gender = driver.find_element(By.ID, "gender-radio-2")
    gender.click()
    mobile = driver.find_element(By.ID, "userNumber")
    mobile.send_keys("6421102585")
    birthday = driver.find_element(By.ID, "dateOfBirthInput")
    birthday.send_keys("2000-11-21")
    birthday.send_keys(Keys.ENTER)
    subjects = driver.find_element(By.ID, "subjectsInput")
    subjects.send_keys("Maths")
    subjects.send_keys(Keys.ENTER)
    hobbies = driver.find_element(By.ID, "hobbies-checkbox-2")
    hobbies.click()
    picture = driver.find_element(By.ID, "uploadPicture")
    picture.send_keys("F:\\ITSON CLASES\\Pruebas de software EM2026\\SeleniunBasicTests\\demoqa\\image.jpeg")
    address = driver.find_element(By.ID, "currentAddress")
    address.send_keys("Here where I live")
    state = driver.find_element(By.ID, "state")
    state.click()
    state = wait(driver, 10).until(
    EC.element_to_be_clickable((By.ID, "react-select-3-input"))
    )
    state.send_keys("NCR")
    state.send_keys(Keys.ENTER)
    city = driver.find_element(By.ID, "city")
    city.click()
    city = wait(driver, 10).until(
    EC.element_to_be_clickable((By.ID, "react-select-4-input"))
    )
    city.send_keys("Delhi")
    city.send_keys(Keys.ENTER)

    submit = driver.find_element(By.ID, "submit")
    submit.click()


  def tearDown(self):
    return super().tearDown()
  
if(__name__=="__main__"):
  unittest.main()