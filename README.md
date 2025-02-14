# Flourish Application 🌱

This project is a multi-module Vaadin 24 and Spring Boot application with integrated user sign-in and registration
functionality. The application uses Spring Security for route protection and Spring Boot DevTools for live reload in
development mode.

## Prerequisites

- **Java JDK 17** (or higher)
- **Maven** (3.8.x or later)
- **MySQL Database** (with the appropriate database configuration set in `application.properties`)

## Building the Project

From the project root directory, run:

```bash
  mvn clean install
```

This will compile the modules and create the necessary artifacts.

## Running the Application

The application runs from the flourish-server module. Two convenience scripts have been provided:

### Windows

1. Open the project folder in Windows Explorer.
2. Double-click the `run.bat` file.
3. A command prompt window will open and run the following command:

```bash
  mvn -pl flourish-server spring-boot:run
```

4. Once the server starts, open your browser and navigate to http://localhost:8080/login.

### Mac/Linux

1. Open a terminal in the project root directory.
2. Make sure the `run.sh` script is executable (if not, run `chmod +x run.sh`).
3. Start the application by executing:

```bash
  ./run.sh
```

4. Once the server starts, open your browser and navigate to http://localhost:8080/login.