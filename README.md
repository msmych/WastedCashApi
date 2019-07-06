# Wasted cash API

Project [**help page**](https://telegra.ph/Wasted-cash-03-11)

### How to build
```
gradle clean bootJar
```

### How to run
```
java -jar <jar>
```
```
gradle bootRun
```

### Configuration

**Prod**

*application-prod.yml*
```
api-token: <api-token>
```

**Test**

*VM options*
```
-Dspring.profiles.active=dev
```
