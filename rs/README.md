# Impersonation Resource and Webhook Server

* Receives calls to `PUT /impersonate/{username}` and saves them for the current user, based on access token.
* Responds to (okta) calls to `POST /webhook` with a payload that will add an impersonation claim.

## Webhook Payload

### Example Incoming Webhook Body

```json
{
  "eventId": "F4z-UQf4RgaLTsl2niudaA",
  "eventTime": "2023-01-27T21:02:37.000Z",
  "eventType": "com.okta.oauth2.tokens.transform",
  "eventTypeVersion": "1.0",
  "contentType": "application/json",
  "cloudEventVersion": "0.1",
  "source": "https://dev-25852157.okta.com/oauth2/aus80gq9jmT6cfXyZ5d7/v1/authorize",
  "data": {
    "context": {
      "request": {
        "id": "Y9Q77Q53Qmvz_oW7tA4M7gAACuc",
        "method": "GET",
        "url": {
          "value": "https://dev-25852157.okta.com/oauth2/aus80gq9jmT6cfXyZ5d7/v1/authorize?scope=openid+email&response_type=code&state=vPaxn07xsjiQ2vk-2STSfr0faSTbdVW03eaMJDWP_9I%3D&redirect_uri=http%3A%2F%2Flocalhost%3A9393%2Flogin%2Foauth2%2Fcode%2Fmyidp&nonce=-m_LiEA1bSp3i-mKl32lM-AKgrBhH-VzqgyE488yXTQ&client_id=0oa7yajmmzyE0mRpo5d7"
        },
        "ipAddress": "167.248.15.71"
      },
      "protocol": {
        "type": "OAUTH2.0",
        "request": {
          "scope": "openid email",
          "state": "vPaxn07xsjiQ2vk-2STSfr0faSTbdVW03eaMJDWP_9I=",
          "redirect_uri": "http://localhost:9393/login/oauth2/code/myidp",
          "response_mode": "query",
          "response_type": "code",
          "client_id": "0oa7yajmmzyE0mRpo5d7"
        },
        "issuer": {
          "uri": "https://dev-25852157.okta.com/oauth2/aus80gq9jmT6cfXyZ5d7"
        },
        "client": {
          "id": "0oa7yajmmzyE0mRpo5d7",
          "name": "Spring Web App",
          "type": "PUBLIC"
        }
      },
      "session": {
        "id": "idxYFdwXQxFTwmoJq9dWwdrvg",
        "userId": "00u7v8l866Ky3aX8H5d7",
        "login": "tmclaughlin@dmsi.com",
        "createdAt": "2023-01-27T21:02:37.000Z",
        "expiresAt": "2023-01-27T23:02:37.000Z",
        "status": "ACTIVE",
        "lastPasswordVerification": "2023-01-27T18:32:48.000Z",
        "lastFactorVerification": "2023-01-27T18:32:48.000Z",
        "amr": [
          "PASSWORD"
        ],
        "idp": {
          "id": "00o7ujs31mno1qbwS5d7",
          "type": "OKTA"
        },
        "mfaActive": false
      },
      "user": {
        "id": "00u7v8l866Ky3aX8H5d7",
        "passwordChanged": "2023-01-06T18:55:52.000Z",
        "profile": {
          "login": "tmclaughlin@dmsi.com",
          "firstName": "Tom",
          "lastName": "McLaughlin",
          "locale": "en",
          "timeZone": "America/Los_Angeles"
        },
        "_links": {
          "groups": {
            "href": "https://dev-25852157.okta.com/api/v1/users/00u7v8l866Ky3aX8H5d7/groups"
          },
          "factors": {
            "href": "https://dev-25852157.okta.com/api/v1/users/00u7v8l866Ky3aX8H5d7/factors"
          }
        }
      },
      "policy": {
        "id": "00p818y0dfWtWggVT5d7",
        "rule": {
          "id": "0pr83lkzks07IRSOA5d7"
        }
      }
    },
    "identity": {
      "claims": {
        "sub": "00u7v8l866Ky3aX8H5d7",
        "email": "tmclaughlin@dmsi.com",
        "ver": 1,
        "iss": "https://dev-25852157.okta.com/oauth2/aus80gq9jmT6cfXyZ5d7",
        "aud": "0oa7yajmmzyE0mRpo5d7",
        "jti": "ID.W4sPxEHvTKswe8ty0iJJ-br9AbZiLyWAQipyd-7OrH8",
        "amr": [
          "pwd"
        ],
        "idp": "00o7ujs31mno1qbwS5d7",
        "nonce": "-m_LiEA1bSp3i-mKl32lM-AKgrBhH-VzqgyE488yXTQ",
        "auth_time": 1674844368,
        "agilityUsername": "tmclaughlin123"
      },
      "token": {
        "lifetime": {
          "expiration": 3600
        }
      }
    },
    "access": {
      "claims": {
        "ver": 1,
        "jti": "AT.v4g7PKop0mhY4bICuQozMdAbHFhxnph0Xd9M4fDIQsk",
        "iss": "https://dev-25852157.okta.com/oauth2/aus80gq9jmT6cfXyZ5d7",
        "aud": "urn:dev:poc",
        "cid": "0oa7yajmmzyE0mRpo5d7",
        "uid": "00u7v8l866Ky3aX8H5d7",
        "auth_time": 1674844368,
        "sub": "00u7v8l866Ky3aX8H5d7"
      },
      "token": {
        "lifetime": {
          "expiration": 3600
        }
      },
      "scopes": {
        "openid": {
          "id": "scp80gq9jookD8FEP5d7",
          "action": "GRANT"
        },
        "email": {
          "id": "scp80gq9jqsr1VV5T5d7",
          "action": "GRANT"
        }
      }
    }
  }
}
```

### Example Webhook Response

```json
{
    "commands": [
        {
            "type": "com.okta.identity.patch",
            "value": [
                {
                    "op": "add",
                    "path": "/claims/impersonate",
                    "value": "bstan"
                }
            ]
        }
    ]
}
```

## Links

- [Baeldung on Resource Servers](https://www.baeldung.com/spring-security-oauth-resource-server)