# Battery management system (bachelor thesis)

### Description

This bachelor thesis was created for the purpose of tracking the state of the batteries during the charging and discharging process. System is designed to work with two types of chargers (Conrad Charge Manager 2010 and Turnigy Accucell-6).
System is also designed for optional adding new types of chargers. The system divided into two parts: backend and frontend. The backend part is responsible for collecting data from the accumulators, storing it in the database and providing it to the frontend.
The frontend part is responsible for displaying the data collected by the backend in a user-friendly way. Frontend is communicating with the backend using REST API.
You can control the system using the web application. The application allows you to add new types, sizes, batteries, and also chargers. In the application, you can view the history of the batteries tracking results, and also some basic statistics.

### Requirements
Versions are those that were used during the development of the project. It is possible that the project will work with other versions of the software.
- Java with JDK 21
- npm version 10.5.0
- docker version 26.0.0

### How to run

1. Clone the repository using the command:
```
git clone https://github.com/Kudl1k/bachelor-thesis
```
2. Go to the directory with the project and create a docker image using the command:
```
cd bachelor-thesis
cd battery-managerment-system
./gradlew databaseInstance
```
3. Wait for the database to start and then run the backend using the command:
```
./gradlew run
```
4. Go to the directory with the project and run the frontend using the command:
```
cd web
npm install
npm run dev
```
5. Open the browser and go to the address:
```
http://localhost:5173/
```
Now you can use the application. For the start you can firstly add a new sizes and types for creation of the other objects. 