## Exercises for Vault

### Start a Vault and Mysql services using Docker.

- Ensure that Docker Desktop is started.
- Open a (WSL) terminal and navigate to the root of the springbootcourse folder.
- In the root of the springbootcourse project, there is a `docker.compose.yml` file.
- Run the command `docker-compose up vault mysql` to start the vault service and the mysql.
- Open a browser on `http://localhost:8200`. 
- Use `myroot` as token to login.

### Create secrets for MySql in Vault.

- Navigate to the `secret` and create a new secret `mysql` with the following secrets:

```json
{
  "db": "mydb",
  "password": "testuser",
  "username": "testuser"
}
```

### Create a new Springboot project with `jdbc` and `vault`

- Add the following configuration: 

```properties
spring.cloud.vault.token=myroot
spring.cloud.vault.scheme=http
spring.cloud.vault.kv.enabled=true
spring.cloud.vault.kv.application-name=mysql
spring.config.import=vault://

spring.datasource.url=jdbc:mysql://localhost/${db}
spring.datasource.username=${username}
spring.datasource.password=${password}
```

- Start the application and see that it connects to the database without errors.

### Add secrets for demo profile

- Use Intellij or your favorite DB manager to create a new database on mysql (login with root/password):

```mysql
create database demodb;
create user 'demo' identified by 'demo';
grant all on demodb.* to 'demo'@'%';
```
- In the browser for Vault navigate to `secret` and create a new demo secret with the path `mysql/demo`:

```json
{
  "db": "demodb",
  "password": "demo",
  "username": "demo"
}
```

### Activate demo profile in project

- Activate the demo profile by adding `spring.profiles.active=demo`
- Restart the application and see that it connects without errors.


