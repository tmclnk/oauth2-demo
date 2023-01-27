# OIDC Login App

This is an app that redirects out to Cloudentity for login,
then shoves the resulting info into the HTTP Session.

## Configuring the ACP

You'll need to register a **Server-side Web** Client in ACP. Set the **OAuth > REDIRECT_URI** to

```
http://localhost:8080/login/oauth2/code/cloudentity
```

## Configuring The App

Create a `src/main/resources/application.yaml` file.

```yaml
server.port: 8080
cloudentity.tenant-id:
cloudentity.workspace-id:

# uri of the resource service we are calling
user-service.base-uri: https://spring.users.runpaste.com
spring:
  security:
    oauth2:
      client:
        registration:
          cloudentity:
            ## Use the client-id and client-secret from your "Server-side Web" project
            ## in ACP.
            client-id:
            client-secret:
            provider: cloudentity
            client-authentication-method: client_secret_post
            authorization-grant-type: authorization_code

            ## Which scopes are we, the client, asking for?
            ## If we ask for permissions here that this client hasn't
            ## been granted in the ACP, we will get an error before we
            ## even redirect to the login page
            scope:
              # Standard OAuth2 scopes
              - email
              - profile
              # Scopes from our resource server, ce-rs-user-profile.
              - userprofile.view
              - userprofile.edit
        provider:
          cloudentity:
            user-info-authentication-method: form
            issuer-uri: https://${cloudentity.tenant-id}.us.authz.cloudentity.io/${cloudentity.tenant-id}/${cloudentity.workspace-id}
logging:
  level:
    root: INFO
    com.example: TRACE
    org.springframework.web.client: DEBUG
    reactor.netty.http.client: DEBUG
server:
  forward-headers-strategy: native
  error:
    include-message: always
    include-exception: true
    include-stacktrace: always
    include-binding-errors: always

```

## Usage

```shell
mvn spring-boot:run
```

Then visit [localhost:8080], which will redirect you to the CloudEntity login screen. After authenticating, you'll
be redirected back to http://localhost:8080/login/oauth2/code/cloudentity.

Log out at [http://localhost:8080/logout](http://localhost:8080/logout).

## Links

* [Spring WebFlux Authorized Clients](https://docs.spring.io/spring-security/reference/5.7.4/reactive/oauth2/client/index.html)
* [Spring WebFlux Security](https://docs.spring.io/spring-security/reference/5.7.4/reactive/configuration/webflux.html)
* [OAuth Grant Explainer](https://alexbilbie.com/guide-to-oauth-2-grants/)

[localhost:8080]: (https://localhost:8080)
