# Introducción a Selenium con Python

Este repositorio contiene un **ejemplo básico de pruebas automatizadas usando Selenium y Python**.  

---

# ¿Qué es Selenium?

**Selenium** es una herramienta de **automatización de navegadores web** utilizada principalmente para realizar **pruebas automatizadas de aplicaciones web**.

Permite controlar un navegador como:

- Chrome
- Firefox
- Edge

como si un usuario real estuviera interactuando con la aplicación.

Con Selenium se pueden automatizar acciones como:

- Abrir páginas web
- Llenar formularios
- Hacer clic en botones
- Navegar entre páginas
- Validar contenido en pantalla

---

# ¿Qué es Selenium WebDriver?

**WebDriver** es el componente de Selenium que permite controlar el navegador.

Funciona enviando comandos desde el código hacia el navegador, por ejemplo:

```python
driver.get("https://example.com")
```

Esto abre una página web automáticamente.

---

# Tecnologías utilizadas en este proyecto

Este proyecto usa:

- **Python**
- **Selenium**
- **unittest** (framework de pruebas incluido en Python)
- **pyunitreport** (generación de reportes HTML)

---

# Instalación paso a paso

## 1. Instalar Python

Primero debes instalar Python.

Descargar desde:

```
https://www.python.org/downloads/
```

Durante la instalación **activa la opción**:

```
Add Python to PATH
```

Verifica la instalación:

```bash
python --version
```

o

```bash
python3 --version
```
o
```bash
py --version
```

---

# 2. Instalar pip

`pip` es el administrador de paquetes de Python.

Normalmente ya viene incluido con Python.

Verificar:

```bash
pip --version
```
Si no se agrego a tu PATH como me paso a mi puedes intentar con
```bash
python -m pip --version
```

---

# 3. Instalar Selenium

Instalar Selenium con pip:

```bash
pip install selenium
```
o
```bash
python -m pip install selenium
```

Esto descargará la librería necesaria para controlar el navegador.

---

# 4. Instalar pyunitreport

`pyunitreport` permite generar reportes HTML de las pruebas.

Instalar con:

```bash
pip install pyunitreport
```
o
```bash
python -m pip install pyunitreport
```

---

# 5. Instalar un navegador

Obviamente necesitaras tener un navegador instalado, pero no voy a explicarte como instalar eso.

---

# 6. Descargar ChromeDriver

ChromeDriver es el **driver que permite que Selenium controle Google Chrome**.
(En el caso de este proyecto ya inclui el WebDriver en el repositorio)

1. Verificar versión de Chrome.
2. Descargar ChromeDriver compatible.

Descargar desde:

```
https://chromedriver.chromium.org/downloads
```

Descomprimir y colocar el ejecutable en:

- la misma carpeta del proyecto  
o
- una carpeta incluida en el **PATH del sistema**

---

# Ejemplo de prueba

El archivo `test_login.py` contiene una prueba automatizada de login.

Ejemplo de interacción con Selenium:

```python
driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login")

inputUsername = driver.find_element(By.NAME, "username")
inputUsername.send_keys("Admin")

inputPassword = driver.find_element(By.NAME, "password")
inputPassword.send_keys("admin123")

inputPassword.submit()
```

Esto automatiza las siguientes acciones:

1. Abrir la página de login
2. Escribir usuario
3. Escribir contraseña
4. Enviar formulario

Si quieres saber mas sobre como encontrar elementos con XPATH puedes revisar [esta informacion](https://github.com/ItsonClasesEjemplos/XPath-guia-basica)

[Guía básica de XPath](https://github.com/ItsonClasesEjemplos/XPath-guia-basica) 

---

# Esperas en Selenium

Las aplicaciones web suelen cargar elementos de forma dinámica.

Para evitar errores se usan **esperas**.

Ejemplo:

```python
WebDriverWait(driver, 10).until(
    EC.url_contains("/dashboard")
)
```

Esto significa:

```
esperar hasta 10 segundos
hasta que la URL contenga "/dashboard"
```

Si la condición no se cumple, se lanza una excepción.

---

# Uso de asserts

Los **asserts** verifican que el resultado sea el esperado.

Ejemplo:

```python
self.assertIn("/dashboard", driver.current_url)
```

Esto valida que el login fue exitoso.

---

# Ciclo de vida de las pruebas

El framework `unittest` ejecuta varias funciones automáticamente.

## setUp()

Se ejecuta **antes de cada prueba**.

Se usa para preparar el entorno.

```python
def setUp(self):
    self.driver = webdriver.Chrome()
```

---

## tearDown()

Se ejecuta **después de cada prueba**.

Se usa para limpiar el entorno.

```python
def tearDown(self):
    self.driver.quit()
```

---

## Métodos de prueba

Las pruebas deben comenzar con:

```
test_
```

Ejemplo:

```python
def test_login(self):
```

---

# Ejecutar las pruebas

Para ejecutar las pruebas:

```bash
python loginTest.py
```

o

```bash
python3 loginTest.py
```

---

# Reportes HTML

El proyecto utiliza **pyunitreport** para generar reportes automáticos.

Configuración:

```python
unittest.main(testRunner=HTMLTestRunner(output='./'))
```

Después de ejecutar las pruebas se generará un **reporte HTML** en la carpeta del proyecto.

Estos reportes muestran:

- pruebas exitosas
- pruebas fallidas
- tiempo de ejecución
- detalles de errores

---

# Buenas prácticas

Al escribir pruebas automatizadas se recomienda:

- Mantener las pruebas independientes
- Usar nombres claros para los tests
- Usar esperas (`WebDriverWait`) en lugar de `sleep`
- Verificar resultados usando asserts
- Automatizar escenarios reales de usuario

---

# Escenarios de prueba incluidos

Este proyecto incluye dos pruebas:

### Login exitoso

Verifica que un usuario válido pueda iniciar sesión.

### Login inválido

Verifica que credenciales incorrectas no permitan iniciar sesión.

---

# Recursos para aprender más

Documentación oficial de Selenium:

```
https://www.selenium.dev/documentation/
```

Documentación de Python unittest:

```
https://docs.python.org/3/library/unittest.html
```

---