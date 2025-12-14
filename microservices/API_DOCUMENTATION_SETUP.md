# API Documentation Setup (Swagger)

## Swagger UI Entegrasyonu

Her servise Swagger UI eklemek için:

### 1. pom.xml'e Dependency Ekle

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
```

### 2. application.properties'e Ekle

```properties
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

### 3. Controller'lara Annotation Ekle

```java
@Tag(name = "User Controller", description = "User management APIs")
@RestController
@RequestMapping("/api/users")
public class UserController {
    // ...
}
```

## Erişim

- Swagger UI: `http://localhost:{port}/swagger-ui.html`
- API Docs: `http://localhost:{port}/api-docs`

## Örnek

- User Service: http://localhost:8001/swagger-ui.html
- Hospital Service: http://localhost:8002/swagger-ui.html

