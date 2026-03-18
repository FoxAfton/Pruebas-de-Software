import unittest
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import Select
from pyunitreport import HTMLTestRunner

class tableTest(unittest.TestCase):
    
    def setUp(self):
        self.driver = webdriver.Firefox()
    
    
    def test_table_Languagefilter(self):
       driver = self.driver
       driver.get("https://practicetestautomation.com/practice-test-table/")
       driver.implicitly_wait(10)

       driver.find_element(By.XPATH, "//label[contains(.,'Java')]").click()

       WebDriverWait(driver, 10).until(
        EC.invisibility_of_element_located((By.XPATH, "//tbody//tr[contains(.,'Python')]"))
    )

       celdas_language = driver.find_elements(By.XPATH, "//tbody//tr/td[3]")

       celdas_visibles = [celda for celda in celdas_language if celda.is_displayed()]

       for celda in celdas_visibles:
        self.assertEqual(celda.text, "Java")

    def test_table_LevelFilter(self):
       driver = self.driver
       driver.get("https://practicetestautomation.com/practice-test-table/")
       driver.implicitly_wait(10)

       driver.find_element(By.XPATH, "//label[contains(.,'Intermediate')]").click()
       driver.find_element(By.XPATH, "//label[contains(.,'Advanced')]").click()

       WebDriverWait(driver, 10).until(
        EC.invisibility_of_element_located((By.XPATH, "//tbody//tr[contains(.,'Intermediate, Advanced')]"))
        
    )

       celdas_level = driver.find_elements(By.XPATH, "//tbody//tr/td[4]")

       celdas_visibles = [celda for celda in celdas_level if celda.is_displayed()]

       for celda in celdas_visibles:
        self.assertEqual(celda.text, "Beginner")
    
    
    def test_table_minenrollments (self):
       driver = self.driver
       driver.get("https://practicetestautomation.com/practice-test-table/")
       driver.implicitly_wait(10)

       driver.find_element(By.XPATH, "//div[@class='dropdown-button']").click()
       
       
       driver.find_element(By.XPATH, "//li[contains(text(), '10,000+')]").click()

       import time
       time.sleep(2)
       
       celdas_enrollments = driver.find_elements(By.XPATH, "//tbody//tr/td[5]")
       celdas_visibles = [celda for celda in celdas_enrollments if celda.is_displayed()]

       for celda in celdas_visibles:
        numero = int(celda.text.replace(",", ""))
        self.assertGreaterEqual(numero, 10000)
    
       
    def test_table_Combinedfilters (self):
       driver = self.driver
       driver.get("https://practicetestautomation.com/practice-test-table/")
       driver.implicitly_wait(10)

       driver.find_element(By.XPATH, "//label[contains(.,'Python')]").click()
       driver.find_element(By.XPATH, "//label[contains(.,'Intermediate')]").click()
       driver.find_element(By.XPATH, "//label[contains(.,'Advanced')]").click()
      

       driver.find_element(By.XPATH, "//div[@class='dropdown-button']").click()
       WebDriverWait(driver, 10).until(
        EC.visibility_of_element_located((By.XPATH, "//li[contains(text(),'10,000+')]"))
    )
       driver.find_element(By.XPATH, "//li[contains(text(), '10,000+')]").click()

       import time
       time.sleep(2)

       celdas_language = driver.find_elements(By.XPATH, "//tbody//tr/td[3]")
       for celda in celdas_language:
        if celda.is_displayed():
            self.assertEqual(celda.text, "Python")

       celdas_level = driver.find_elements(By.XPATH, "//tbody//tr/td[4]")
       for celda in celdas_level:
        if celda.is_displayed():
            self.assertEqual(celda.text, "Beginner")

        celdas_enroll = driver.find_elements(By.XPATH, "//tbody//tr/td[5]")
       for celda in celdas_enroll:
        if celda.is_displayed():
            numero = int(celda.text.replace(",", ""))
            self.assertGreaterEqual(numero, 10000)

    def test_table_No_results_state (self):
       driver = self.driver
       driver.get("https://practicetestautomation.com/practice-test-table/")
       driver.implicitly_wait(10)

       driver.find_element(By.XPATH, "//label[contains(.,'Python')]").click()
       driver.find_element(By.XPATH, "//label[contains(.,'Beginner')]").click()

       NoState = WebDriverWait(driver, 10).until(
          EC.visibility_of_element_located((By.XPATH, "//*[contains(text(),'No matching courses')]"))
       )
       self.assertTrue(NoState.is_displayed)
        
    def test_table_Reset_button_visibility_and_behavior (self):
       driver = self.driver
       driver.get("https://practicetestautomation.com/practice-test-table/")
       driver.implicitly_wait(10)

       driver.find_element(By.XPATH, "//label[contains(.,'Java')]").click()

       reset = WebDriverWait (driver, 10).until(
          EC.visibility_of_element_located((By.ID, "resetFilters"))
       )
       self.assertTrue(reset.is_displayed)

       driver.find_element(By.XPATH, "//button[text()='Reset']").click()

       reset = WebDriverWait (driver, 10).until(
          EC.invisibility_of_element_located((By.ID, "resetFilters"))
       )

       reset_oculto = driver.find_element(By.ID, "resetFilters")
       self.assertFalse(reset_oculto.is_displayed())

       filas = driver.find_elements(By.XPATH, "//tbody//tr")
       filas_visibles = [f for f in filas if f.is_displayed()]
       self.assertEqual(len(filas_visibles), 9)

    def test_table_SortbyEnrollments (self):
       driver = self.driver
       driver.get("https://practicetestautomation.com/practice-test-table/")
       driver.implicitly_wait(10)

       select = Select(driver.find_element(By.ID, "sortBy"))
       select.select_by_visible_text("Enrollments")

       rows = WebDriverWait(driver, 10).until(
          EC.visibility_of_all_elements_located((By.XPATH, "//tbody//tr"))
       )

       celdas = driver.find_elements(By.XPATH, "//tbody//tr//td[5]")
       valores = [int(c.text.replace(",", "")) for c in celdas if c.is_displayed()]
       
       self.assertEqual(valores, sorted(valores))

    def test_table_Sort_by_Course_Name (self):
       driver = self.driver
       driver.get("https://practicetestautomation.com/practice-test-table/")
       driver.implicitly_wait(10)

       select = Select(driver.find_element(By.ID, "sortBy"))
       select.select_by_visible_text("Course Name")

       celdas = driver.find_elements(By.XPATH, "//tbody//tr//td[2]")
       nombres = [c.text for c in celdas if c.is_displayed()]

       self.assertEqual(nombres, sorted(nombres))



        
    def tearDown(self):
        self.driver.quit()

if(__name__=="__main__"):
   unittest.main(testRunner=HTMLTestRunner(output='./'))