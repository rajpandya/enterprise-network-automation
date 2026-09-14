# Enterprise Network Automation Self-Service Platform

## 1. Project Overview

The Enterprise Network Automation Self-Service Platform is a portfolio project designed to demonstrate how modern enterprise applications can combine network automation, REST APIs, Java concurrency, Python automation, persistent data storage, and web-based self-service capabilities.

The application currently provides a centralized network device inventory where users can create, view, update, delete, and health-check network devices.

The project is being developed incrementally with an enterprise architecture in mind.

The current implementation includes:

* Java 21
* Spring Boot
* Spring Web REST APIs
* Spring Data JPA
* Hibernate
* PostgreSQL
* Java `CompletableFuture`
* `ThreadPoolTaskExecutor`
* Python network automation
* Next.js
* React
* TypeScript
* Maven
* Git and GitHub

Future phases will add:

* Red Hat OpenShift
* Angular
* MuleSoft API Gateway and integration
* ServiceNow integration
* Docker
* Kubernetes and Helm
* Terraform
* AWS
* CI/CD
* Additional network automation integrations

---

# 2. Business Concept

Large enterprises often manage thousands of routers, switches, load balancers, DNS services, IP addresses, and other network infrastructure components.

Many network operations still depend on manual processes, scripts, tickets, and multiple management platforms.

This project demonstrates how a self-service automation platform could provide a centralized interface for network operations.

A user can currently:

* View registered network devices
* Add devices
* Update device information
* Delete devices
* Run network health checks
* See whether devices are online or offline
* Store network inventory persistently
* Run network checks through Java or Python automation

The long-term goal is to evolve the application into an enterprise network automation platform where internal and external systems can interact through governed APIs.

---

# 3. Current Architecture

The current application contains multiple layers.

```text
                Next.js Web Application
                         |
                         |
                      REST API
                         |
                         v
                Spring Boot Backend
                         |
                  DeviceController
                         |
              -----------------------
              |                     |
       DeviceService       DeviceHealthCheckService
              |                     |
              |              CompletableFuture
              |                     |
              |            ThreadPoolTaskExecutor
              |                     |
              |             Concurrent Device Checks
              |                     |
              -----------+-----------
                         |
                  DeviceRepository
                         |
                  Spring Data JPA
                         |
                     Hibernate
                         |
                     PostgreSQL
```

Python provides an additional automation client:

```text
Python Automation
       |
       | GET / PATCH
       v
Spring Boot REST API
       |
DeviceController
       |
DeviceService
       |
DeviceRepository
       |
PostgreSQL
```

---

# 4. Application Layers

## Next.js Frontend

The Next.js frontend provides the user interface.

Current capabilities include:

* Display device inventory
* Add devices
* Edit devices
* Delete devices
* Run health checks
* Display ONLINE, OFFLINE, and UNKNOWN states
* Display latency
* Display backend errors
* Show loading state while health checks are running

The frontend currently runs locally on:

```text
http://localhost:3000
```

---

## Spring Boot Backend

Spring Boot provides the REST API and business logic.

The backend currently runs locally on:

```text
http://localhost:8080
```

Main layers:

```text
Controller
    |
Service
    |
Repository
    |
Database
```

---

## DeviceController

`DeviceController` exposes REST endpoints to clients such as:

* Next.js
* Python
* curl
* Web browsers
* Future Angular applications
* Future MuleSoft integrations

---

## DeviceService

`DeviceService` contains device business logic including:

* Retrieve devices
* Retrieve a device by IP address
* Create a device
* Update a device
* Partially update a device
* Delete a device
* Check for duplicate devices

---

## DeviceRepository

`DeviceRepository` is the persistence repository.

It extends:

```java
JpaRepository<Device, String>
```

The persistence stack is:

```text
DeviceRepository
       |
Spring Data JPA
       |
JPA
       |
Hibernate
       |
JDBC
       |
PostgreSQL
```

Spring Data JPA provides repository abstractions while Hibernate acts as the JPA ORM implementation.

---

# 5. Device Model

The current device model contains:

```text
hostname
ip
status
latency
deviceType
```

The IP address currently acts as the device identifier.

Example:

```json
{
  "hostname": "router-prod-01",
  "ip": "10.10.20.15",
  "status": "ONLINE",
  "latency": 12,
  "deviceType": "Router"
}
```

---

# 6. REST API Endpoints

## Retrieve All Devices

```text
GET /api/devices
```

Example:

```bash
curl http://localhost:8080/api/devices
```

---

## Retrieve One Device

```text
GET /api/devices/{ip}
```

Example:

```bash
curl http://localhost:8080/api/devices/127.0.0.1
```

---

## Create Device

```text
POST /api/devices
```

Example:

```bash
curl -X POST http://localhost:8080/api/devices \
  -H "Content-Type: application/json" \
  -d '{
    "hostname": "local-mac",
    "ip": "127.0.0.1",
    "status": "UNKNOWN",
    "latency": 0,
    "deviceType": "Local Host"
  }'
```

---

## Update Device

```text
PUT /api/devices/{ip}
```

---

## Partially Update Device

```text
PATCH /api/devices/{ip}
```

---

## Delete Device

```text
DELETE /api/devices/{ip}
```

Example:

```bash
curl -X DELETE http://localhost:8080/api/devices/127.0.0.1
```

---

## Run Health Check

```text
GET /api/devices/health-check
```

Example:

```bash
curl http://localhost:8080/api/devices/health-check
```

---

# 7. Java Concurrent Health Check

One of the important backend features is concurrent device health checking.

Instead of checking devices sequentially, the application uses Java concurrency.

```text
GET /api/devices/health-check
        |
        v
DeviceController
        |
        v
DeviceHealthCheckService
        |
        v
CompletableFuture
        |
        v
ThreadPoolTaskExecutor
        |
   +----+----+----+
   |         |    |
Device 1 Device 2 Device 3
   |         |    |
   +----+----+----+
        |
DeviceService
        |
DeviceRepository
        |
PostgreSQL
```

This demonstrates:

* Java multithreading
* Asynchronous execution
* Thread pools
* `CompletableFuture`
* Concurrent network operations

---

# 8. Python Network Automation

The project also contains Python-based automation.

The Python script retrieves devices from the Spring Boot API, performs network checks, and updates the backend.

```text
Python
   |
GET /api/devices
   |
Spring Boot
   |
Device Inventory
   |
Python ping
   |
ONLINE / OFFLINE
   |
PATCH /api/devices/{ip}
   |
Spring Boot
   |
PostgreSQL
```

This demonstrates that Spring Boot acts as a reusable API platform rather than being tied only to the Next.js frontend.

---

# 9. Repository Structure

The planned repository structure is:

```text
enterprise-network-automation/
|
|-- network-api/
|   Spring Boot backend
|
|-- python-automation/
|   Python network automation
|
|-- nextjs-portal/
|   Next.js self-service frontend
|
|-- angular-dashboard/
|   Future Angular operations dashboard
|
|-- terraform/
|   Future Infrastructure as Code
|
|-- openshift/
|   Future OpenShift manifests
|
|-- jenkins/
|   Future CI/CD configuration
|
`-- docs/
    Architecture and project documentation
```

---

# 10. Development Environment

The project was originally developed on an Apple Silicon Mac.

Current development environment:

```text
macOS 26.6.2
Apple Silicon / arm64
Java 21
Python 3.14
Node.js 26
npm
Maven
PostgreSQL 17
Git
VS Code
```

Exact versions are not necessarily required as long as compatible supported versions are installed.

---

# 11. Mac Development Setup

## Install Apple Command Line Tools

Check whether they are installed:

```bash
xcode-select -p
```

If they are not installed:

```bash
xcode-select --install
```

---

# 12. Install Homebrew

Check:

```bash
brew --version
```

If Homebrew is already installed, continue to the next step.

Homebrew is used to install development dependencies such as Java, PostgreSQL, Maven, and Node.js.

---

# 13. Install Git

Check:

```bash
git --version
```

Git is used for source control and GitHub integration.

---

# 14. Install Java 21

Install:

```bash
brew install openjdk@21
```

Verify:

```bash
java -version
```

Expected major version:

```text
21
```

Check Java compiler:

```bash
javac -version
```

---

# 15. Install Maven

Install:

```bash
brew install maven
```

Verify:

```bash
mvn -version
```

The Spring Boot project also includes the Maven Wrapper, so Maven commands can normally be executed with:

```bash
./mvnw
```

---

# 16. Install Node.js and npm

Install Node.js:

```bash
brew install node
```

Verify:

```bash
node --version
npm --version
```

Node.js is required for the Next.js application.

---

# 17. Install Python

Install Python:

```bash
brew install python
```

Verify:

```bash
python3 --version
```

Check its location:

```bash
which python3
```

On Apple Silicon with Homebrew it may appear under:

```text
/opt/homebrew/bin/python3
```

---

# 18. Install PostgreSQL

Install PostgreSQL 17:

```bash
brew install postgresql@17
```

Start PostgreSQL:

```bash
brew services start postgresql@17
```

Check service status:

```bash
brew services list
```

Connect to PostgreSQL:

```bash
psql postgres
```

---

# 19. Create PostgreSQL Database

Create the application database:

```sql
CREATE DATABASE networkdb;
```

Create an application user:

```sql
CREATE USER networkapp WITH PASSWORD 'your-local-development-password';
```

Grant access:

```sql
GRANT ALL PRIVILEGES ON DATABASE networkdb TO networkapp;
```

If required, connect to the database and grant schema permissions.

```sql
\c networkdb
```

Then:

```sql
GRANT ALL ON SCHEMA public TO networkapp;
```

Do not commit real production passwords to GitHub.

---

# 20. Clone the Repository

Clone:

```bash
git clone https://github.com/rajpandya/enterprise-network-automation.git
```

Enter the repository:

```bash
cd enterprise-network-automation
```

---

# 21. Configure Spring Boot Database

Navigate to:

```bash
cd network-api
```

The application requires PostgreSQL configuration.

For local development, the application needs values equivalent to:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/networkdb
spring.datasource.username=networkapp
spring.datasource.password=<your-password>

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

For public or production deployments, credentials should be supplied through environment variables rather than committed to source control.

Planned configuration:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

---

# 22. Build Spring Boot

From:

```text
enterprise-network-automation/network-api
```

run:

```bash
./mvnw clean package
```

This:

```text
cleans previous build output
        |
compiles Java
        |
runs tests
        |
packages application
        |
creates JAR
```

The generated JAR is placed under:

```text
target/
```

---

# 23. Compile Without Running Full Package

Maven can compile the application with:

```bash
./mvnw compile
```

---

# 24. Run Backend Tests

Run:

```bash
./mvnw test
```

For a clean test run:

```bash
./mvnw clean test
```

The project includes Spring Boot and MockMvc tests for REST API functionality.

---

# 25. Run Spring Boot Locally

From:

```text
network-api/
```

run:

```bash
./mvnw spring-boot:run
```

Spring Boot starts its embedded Tomcat server.

The backend should become available at:

```text
http://localhost:8080
```

---

# 26. Verify Backend

Open:

```text
http://localhost:8080/api/devices
```

or run:

```bash
curl http://localhost:8080/api/devices
```

You should receive JSON.

Example:

```json
[
  {
    "hostname": "local-mac",
    "ip": "127.0.0.1",
    "status": "ONLINE",
    "latency": 1,
    "deviceType": "Local Host"
  }
]
```

---

# 27. Run Health Check Directly

Use:

```bash
curl http://localhost:8080/api/devices/health-check
```

or open:

```text
http://localhost:8080/api/devices/health-check
```

Flow:

```text
Browser / curl
      |
DeviceController
      |
DeviceHealthCheckService
      |
CompletableFuture
      |
ThreadPoolTaskExecutor
      |
Device checks
      |
DeviceService
      |
DeviceRepository
      |
PostgreSQL
      |
JSON response
```

---

# 28. Set Up Python Automation

From the repository root:

```bash
cd python-automation
```

Create a Python virtual environment:

```bash
python3 -m venv .venv
```

Activate it:

```bash
source .venv/bin/activate
```

Install required package:

```bash
pip install requests
```

The project should eventually maintain dependencies in:

```text
requirements.txt
```

Then dependencies can be installed with:

```bash
pip install -r requirements.txt
```

---

# 29. Run Python Network Automation

Make sure Spring Boot is already running on port 8080.

Then:

```bash
python3 device_check.py
```

The Python script communicates with:

```text
http://localhost:8080/api/devices
```

It retrieves devices, performs health checks, and updates device information through the Spring Boot REST API.

---

# 30. Set Up Next.js Frontend

From the repository root:

```bash
cd nextjs-portal
```

Install JavaScript dependencies:

```bash
npm install
```

---

# 31. Configure Next.js API URL

Create:

```text
nextjs-portal/.env.local
```

Add:

```text
NEXT_PUBLIC_API_URL=http://localhost:8080
```

This allows the frontend to communicate with the Spring Boot backend.

Do not commit `.env.local` if it later contains sensitive configuration.

---

# 32. Run Next.js Locally

Start the development server:

```bash
npm run dev
```

The frontend should be available at:

```text
http://localhost:3000
```

---

# 33. Local Application Startup Order

For normal development, start the services in this order.

### Terminal 1: PostgreSQL

Verify PostgreSQL is running:

```bash
brew services list
```

If necessary:

```bash
brew services start postgresql@17
```

---

### Terminal 2: Spring Boot

```bash
cd ~/Projects/enterprise-network-automation/network-api

./mvnw spring-boot:run
```

Verify:

```text
http://localhost:8080/api/devices
```

---

### Terminal 3: Next.js

```bash
cd ~/Projects/enterprise-network-automation/nextjs-portal

npm run dev
```

Open:

```text
http://localhost:3000
```

---

### Optional Terminal 4: Python Automation

```bash
cd ~/Projects/enterprise-network-automation/python-automation

source .venv/bin/activate

python3 device_check.py
```

---

# 34. Complete Local Runtime Architecture

When everything is running locally:

```text
Browser
   |
   v
Next.js
localhost:3000
   |
   | REST
   v
Spring Boot
localhost:8080
   |
   v
Spring Data JPA / Hibernate
   |
   v
PostgreSQL
localhost:5432
```

Python can independently call Spring Boot:

```text
Python
   |
   | REST
   v
Spring Boot :8080
   |
   v
PostgreSQL
```

---

# 35. CORS

Next.js and Spring Boot run on different origins:

```text
Next.js
http://localhost:3000

Spring Boot
http://localhost:8080
```

Because the ports are different, browsers treat these as different origins.

Spring Boot therefore needs CORS configuration allowing the frontend origin.

The current development configuration allows:

```text
http://localhost:3000
```

Production CORS configuration will later be centralized and controlled through environment-specific settings.

---

# 36. Build Next.js

Before committing or deploying frontend changes, run:

```bash
npm run build
```

This performs the production Next.js build and catches TypeScript and build-time errors.

---

# 37. Run Complete Project Validation

Before pushing major changes to GitHub, run backend tests:

```bash
cd network-api

./mvnw clean test
```

Then run frontend build:

```bash
cd ../nextjs-portal

npm run build
```

Both should complete successfully.

---

# 38. Important Git Ignore Rules

The following development files should not be committed:

```text
.venv/
__pycache__/
*.pyc

node_modules/
.env.local

target/
```

Sensitive credentials should also never be committed.

Before every major commit:

```bash
git status
```

Review exactly what Git is going to include.

---

# 39. Git Development Workflow

Example feature workflow:

```bash
git checkout main

git pull

git checkout -b feature/example-feature
```

Make changes.

Check:

```bash
git status
```

Stage:

```bash
git add .
```

Commit:

```bash
git commit -m "Add example feature"
```

Push:

```bash
git push origin feature/example-feature
```

Then create a pull request and merge after validation.

---

# 40. Current End-to-End Request Flow

For normal device operations:

```text
Next.js
   |
HTTP REST
   |
DeviceController
   |
DeviceService
   |
DeviceRepository
   |
Spring Data JPA
   |
Hibernate
   |
PostgreSQL
   |
JSON Response
   |
Next.js State
   |
React UI Re-render
```

For Java health checks:

```text
Next.js
   |
GET /api/devices/health-check
   |
DeviceController
   |
DeviceHealthCheckService
   |
DeviceService.getAllDevices()
   |
PostgreSQL
   |
CompletableFuture
   |
ThreadPoolTaskExecutor
   |
Concurrent Network Checks
   |
DeviceService.updateDevice()
   |
DeviceRepository
   |
PostgreSQL
   |
JSON Response
   |
Next.js
   |
Updated Dashboard
```

For Python:

```text
Python
   |
GET /api/devices
   |
Spring Boot
   |
PostgreSQL
   |
Device list
   |
Python ping
   |
PATCH /api/devices/{ip}
   |
Spring Boot
   |
PostgreSQL
```

---

# 41. Current Project Status

Completed:

```text
Java 21 / Spring Boot backend
REST CRUD APIs
Spring Data JPA
Hibernate
PostgreSQL persistence
Validation
Global exception handling
MockMvc API testing
Python automation
Java multithreaded health checking
CompletableFuture
ThreadPoolTaskExecutor
Next.js frontend
TypeScript
Device CRUD UI
Health-check UI
Error handling
Environment-based frontend API URL
CORS configuration
```

---

# 42. Next Major Development Phases

The next priority is enterprise platform integration.

## Phase 1: Red Hat OpenShift

The Spring Boot application will be containerized and deployed using OpenShift concepts including:

```text
Container Image
Pod
Deployment
Service
Route
ConfigMap
Secret
Health Probes
Scaling
Rolling Deployment
```

Target runtime flow:

```text
Client
   |
OpenShift Route
   |
Service
   |
Spring Boot Pod(s)
```

---

## Phase 2: Angular Network Operations Dashboard

A separate Angular frontend will provide an operations-focused dashboard.

Planned capabilities include:

```text
Total devices
Online devices
Offline devices
Latency
Health status
Failed checks
Network inventory
```

Architecture:

```text
Angular Component
       |
Angular Service
       |
HttpClient
       |
Spring Boot REST API
```

This phase will demonstrate:

```text
Angular Components
Services
Dependency Injection
HttpClient
RxJS
Observables
Routing
Forms
Environment Configuration
```

---

## Phase 3: MuleSoft API Gateway and Integration

MuleSoft will be introduced as an enterprise API and integration layer.

Planned architecture:

```text
Next.js
    \
Angular
      \
External Client
        \
         v
     MuleSoft
 API Gateway / Integration
         |
         v
     Spring Boot
         |
         v
     PostgreSQL
```

MuleSoft will be used to explore:

```text
Internal APIs
External APIs
Authentication
Authorization
API policies
Rate limiting
Transformation
Routing
Versioning
Logging
Monitoring
Service integration
```

Future integrations may include ServiceNow, IPAM, DNS, DHCP, F5, and other network-management platforms.

---

# 43. Long-Term Target Architecture

```text
                       Internal Users
                              |
               +--------------+--------------+
               |                             |
          Next.js Portal              Angular Dashboard
               |                             |
               +--------------+--------------+
                              |
                              v
                         MuleSoft
                    API / Integration Layer
                              |
                              v
                       OpenShift Route
                              |
                              v
                           Service
                              |
                    +---------+---------+
                    |                   |
              Spring Boot Pod     Spring Boot Pod
                    |                   |
                    +---------+---------+
                              |
                    Network Automation
                         /          \
                        /            \
             Java Automation     Python Automation
                       |
             External Network Systems
                       |
                       v
                   PostgreSQL
```

Infrastructure and deployment will later include:

```text
Red Hat OpenShift
Docker / OCI Containers
Terraform
AWS
CI/CD
Jenkins / GitHub
Helm
ServiceNow
MuleSoft
Monitoring
Secrets Management
```

---

# 44. Purpose of This Repository

This repository is intended both as a working application and as a hands-on demonstration of enterprise engineering concepts including:

* Full-stack application development
* Java and Spring Boot architecture
* RESTful API design
* Database persistence
* ORM using JPA and Hibernate
* Java concurrency
* Network automation
* Python integration
* React and Next.js
* Enterprise API integration
* Container orchestration
* Cloud infrastructure
* Infrastructure as Code
* CI/CD
* Observability
* Enterprise network operations
