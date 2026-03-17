import unittest
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import Select

class tableTest(unittest.TestCase):
    
    def setUp(self):
        self.driver = webdriver.Chrome()
    
    def test_login(self):
        driver = self.driver
        driver.get("https://practicetestautomation.com/practice-test-table/")
        driver.implicitly_wait(10)

        selectSort = Select(driver.find_element(By.ID, "sortBy"))
        selectSort.select_by_visible_text("Level")
        
    def tearDown(self):
        self.driver.quit()

if(__name__=="__main__"):
    unittest.main()