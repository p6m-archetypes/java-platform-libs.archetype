# {{ SolutionName }} Libs: Logging

To enable this as the logging implementation for a specific module (such as a server):

```xml
<dependencies>
    <dependency>
        <groupId>{{ root_package }}</groupId>
        <artifactId>{{ org-solution-name }}-platform-logging</artifactId>
    </dependency>
</dependencies>
```

Generally, you only want to supply a logging implementation in just your server.  However, to ensure
there is a logging implementation for your tests, you can add this to all modules at tests scope by placing this
in the parent pom dependencies.  Use the above dependency declaration to provide a logging implementation for runtime
scope.
```xml
<dependencies>
    <dependency>
        <groupId>{{ root_package }}</groupId>
        <artifactId>{{ org-solution-name }}-platform-logging</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```
