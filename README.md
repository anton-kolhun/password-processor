# Password sharing application

## Required Prerequisites

* JDK 8
* docker/docker-compose

## Building 
```
./mvnw package -DskipTests
```
Once project is built  ./docker/password-processor/password-processor-1.0.jar should get added

## Running tests
```
./mvnw test
```
## Running application
docker should be started from ./docker folder.
`docker/password-processor/start.sh` and `docker/password-processor/wait-for-it.sh`
should have executable rights.

```
cd docker
docker-compose build
docker-compose up -d
```

## Api documentation
```
http://localhost:8080/swagger-ui.html
```
### Creating token
```
curl -v -H "Content-Type: application/json" -d "{\"passwords\": [\"secret\"]}" localhost:8080/password
```

### Requesting password
```
curl localhost:8080/password?token=<generated_token>
```
where `<generated_token>` is taken from creating-token response

### Deleting tokens
```
curl -v -H "Content-Type: application/json" -d "{\"requester\": \"linked.in\"}" localhost:8080/password/delete
```

