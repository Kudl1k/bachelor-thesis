# Battery Management system

An application for managing battery data that uses Postgres database running on Docker image as a storage.

## Steps

1. Execute gradle task `databaseInstance` and wait until Docker Compose builds image and starts container
2. Execute this command to run the sample:

```bash
./gradlew run
```

Then, you can open [http://localhost:8080/](http://localhost:8080/) in a browser to use app.

