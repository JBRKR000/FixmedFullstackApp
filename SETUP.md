# Development Setup Guide

Przewodnik konfiguracji środowiska rozwojowego dla projektu FixMed.

## Wymagania

- **Java 17 LTS** - [Pobierz](https://www.oracle.com/java/technologies/downloads/#java17)
- **Maven 3.9+** - [Pobierz](https://maven.apache.org/download.cgi)
- **MySQL 8.0+** - [Pobierz](https://dev.mysql.com/downloads/mysql/)
- **Docker & Docker Compose** (opcjonalnie, do uruchamiania usług) - [Pobierz](https://www.docker.com/products/docker-desktop)

## Opcja 1: Setup z Docker Compose (Rekomendowana)

### Krok 1: Klonowanie repozytorium

```bash
git clone https://github.com/JBRKR000/fixmed.git
cd fixmed
```

### Krok 2: Tworzenie pliku `.env`

Skopiuj plik `application-example.properties` i dostosuj wartości:

```bash
cp src/main/resources/application-example.properties .env
```

Lub utwórz plik `.env` w głównym katalogu:

```env
# Database
DB_URL=jdbc:mysql://mysql:3306/fixmed?useSSL=false&serverTimezone=UTC
DB_USERNAME=fixmed_user
DB_PASSWORD=your_secure_password_here

# MinIO (S3 Storage)
MINIO_URL=http://minio:9000
MINIO_ACCESS_NAME=minioadmin
MINIO_ACCESS_SECRET=minioadmin
MINIO_BUCKET_NAME=doctor-photos
DOCTOR_PHOTOS_BASE_URL=http://localhost:9000/doctor-photos

# RabbitMQ
RABBITMQ_HOST=rabbitmq
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest

# JWT (jeśli aplikacja go używa)
JWT_SECRET=your_secret_jwt_key_here
JWT_EXPIRATION=86400000
```

### Krok 3: Budowanie i uruchamianie aplikacji

```bash
# Budowanie obrazu Docker
docker build -t fixmed:latest .

# Uruchamianie aplikacji z Docker Compose
docker-compose up -d
```

Aplikacja będzie dostępna pod: `http://localhost:8083`

---

## Opcja 2: Lokalna konfiguracja (bez Docker)

### Krok 1: Konfiguracja bazy danych

Utwórz bazę danych MySQL:

```sql
CREATE DATABASE fixmed;
CREATE USER 'fixmed_user'@'localhost' IDENTIFIED BY 'your_secure_password';
GRANT ALL PRIVILEGES ON fixmed.* TO 'fixmed_user'@'localhost';
FLUSH PRIVILEGES;
```

### Krok 2: Konfiguracja aplikacji

Skopiuj plik konfiguracyjny:

```bash
cp src/main/resources/application-example.properties src/main/resources/application-dev.properties
```

Edytuj `application-dev.properties` i dostosuj:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/fixmed?useSSL=false&serverTimezone=UTC
spring.datasource.username=fixmed_user
spring.datasource.password=your_secure_password

minio.url=http://localhost:9000
minio.access.name=minioadmin
minio.access.secret=minioadmin

spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

### Krok 3: Uruchamianie lokalnie

```bash
# Budowanie aplikacji
mvn clean install

# Uruchamianie aplikacji
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.config.name=application-dev"
```

Lub korzystając z IDE (IntelliJ IDEA, Eclipse) - uruchom `FixmedApplication.java`.

---

## Instalacja zależności usług (RabbitMQ, MinIO)

### RabbitMQ

```bash
# Z Docker
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 \
  -e RABBITMQ_DEFAULT_USER=guest \
  -e RABBITMQ_DEFAULT_PASS=guest \
  rabbitmq:management-alpine
```

RabbitMQ Management dostępny: `http://localhost:15672` (guest:guest)

### MinIO (S3 Compatible Storage)

```bash
# Z Docker
docker run -d --name minio -p 9000:9000 -p 9001:9001 \
  -e MINIO_ROOT_USER=minioadmin \
  -e MINIO_ROOT_PASSWORD=minioadmin \
  minio/minio server /data --console-address ":9001"
```

MinIO Console: `http://localhost:9001` (minioadmin:minioadmin)

### MySQL

```bash
# Z Docker
docker run -d --name mysql -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=fixmed \
  -e MYSQL_USER=fixmed_user \
  -e MYSQL_PASSWORD=your_password \
  mysql:8.0
```

---

## Weryfikacja instalacji

### 1. Sprawdzenie Javy

```bash
java -version
# Powinna zwrócić wersję 17+
```

### 2. Sprawdzenie Mavena

```bash
mvn -version
```

### 3. Testowanie połączenia z bazą

```bash
mvn spring-boot:run
# Szukaj logów połączenia z bazą danych
```

### 4. Sprawdzenie endpointów

```bash
curl http://localhost:8083/health
# Powinna zwrócić status aplikacji
```

---

## Zmienne Środowiskowe

### Dla produkcji

Ustaw zmienne środowiskowe zamiast modyfikować `application.properties`:

```bash
export DB_URL="jdbc:mysql://prod-server:3306/fixmed"
export DB_USERNAME="prod_user"
export DB_PASSWORD="strong_password"
export MINIO_URL="https://s3.production.com"
export MINIO_ACCESS_NAME="prod_access_key"
export MINIO_ACCESS_SECRET="prod_secret_key"
export RABBITMQ_HOST="rabbitmq.production.com"
export RABBITMQ_USERNAME="prod_user"
export RABBITMQ_PASSWORD="prod_password"
export JWT_SECRET="your_production_jwt_secret"
```

---

## Rozwiązywanie problemów

### Problem: "Connection refused" dla bazy danych

**Rozwiązanie:** Sprawdź czy MySQL jest uruchomiony i dostępny na podanym URL.

```bash
mysql -h localhost -u fixmed_user -p
```

### Problem: "Failed to authenticate with RabbitMQ"

**Rozwiązanie:** Sprawdź czy RabbitMQ jest uruchomiony i hasło jest poprawne.

```bash
curl -u guest:guest http://localhost:15672/api/overview
```

### Problem: "Cannot connect to MinIO"

**Rozwiązanie:** Sprawdź czy MinIO jest uruchomiony i dostępny na podanym URL.

```bash
curl http://localhost:9000/health
```

---

## Dokumentacja API

Po uruchomieniu aplikacji, Swagger UI będzie dostępny pod:

```
http://localhost:8083/swagger-ui.html
```

---

## Komendy Maven

```bash
# Budowanie aplikacji
mvn clean install

# Uruchamianie testów
mvn test

# Uruchamianie testów z raportem pokrycia
mvn test jacoco:report

# Budowanie bez testów
mvn clean install -DskipTests

# Uruchamianie aplikacji
mvn spring-boot:run

# Packaging do JAR
mvn clean package
```

---

## IDE Konfiguracja

### IntelliJ IDEA

1. Otwórz projekt w IDEA
2. IDEA automatycznie zdetektuje projekt Maven
3. Zaczekaj na pobranie zależności
4. Konfiguracja Javy: File → Project Structure → Project SDK (ustaw Java 17)
5. Uruchom `FixmedApplication.java` jako aplikację Spring Boot

### VS Code

1. Zainstaluj rozszerzenia:
   - Extension Pack for Java (Microsoft)
   - Spring Boot Extension Pack (VMware)
2. Otwórz folder projektu
3. VS Code automatycznie konfiguruje projekt Maven
4. Uruchom aplikację: F5 lub Run → Start Debugging

---

## Dalsze zasoby

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Security](https://spring.io/projects/spring-security)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [RabbitMQ Documentation](https://www.rabbitmq.com/documentation.html)
- [MinIO Documentation](https://docs.min.io/)

---

**Pytania czy problemy?** Otwórz issue na GitHub lub skontaktuj się z zespołem.
