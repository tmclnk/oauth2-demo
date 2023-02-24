# OAuth2 Timing Demo

This project includes a single maven repository with two applications

- client
- rs (resource server)

### Requirements

You'll need jdk11.

## Running

```shell
./mvnw -pl rs spring-boot:run
```

```shell
./mvnw -pl client spring-boot:run
```

## Configuration

There are two application.yaml files. You'll need to fill in the `TODO` values for issuer-uri, client-id, client-secret

- [rs](rs/src/main/resources/application.yaml)
- [client](client/src/main/resources/application.yaml)
