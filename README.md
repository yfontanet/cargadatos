# ⛽ Proyecto BBDD – Carga de Estaciones de Servicio y Precios

Herramienta en Java para la Actividad 1 de la asignatura OPT1Q - Bases de Datos Avanzadas de la Universidad Internacional de la Rioja.
Este proyecto carga información de estaciones de servicio, empresas, carburantes y precios desde archivos CSV y los inserta en una base de datos PostgreSQL.  
Incluye manejo de duplicados, inserciones optimizadas y una arquitectura modular y escalable.

---

## 📌 Características principales

- Lectura y procesamiento de archivos CSV (`resul_mar.csv` y `resul_t.csv`)
- Inserción en base de datos PostgreSQL mediante JDBC
- Arquitectura en capas:
  - `db/` → conexión con BD
  - `dao/` → acceso a tablas
  - `loader/` → lector de CSV
  - `App.java` → punto de entrada del programa
- Inserciones seguras con PreparedStatement
- Evita duplicados utilizando métodos *findOrCreate*
- Batch insert para mejorar rendimiento en tabla `precio`
- Configuración mediante variables de entorno

---

## 📂 Estructura del proyecto

```text
bbdd/
│
├── datos/
│   ├── resul_mar.csv
│   └── resul_t.csv
│
└── src/main/java/bbdd/
    ├── dao/
    │   ├── CarburanteDAO.java
    │   ├── EmpresaDAO.java
    │   ├── EstacionDAO.java
    │   └── PrecioDAO.java
    ├── db/
    │   └── DatabaseConnection.java
    ├── loader/
    │   └── CSVLoader.java
    └── App.java
```

---

## 🧱 Arquitectura del sistema

El proyecto sigue una arquitectura por capas que separa claramente la lectura de datos, el acceso a base de datos y la lógica de aplicación.

```text
CSV → CSVLoader → DAO → PostgreSQL
                     ↑
                DatabaseConnection
```
                      
- **CSVLoader**: lee y transforma datos de los CSV  
- **DAO**: inserta/consulta datos de cada tabla  
- **DatabaseConnection**: gestiona conexión JDBC  
- **App.java**: controla el flujo general del programa  

---

## 🛢 Base de datos

Se espera una base de datos PostgreSQL con tablas:

- `empresa`
- `carburante`
- `estacion_servicio`
- `precio`

Cada DAO gestiona las operaciones CRUD básicas sobre su tabla.

---

## ⚙ Configuración

El proyecto lee credenciales desde variables de entorno:

| Variable | Descripción | Valor por defecto |
|---------|-------------|------------------|
| `DB_URL` | URL JDBC | `jdbc:postgresql://localhost:5432/act1_xml` |
| `DB_USER` | Usuario BD | `alumna` |
| `DB_PASS` | Contraseña BD | `postgres` |

---

## ▶ Ejecución

> **Importante:** Debes estar en la carpeta donde se encuentra `pom.xml`.

1. **Compilar el proyecto:**

```bash
mvn clean compile
```

2. **Luego corre la aplicación:**

```bash
mvn exec:java -Dexec.mainClass=bbdd.App
```
