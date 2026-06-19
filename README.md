# evmTracker

Proyecto Spring Boot para seguimiento de indicadores EVM con autenticación JWT.

## Requisitos

- Java 17
- Maven 3.x (o usar `./mvnw` incluido)

## Ejecutar localmente

1. Compilar el proyecto:
   ```bash
   ./mvnw clean package
   ```

2. Ejecutar la aplicación:
   ```bash
   ./mvnw spring-boot:run
   ```

3. Abrir en el navegador:
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - API docs: `http://localhost:8080/v3/api-docs`

## Endpoints principales

- `POST /auth/register`
  - Registra un nuevo usuario.
  - Body JSON:
    ```json
    {
      "username": "user1",
      "password": "password"
    }
    ```

- `POST /auth/login`
  - Genera token JWT y refresh token.
  - Body JSON:
    ```json
    {
      "username": "user1",
      "password": "password"
    }
    ```

- `POST /auth/refresh`
  - Renueva el token usando un refresh token.
  - Body JSON:
    ```json
    {
      "refreshToken": "<refresh_token>"
    }
    ```

## Inicialización de la base de datos

El proyecto usa H2 en memoria en desarrollo. Si querés inicializar la base de datos manualmente, hay un script en:

- `scripts/init-db.sql`

Ejemplo usando H2:

```sql
RUNSCRIPT FROM 'scripts/init-db.sql';
```

> Nota: el script crea tablas de usuarios, roles y refresh tokens. La aplicación también genera las tablas automáticamente con `spring.jpa.hibernate.ddl-auto=update`.

## Script de inicialización

El archivo `scripts/init-db.sql` contiene la creación de tablas necesarias para usuarios y refresh tokens, además de un usuario de ejemplo.

## Git y GitHub

Esta rama local se creó para preparar el repositorio de GitHub. Para publicar en GitHub, agrega un remoto y sube las ramas:

```bash
git remote add origin git@github.com:<usuario>/evmTracker.git
git push -u origin develop
git push -u origin feature/github-setup
```

Para un PR real, se puede usar `feature/*` hacia `develop` y luego abrir el pull request en GitHub.
