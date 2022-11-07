# OIDC Login App

This is an app that redirects out to Cloudentity for login, 
then shoves the resulting info into the HTTP Session.

## Configuring the ACP

You'll need to register a **Server-side Web** Client in ACP. Set the **OAuth > REDIRECT_URI** to

```
http://localhost:8080/login/oauth2/code/cloudentity
```

## Configuring The App

Create a `src/main/resources/application.properties` file. You'll need to replace `{{tid}}` with your Tenant Id, e.g.
demo or my-workspace.

```
spring.security.oauth2.client.registration.cloudentity.client-id=
spring.security.oauth2.client.registration.cloudentity.client-secret=
spring.security.oauth2.client.provider.cloudentity.issuer-uri=https://{{tid}}.us.authz.cloudentity.io/{{tid}}/demo
server.forward-headers-strategy=native
server.servlet.session.persistent=false
```

## Usage

```shell
mvn spring-boot:run
```

Then visit [localhost:8080], which will redirect you to the CloudEntity login screen. After authenticating, you'll
be redirected back to http://localhost:8080/login/oauth2/code/cloudentity.

Log out at [http://localhost:8080/logout](http://localhost:8080/logout).

## Reverse Proxies

If you're running behind a reverse proxy, you'll need to respect `X-Forwarded-*` headers in order to
correctly create a redirect url.

```properties
server.forward-headers-strategy=native
```

## Links

* [OAuth Grant Explainer](https://alexbilbie.com/guide-to-oauth-2-grants/)

[localhost:8080]: (https://localhost:8080)
