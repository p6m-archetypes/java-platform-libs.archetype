# {{ SolutionName }} Libs: Auth

## Background
This Library allows you to add authentication to your application. 
It is a wrapper around the [Spring Security](https://spring.io/projects/spring-security) library.

## Usage
1. To leverage this module, import the following module into your server's pom.xml

```xml
<dependencies>
    <dependency>
        <groupId>{{ org-solution-name }}</groupId>
        <artifactId>{{ org-solution-name }}-platform-auth</artifactId>
<!-- Version will be set with bom import in root pom.xml -->
    </dependency>
</dependencies>
```
2. Ensure you graphql-federated-gateway has authentication enabled(not necessarily enforced), and is propagating the Authorization header.
   1. Check your config/router.yaml file for the following:
   ```yaml
    # This enables authentication for the gateway.    
    plugins:
      p6m.auth:
        jwks:
          - provider: auth0
            endpoint: # TODO Update this endpoint to your Auth0 tenant
            rotate_seconds: 86400
        # This allows for specific endpoints to not require Authentication
        allow:
          queries: []
          #        - __schema
          #        - "auth*"
          mutations: []
           #       - "auth*"
    # This allows for the Authorization header to be propagated to the downstream services.
    headers:
      all:
        request:
         - propagate:
           named: "Authorization"
   ```
3. Add the IsAuthenticated annotation to an endpoint you want to protect. [IsAuthenticated](./src/main/java/{{ root_directory }}/auth/annotations/IsAuthenticated.java)
   1. Example:
   ```java
    @DgsQuery
    @IsAuthenticated
    public User user() {
        ...
    }
   ```
4. Access metadata from your token using the JWTService
   1. Import the JWT Service
    ```java
    import {{ root_package }}.{{ org-solution-name }}.platform.auth.service.JWTService;
    
    public class UserFetcher {
      private final JWTService jwtService;
   
      public UserFetcher(
        JWTService jwtService, 
        ...
      ) {
        this.jwtService = jwtService;
        ...
      }
    }
      
    ```
   2. Leverage the JWT Service to pull the userId from the token.
   ```java
    @DgsQuery
    @IsAuthenticated
    public User user() {
        var userId = jwtService.currentUser()
                               .map(JWTUser::getUserId)
                               .map(UUID::toString);
        if (userId.isPresent()) {
            return getUser(userId.get());
        } else {
            // This case signals that you probably haven't setup your token/user creation correctly.             
            throw UnauthorizedException.from("user");
        }
    }
   ```
   3. Double check you are setting your user_uuid correctly for JWT Tokens see [JWT Service](./src/main/java/{{ root_directory }}/auth/JWTService.java)
