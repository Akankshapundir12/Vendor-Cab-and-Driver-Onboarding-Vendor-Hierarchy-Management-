
# Vendor Cab and Driver Onboarding System

A comprehensive multi-level vendor management system for fleet operations, vehicle onboarding, and driver management with hierarchical access control.





### Demo Credentials

Access different admin levels by siginig up 
1. give the name to user
2. email id
3. password
4. role: super admin, regional admin , city admin, local admin.


## 🚀 Features

### 1. Multi-Level Vendor Hierarchy
- Flexible N-level vendor hierarchy (Super → Regional → City → Local)
- Role-based access management
- Parent-child relationship management
- Customizable permissions per vendor level

### 2. Super Vendor Dashboard
- Complete vendor network overview
- Real-time fleet status monitoring
- Document verification management
- Performance analytics and reporting

### 3. Vehicle Management
- Vehicle onboarding and registration
- Document tracking and verification
- Compliance monitoring
- Vehicle status management (active/inactive)

### 4. Driver Management
- Driver onboarding and verification
- Document upload and tracking
- Vehicle assignment
- Performance monitoring

### 5. Access Control
- Granular permission management
- Role-based access control
- Delegation capabilities
- Activity audit trails

## 🛠 Technology Stack

- **Frontend**: HTML, CSS, JavaScript  
- **Backend**: Java (Spring Boot), Maven  
- **State Management**: Local Storage, Context API  
- **UI Components**: Bootstrap, Vanilla JS Components  
- **Charts**: Chart.js  
- **Type Checking**: Java (Strongly Typed), ESLint (for JavaScript)  
- **Database**: PostgreSQL, JPA/Hibernate

## 📦 Installation

1. Clone the repository:
```bash
git clone https://github.com/Akankshapundir12/Vendor-Cab-and-Driver-Onboarding-Vendor-Hierarchy-Management-
cd Vendor-Cab-and-Driver-Onboarding-Vendor-Hierarchy-Management-
```

2. Install dependencies:
```bash
# Prerequisites
- Java 17+
- JRE
- Maven or Gradle
```

3. Start the development server:
```bash
# Using Maven
./mvnw spring-boot:run

# Or with Gradle
./gradlew bootRun
```

4. Visit the app:
```bash
http://localhost:8080
```

## 🏗 Project Structure

```
demo/
├── HELP.md
├── mvnw
├── mvnw.cmd
├── pom.xml
├── databases/
│   ├── users.csv
│   ├── driver.csv
│   └── vehicle.csv
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── moveinsync/
│   │   │           └── demo/
│   │   │               ├── AuthController.java
│   │   │               ├── CsvUserDatabase.java
│   │   │               ├── DashboardController.java
│   │   │               ├── DriverController.java
│   │   │               ├── VehicleController.java
│   │   │               └── VendorService.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       └── templates/
│   └── test/
│       └── java/
│           └── com/
│               └── moveinsync/
│                   └── demo/
│                       └── DemoApplicationTests.java
├── target/
│   ├── classes/
│   │   ├── application.properties
│   │   └── com/
│   │       └── moveinsync/
│   │           └── demo/
│   │               └── DemoApplication.class
│   └── test-classes/
│       └── com/
│           └── moveinsync/
│               └── demo/
│                   └── DemoApplicationTests.class
```

## 🔑 Key Components

### 1. Vendor Hierarchy Tree
- Interactive organizational chart
- Drag-and-drop functionality
- Visual hierarchy representation
- Quick actions menu

### 2. Dashboard Views
- Super Vendor Dashboard
- Regional Dashboard
- City Dashboard
- Local Dashboard

### 3. Management Interfaces
- Vehicle Management
- Driver Management
- Document Verification
- User Management

## 💡 Implementation Details

### State Management
- Zustand for global state management
- Context API for auth state
- Local state for UI components

### Performance Optimization
- Lazy loading of components
- Memoization of expensive calculations
- Efficient re-rendering strategies

### Error Handling
- Comprehensive error boundaries
- Graceful error messages
- Form validation
- API error handling

## 🔒 Security Features

- Role-based access control
- Secure authentication flow
- Protected routes
- Data validation

## 📈 Scalability Considerations

- Modular architecture
- Efficient data structures
- Optimized rendering
- Lazy loading strategies

## 🧪 Building

```bash
# Run build command
npm run build


```

## 📝 Documentation

Detailed documentation is available in the below :

## 📚 Component Documentation

---

### Core Components

#### **1. AuthController.java**
```java
import org.springframework.web.bind.annotation.RestController;
```
- 📌 Purpose: Handles user authentication and login APIs.
- 🔗 Routes:
  - GET /api/user?username=xyz → Returns user details from users.csv
- 🔄 Methods:
  - getUser(String username)
- 📁 Reads from: databases/users.csv
- 🧠 Uses: CsvUserDatabase

---

#### **2. CsvUserDatabase.java**
```java
import com.opencsv.CSVReader;
```
- 📌 Purpose: Central utility for CSV file read/write operations.
- 🔄 Methods:
  - getUserByUsername(String username)
  - getAllDrivers()
  - getAllVehicles()
  - writeUser(User user)
- 📁 Manages:
  - users.csv
  - driver.csv
  - vehicle.csv
- 💡 Tip: Each line in CSV corresponds to a domain model like User, Driver, or Vehicle.

---

#### **3. DashboardController.java**
```java
import org.springframework.web.bind.annotation.RestController;
```
- 📌 Purpose: Serves summary data for dashboards.
- 🔗 Routes:
  - GET /api/dashboard → Returns dashboard stats
- 📊 Returns JSON:
  - { totalDrivers, pendingApprovals, activeVehicles }
- 🧠 Aggregates data from: CsvUserDatabase

---

#### **4. DriverController.java**
```java
import org.springframework.web.bind.annotation.RestController;
```
- 📌 Purpose: Driver listing, addition, and status updates.
- 🔗 Routes:
  - GET /api/drivers
  - POST /api/drivers/add
- 🔄 Methods:
  - getDrivers()
  - addDriver(Driver driver)
- 📁 Data Source: databases/driver.csv

---

#### **5. VehicleController.java**
```java
import org.springframework.web.bind.annotation.RestController;
```
- 📌 Purpose: Vehicle management API.
- 🔗 Routes:
  - GET /api/vehicles
  - POST /api/vehicles/register
- 🔄 Methods:
  - getVehicles()
  - addVehicle(Vehicle vehicle)
- 📁 Data Source: databases/vehicle.csv

---

#### **6. VendorService.java**
```java
import org.springframework.stereotype.Service;
```
- 📌 Purpose: Shared business logic layer for controllers.
- 🧠 Handles:
  - Validation
  - CSV data delegation
  - Logic centralization
- 📁 Collaborates With:
  - CsvUserDatabase
  - Controller classes

---

### Configuration & Resources

#### **7. application.properties**
```properties
spring.main.web-application-type=servlet
server.port=8080
```
- 📌 Purpose: Sets Spring Boot app configuration.
- 🛠 Used For:
  - Port config
  - Servlet handling
  - DB credentials (if using MySQL later)

---

### UI Layer

#### **8. static/**
- 📌 Purpose: Static frontend files (JS, CSS, HTML).
- 📁 Contents: Your index.html, JS scripts, etc.

#### **9. templates/**
- 📌 Purpose: Template rendering engine views (if using Thymeleaf).
- 📁 Contents: Thymeleaf-compatible .html pages

---

### Testing

#### **10. DemoApplicationTests.java**
```java
import org.junit.jupiter.api.Test;
```
- 📌 Purpose: Unit and integration test suite.
- 🧪 Test Scope:
  - Basic app context load
  - Controller responses
- 💡 Uses: Spring Boot Test

---

### Data Files

#### **11. users.csv**
- 📌 Fields: username, password, email
- 📌 Used by: AuthController

#### **12. driver.csv**
- 📌 Fields: name, license, status, etc.
- 📌 Used by: DriverController, DashboardController

#### **13. vehicle.csv**
- 📌 Fields: vehicleId, driverId, active, etc.
- 📌 Used by: VehicleController, DashboardController

---

### ✅ Best Practices

#### 🧹 CSV Handling
- Always close FileReader/Writer using try-with-resources
- Avoid concurrent writes to the same file

#### 🔄 Controller Logic
- Keep controllers thin
- Move business logic to VendorService

#### ✅ Testing
- Write unit tests for controllers and service layer
- Validate CSV parsing with edge cases

#### 🔐 Authentication
- Add session/token logic if scaling
- Store hashed passwords in production

#### 📦 Folder Hygiene
- Keep model classes in a separate model/ directory
- Consider a repository layer if switching to DB later

#### 🤝 Contributing
- Fork the repository
- Create your feature branch
- Commit your changes
- Push to the branch
- Create a Pull Request

