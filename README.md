# Account Manager

Account Manager is a service that does authorization and authentication.

## Authentication
Auth is done via Google and GitHub. The service will delegate to either of them to check for accounts. Once connected to the OAuth provider,
the service will generate a token that can be used to re-authentication with the service.

This token can be invalidated, and more tokens can be generated.

The token that is currently being used, is shown on the homepage, to make it eaiser to use in CLI's if needed.

The token can either be in the a header `X-AUTH-TOKEN`, or in a cookie with the value `account-manager`.

## Authorization
Authorization is optional, but is avaliable is needed.

Authorization is broken into three componenets. The `subject`, which would be an application name. The `resource` is a named item, it could be something
like book, or machine. Finally, `action` is the name of an operation that can be preformed. If any of the components starts with an underscore,
then it's internal to the service, and is part of a reserved namespace.

Some examples of the triples are:

|`subject`|`resource`|`action`|
| --------------------- |:---------------------:| ---------------------:|
| local-library | book | checkout |
| {service name} | host | login |
| {service name} | container | login |
| {service name} | deply | promote |
| {service name} | deply | rollback |
| {service name} | deply | canary |

### Check Permissions

The endpoint is located at `accountmanager.ehdev.io/api/v1/check/{subject}/permission/{resource}/{action}`. The response will either have a status code of 
`200` if the user has permission. Otherwise the result status code will be `400`.

### Authorization Endpoint

Below is an API workflow. The Auth Token `LK2CvWAqp0klaTuC9YgSz6KWGykcKJEps2kdu3jfdo1kj4idm3i` is used, but this will be different for your deployment.

#### Create an Authorization

```
> curl -X "POST" "http://localhost:8080/api/v1/authorization" \
     -H 'X-AUTH-TOKEN: LK2CvWAqp0klaTuC9YgSz6KWGykcKJEps2kdu3jfdo1kj4idm3i' \
     -d $'{
  "subjectName": "my-test"
}'

# Response
{
  "subject": "my-test",
  "roles": [
    {
      "resource": "_admin",
      "action": "permission_admin"
    },
    {
      "resource": "_admin",
      "action": "rule_admin"
    }
  ]
}
```

Notice that there is an `_admin` resource. This is part of the public API for the resource. Any resource that's user defined
cannot start with an underscore.

#### Check to see the new subject.

```
## Get Subject
> curl "http://localhost:8080/api/v1/authorization/my-test" \
     -H 'X-AUTH-TOKEN: LK2CvWAqp0klaTuC9YgSz6KWGykcKJEps2kdu3jfdo1kj4idm3i'

# Response
{
  "subject": "my-test",
  "roles": [
    {
      "resource": "_admin",
      "action": "permission_admin"
    },
    {
      "resource": "_admin",
      "action": "rule_admin"
    }
  ]
}
```

This is the same response from the create.

#### Create a new Resource/Action

```
> curl -X "POST" "http://localhost:8080/api/v1/authorization/my-test/permission" \
     -H 'X-AUTH-TOKEN: LK2CvWAqp0klaTuC9YgSz6KWGykcKJEps2kdu3jfdo1kj4idm3i' \
     -d $'{
  "defaultUsers": [
    "ethan@ehdev.io"
  ],
  "action": "unlock",
  "resource": "home"
}'

# Response
{
  "usersAdded": [
    "ethan@ehdev.io"
  ],
  "usersMissing": []
}
```

Create a new resource and action pair. The `defaultUsers` field is optional. When provided, users will automatically be added to the 
pair. If there is a typo or missing user, it will show up in the `usersMissing` field in the response. The create will not fail
due to a user missing. By default, the user that created the resource will be added to the pair.

#### Check to see the new resource/action exists

```
> curl "http://localhost:8080/api/v1/authorization/my-test" \
     -H 'X-AUTH-TOKEN: LK2CvWAqp0klaTuC9YgSz6KWGykcKJEps2kdu3jfdo1kj4idm3i'

# Response
{
  "subject": "my-test",
  "roles": [
    {
      "resource": "_admin",
      "action": "permission_admin"
    },
    {
      "resource": "_admin",
      "action": "rule_admin"
    },
    {
      "resource": "home",
      "action": "unlock"
    }
  ]
}
```

See that when getting the resource, it will show the new `home/unlock` pair.

#### Get Subject, Resource, Action
```
> curl "http://localhost:8080/api/v1/authorization/my-test/permission/home/unlock" \
     -H 'X-AUTH-TOKEN: LK2CvWAqp0klaTuC9YgSz6KWGykcKJEps2kdu3jfdo1kj4idm3i'

# Response
{
  "users": [
    {
      "id": "lAxADyfI",
      "email": "ethan@ehdev.io",
      "name": "Ethan Hall"
    }
  ]
}
```

When requesting for a specific pair, you will see users who have permission.

#### Remove user from Subject, Resource, Action
```
> curl -X "DELETE" "http://localhost:8080/api/v1/authorization/my-test/permission/home/unlock/user/ethan@ehdev.io" \
     -H 'X-AUTH-TOKEN: LK2CvWAqp0klaTuC9YgSz6KWGykcKJEps2kdu3jfdo1kj4idm3i'

# No Response Returned
```

This will remove the user from the pair.

#### Remove Resource and Action
```
curl -X "DELETE" "http://localhost:8080/api/v1/authorization/my-test/permission/home/unlock" \
     -H 'X-AUTH-TOKEN: LK2CvWAqp0klaTuC9YgSz6KWGykcKJEps2kdu3jfdo1kj4idm3i'

# No Response Returned
```

This will delete the pair. Will fail if there are any users still in the pair.

#### Get Subject
```
curl "http://localhost:8080/api/v1/authorization/my-test" \
     -H 'X-AUTH-TOKEN: LK2CvWAqp0klaTuC9YgSz6KWGykcKJEps2kdu3jfdo1kj4idm3i'

# Response
{
  "subject": "my-test",
  "roles": [
    {
      "resource": "_admin",
      "action": "permission_admin"
    },
    {
      "resource": "_admin",
      "action": "rule_admin"
    }
  ]
}
```

Show the created pair is deleted.

#### Remove Subject
```
curl -X "DELETE" "http://localhost:8080/api/v1/authorization/my-test" \
     -H 'X-AUTH-TOKEN: LK2CvWAqp0klaTuC9YgSz6KWGykcKJEps2kdu3jfdo1kj4idm3i'

# No Response Returned
```

Clean up the created subject.v


DELETE http://localhost:8080/api/v1/authorization/my-test
Accept: application/json
Cache-Control: no-cache
Content-Type: application/json
X-AUTH-TOKEN: {{auth-token}}

###
