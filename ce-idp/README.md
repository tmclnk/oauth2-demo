# Spring Boot Custom Identity Provider

Dummy IdP to verify we can hack our legacy authentication into a modern
auth flow using CloudEntity.

![login screen](./docs/login.png)

## Configuration

### Register Custom IdP in ACP

You'll need to add an **Identity Provider** in ACP. This will be a **Custom IDP**. When prompted, the Login URL will be

```text
http://localhost:8888/login
```

### application.properties

Create a `src/main/resources/application.properties` (which is .gitignore'd).

```properties

```

## Running

We're expecting Java 17.

```shell
./mvnw spring-boot:run
```

You'll need a client app to perform a redirect to get you valid
`login_id` and `login_state` values on the login page. The default demo app
in the workspace should suffice.

## Consent Screen

You'll need to map an attribute into the "Name" field in order to get sane output on the
Consent screen.

![Consent Screen](./docs/consent.png)

## Links

- [HOWTO Create Custom IDP in CloudEntity](https://cloudentity.com/developers/howtos/identities/custom-idp/)