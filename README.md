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

## Inicialización de la base de datos (PostgreSQL)

La aplicación ahora usa PostgreSQL por defecto. Configuración por defecto en `application.yaml`:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/evm_tracker
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
```

Pasos recomendados para desarrollo local:

1. Instalar y ejecutar PostgreSQL (por ejemplo con Docker):

```bash
docker run --name evm-postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=evm_tracker -p 5432:5432 -d postgres:15
```

2. Inicializar tablas y datos de ejemplo (opcional):

```bash
# usando psql (debe estar instalado)
psql -h localhost -p 5432 -U postgres -d evm_tracker -f scripts/init-db.sql
```

3. Ejecutar la aplicación (maven):

```bash
./mvnw spring-boot:run
```

Alternativa: configurar variables de entorno en vez de editar `application.yaml`:

```
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/evm_tracker
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
```

> Nota: Hibernate crea/actualiza las tablas automáticamente con `spring.jpa.hibernate.ddl-auto=update`. El script en `scripts/init-db.sql` añade el usuario `admin` y roles si lo ejecutás manualmente.

## Git y GitHub

Esta rama local se creó para preparar el repositorio de GitHub. Para publicar en GitHub, agrega un remoto y sube las ramas:

```bash
git remote add origin git@github.com:<usuario>/evmTracker.git
git push -u origin develop
git push -u origin feature/github-setup
```

Para un PR real, se puede usar `feature/*` hacia `develop` y luego abrir el pull request en GitHub.
