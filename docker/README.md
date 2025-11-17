# Contenedores Docker

## Herramientas

* [Docker desktop](https://docs.docker.com/get-started/get-docker/)
* [Extension _Container Tools_ para Visual Studio Code (opcional)](https://marketplace.visualstudio.com/items?itemName=ms-azuretools.vscode-containers)

## Contenedor de Postgres

### Descripción del fichero `docker-compose.yml`

El fichero `postgres/docker-compose.yml` configura los servicios Docker necesarios para la práctica:

* Servicio `db` (imagen `postgres:16`):
  * Usuario, contraseña y base de datos por defecto (POSTGRES_USER, POSTGRES_PASSWORD, POSTGRES_DB).
  * Mapeo de puerto (por defecto "5432:5432").
  * Volúmenes: `pgdata` para datos y `./init` para scripts de inicialización (.sql).
  * Healthcheck para esperar a que la base de datos esté lista.

* Servicio `pgadmin` (imagen `dpage/pgadmin4`):
  * Interfaz web en el puerto 8080.
  * Depende de que `db` esté _healthy_.
  * Volumen `pgadmindata` para persistencia de configuración.

* Volúmenes declarados: `pgdata` y `pgadmindata`.

Notas:

* En Windows, si el puerto 5432 está ocupado, cambie el mapeo de puertos (por ejemplo `5433:5432`).

* Los scripts SQL colocados en `postgres/init` se ejecutan automáticamente al crear el contenedor de la base de datos.

### Conectarse a pgAdmin y añadir una conexión a la base de datos

1. Levantar los contenedores

2. Abrir pgAdmin en el navegador:
   * URL: <http://localhost:8080>
   * Usuario: <admin@example.com>
   * Contraseña: admin

3. Añadir un nuevo servidor en pgAdmin:
   * Menú: "Create" → "Server..."
   * Pestaña General:
     * Name: `demo_xml`
   * Pestaña Connection:
     * Host name/address: `db`
     * Port: `5432`
     * Maintenance DB: `demo_xml`
     * Username: `profesor`
     * Password: `postgres`
     * Marcar "Save password" si se desea
   * Guardar y conectar.

4. Comprobación y resolución de problemas:
   * Ver el estado de los contenedores:

     ```powershell
     docker compose ps
     ```

   * Ver logs del servicio de BD:

     ```powershell
     docker compose logs -f db
     ```

   * Si hay problemas de conexión, comprobar que el servicio `db` está "healthy" y que no hay conflicto en el puerto (Windows).
