# Battery management system (bachelor thesis)
This bachelor thesis focuses on designing and implementing a system for charger monitoring and
battery management. Current solutions often lack comprehensive approaches to battery status mon-
itoring and charging process analysis. The implemented system supports Conrad Charge Manager
2010 and Turnigy Accucell-6 chargers. It utilizes a client-server architecture with a Kotlin backend
and PostgreSQL database. The web interface enables real-time monitoring of charging processes,
visualization of charging and discharging, including voltage, current, and capacity. For multi-cell
batteries, it offers monitoring of individual cells. Testing has proven the system’s functionality with
various charging programs. Thanks to its selected architecture, the system is prepared for future
expansion to support additional charger types. The solution represents an useful tool for optimizing
battery usage and extending battery lifespan.

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
2. Start the init script:
```bash
./run.sh
```

Application should be listening now on `localhost:3000`

Now you can use the application. For the start you can firstly add a new sizes and types for creation of the other objects. 
