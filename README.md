# 🏦 Credit Cards API

```bash
# 1. Клонируйте репозиторий
git clone https://github.com/yourusername/credit-cards-api.git

# 2. Настройте БД (для Linux/macOS)
createdb creditcards
export DB_URL=jdbc:postgresql://localhost:5432/creditcards
export DB_USERNAME=postgres
export DB_PASSWORD=yourpassword

mvn spring-boot:run
```

## ⚙ Конфигурация

Основные настройки (`application.properties`):

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

jwt.access.expiration=900000     
jwt.refresh.expiration=86400000 
encryption.secret.key=ваш-секретный-ключ

springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/v3/api-docs
```
## 🐳 Docker-развертывание

```bash

docker build -t credit-cards-api .


docker run -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/creditcards \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=yourpassword \

```
