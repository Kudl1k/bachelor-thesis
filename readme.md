# Battery management system (bachelor thesis)

### Description

This bachelor thesis was created for the purpose of tracking the state of the batteries during the charging and discharging process. System is designed to work with two types of chargers (Conrad Charge Manager 2010 and Turnigy Accucell-6).
System is also designed for optional adding new types of chargers. The system divided into two parts: backend and frontend. The backend part is responsible for collecting data from the accumulators, storing it in the database and providing it to the frontend.
The frontend part is responsible for displaying the data collected by the backend in a user-friendly way. Frontend is communicating with the backend using REST API.
You can control the system using the web application. The application allows you to add new types, sizes, batteries, and also chargers. In the application, you can view the history of the batteries tracking results, and also some basic statistics.

### Requirements
Versions are those that were used during the development of the project. It is possible that the project will work with other versions of the software.
- `Java 21`
- `npm 10.5.0`
- `docker 26.0.0`

### How to run

1. Clone the repository using the command:
```
git clone https://github.com/Kudl1k/bachelor-thesis
```
2. Create a docker container with this command:
```bash
docker run -d \
  --name postgres_battery \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=admin \
  -v postgres_data:/var/lib/postgresql/data \
  -p 5432:5432 \
  postgres:17-alpine
```
If you don't have the docker on your raspberry. Here is setup of [docker](https://docs.docker.com/engine/install/raspberry-pi-os/).

3. Create a database in the docker container:
``` bash
docker exec -it postgres_battery psql -U admin -d postgres -c "CREATE DATABASE battery;"
```
4. Go to the directory with the project and create a docker image using the command:
```
cd bachelor-thesis
cd battery-managerment-system
```
5. Wait for the database to start and then run the backend using the command:
```
./gradlew run
```
6. Go back to root directory with the project, navigate to `web` and run the frontend using the command:
```
cd web
npm install
npm run dev
```
If you don't have `npm` you can install it [here](https://github.com/nodesource/distributions?tab=readme-ov-file#installation-instructions-deb)

**OPTIONAL:**
If you want to have [exposed port](https://www.a2hosting.com/kb/developer-corner/linux/installing-and-configuring-ufw-uncomplicated-firewall/), you need to change the default url here:
```
/web/src/models/Default.ts
```
7. Open the browser and go to the address:
```
http://localhost:5173/
```
Now you can use the application. For the start you can firstly add a new sizes and types for creation of the other objects. 
