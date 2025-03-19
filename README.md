# Flourish Application 🌱

**Flourish** is a web-based plant care application built with **Vaadin 24** and **Spring Boot 3.4.2**. Designed for plant enthusiasts, Flourish enables users to seamlessly manage, explore, and nurture their personal plant collections. It integrates with the **Perenual API** to provide comprehensive and detailed plant information directly within the application.

The app simplifies plant care by providing an interactive dashboard, detailed plant profiles, and dynamic watering schedules tailored to each plant’s specific needs.

## Key Features:

- **Personal Plant Library**:
    - Effortlessly add and manage plants in your personal digital garden.
    - Explore detailed plant descriptions, including medicinal properties, toxicity (to humans and pets), edibility, growth conditions, and care instructions.

- **Interactive Plant Profiles**:
    - Access comprehensive plant data sourced from the Perenual API.
    - Visualize plant characteristics with intuitive icons and clear, engaging descriptions.

- **Watering Schedule Management**:
    - Stay informed with intelligent watering schedules based on each plant's unique care requirements.
    - Real-time visual feedback through an integrated water-level gauge indicating optimal watering times.

- **Secure User Authentication**:
    - Robust sign-in and registration secured by Spring Security, ensuring a personalized and protected user experience.

- **Modern, Responsive UI**:
    - Intuitive interface developed using Vaadin 24, with smooth navigation, real-time updates, and a clean, user-friendly design optimized for all devices.

## Technology Stack:

- **Java 17**
- **Spring Boot 3.4.2**
- **Vaadin 24.6.5**
- **MariaDB** (Data persistence)
- **Spring Security** (User authentication and authorization)
- **Perenual API** (Comprehensive plant information)

🌿 **Flourish** helps your plants thrive by bringing smart, user-friendly gardening to your fingertips.

## Prerequisites

- **Java JDK 17** (or higher)
- **Maven** (3.8.x or later)
- **MariaDB Database** (with the appropriate database configuration loaded from an `env.properties` file as specified in `application.properties`)

## Building the Project

From the project root directory, run:

```bash
  mvn clean install
```

This will compile the modules and create the necessary artifacts.
Note that Maven is required to run `mvn` commands.

## Running the Application

The application runs from the flourish-server module. Two convenience scripts have been provided:

### Windows

1. Open the project folder in Windows Explorer.
2. Double-click the `run.bat` file.
3. A command prompt window will open and run the following command:

```bash
  mvn spring-boot:run
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