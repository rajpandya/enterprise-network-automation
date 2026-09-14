# Enterprise Network Automation Self-Service Platform**

## 1. Project Overview**

The Enterprise Network Automation Self-Service Platform is a portfolio project designed to demonstrate how modern enterprise applications can combine network automation, REST APIs, Java concurrency, Python automation, persistent data storage, and web-based self-service capabilities.

The application currently provides a centralized network device inventory where users can create, view, update, delete, and health-check network devices.

The project is being developed incrementally with an enterprise architecture in mind.

The current implementation includes:

* Java 21 / Spring Boot
* Spring Web REST APIs
* Spring Data JPA / Hibernate
* PostgreSQL
* Java `CompletableFuture` and `ThreadPoolTaskExecutor`
* Python network automation
* Next.js / React / TypeScript self-service portal
* Angular 22 operations dashboard
* Angular `HttpClient`, RxJS, dependency injection, SSR and hydration
* Docker / OCI containerization
* Multi-architecture image builds with Docker Buildx
* Red Hat OpenShift Deployment, Pod, Service, Route, ConfigMap, Secret, and PVC
* PostgreSQL on OpenShift with persistent storage
* Spring Boot Actuator readiness and liveness probes
* Maven
* Git and GitHub

Next major phases will add:

* MuleSoft API Gateway and integration
* ServiceNow integration
* Terraform / AWS infrastructure
* CI/CD automation
* Helm packaging
* Additional network automation integrations

---

# 2. Business Concept**

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

# 3. Current Architecture**

The project now has two web frontends, a reusable Spring Boot API, Java and Python automation, and an OpenShift-hosted backend/database environment.

```text
        +---------------------+       +---------------------+
        |   Next.js Portal    |       | Angular Dashboard   |
        |   localhost:3000    |       |   localhost:4200    |
        +----------+----------+       +----------+----------+
                   |                             |
                   +-------------+---------------+
                                 |
                                 | REST
                                 v
                        OpenShift Route
                                |
                                v
                     network-api-service
                                |
                                v
                      Spring Boot API Pod
                                |
                         DeviceController
                                |
                +---------------+---------------+
                |                               |
         DeviceService              DeviceHealthCheckService
                |                               |
                |                     CompletableFuture
                |                               |
                |                    ThreadPoolTaskExecutor
                |                               |
                +---------------+---------------+
                                |
                        DeviceRepository
                                |
                        Spring Data JPA
                                |
                            Hibernate
                                |
                                v
                     network-postgres Service
                                |
                                v
                        PostgreSQL Pod
                                |
                                v
                    PersistentVolumeClaim
```

Python provides an additional automation client that consumes the same REST API:

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

Current Angular/OpenShift request flow:

```text
Angular Dashboard
       |
       v
OpenShift Route
       |
       v
Spring Boot
       |
       v
PostgreSQL
```

The next architectural step is to insert MuleSoft between the frontend/API consumers and the OpenShift Route.

---

# 4. Application Layers**

## Next.js Frontend**

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

## Angular Operations Dashboard**

The Angular 22 dashboard provides an operations-focused view of device inventory and health data.

Current capabilities include:

* Display network inventory returned by Spring Boot
* Calculate total, online, and offline device counts
* Calculate average latency
* Refresh inventory using Angular event binding
* Invoke the Java multithreaded health-check endpoint
* Display loading and error states
* Apply conditional status classes
* Use Angular services, dependency injection, `HttpClient`, RxJS, and Observables
* Preserve SSR and hydration support
* Use environment-based backend API configuration
* Connect through the OpenShift Route
* Produce a validated production build with `ng build`

The Angular development server runs locally on:

```text
http://localhost:4200
```

The backend is currently accessed through the OpenShift Route rather than requiring Spring Boot to run locally.

---

## Spring Boot Backend**

Spring Boot provides the REST API and business logic.

The backend can run locally on:

```text
http://localhost:8080
```

The interview-ready deployment is also running in OpenShift and exposed through an OpenShift Route.

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

## DeviceController**

`DeviceController` exposes REST endpoints to clients such as:

* Next.js

* Python

* curl

* Web browsers

* Angular operations dashboard

* Future MuleSoft integrations

---

## DeviceService**

`DeviceService` contains device business logic including:

* Retrieve devices

* Retrieve a device by IP address

* Create a device

* Update a device

* Partially update a device

* Delete a device

* Check for duplicate devices

---

## DeviceRepository**

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

# 5. Device Model**

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

# 6. REST API Endpoints**

## Retrieve All Devices**

```text

GET /api/devices

```

Example:

```bash

curl http://localhost:8080/api/devices

```

---

## Retrieve One Device**

```text

GET /api/devices/{ip}

```

Example:

```bash

curl http://localhost:8080/api/devices/127.0.0.1

```

---

## Create Device**

```text

POST /api/devices

```

Example:

```bash

curl -X POST http://localhost:8080/api/devices \\

  -H "Content-Type: application/json" \\

  -d '{

    "hostname": "local-mac",

    "ip": "127.0.0.1",

    "status": "UNKNOWN",

    "latency": 0,

    "deviceType": "Local Host"

  }'

```

---

## Update Device**

```text

PUT /api/devices/{ip}

```

---

## Partially Update Device**

```text

PATCH /api/devices/{ip}

```

---

## Delete Device**

```text

DELETE /api/devices/{ip}

```

Example:

```bash

curl -X DELETE http://localhost:8080/api/devices/127.0.0.1

```

---

## Run Health Check**

```text

GET /api/devices/health-check

```

Example:

```bash

curl http://localhost:8080/api/devices/health-check

```

---

# 7. Java Concurrent Health Check**

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

# 8. Python Network Automation**

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

# 9. Repository Structure**

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

|   Angular network operations dashboard

|

|-- terraform/

|   Future Infrastructure as Code

|

|-- openshift/

|   OpenShift deployment, service, route, configuration, and persistence manifests

|

|-- jenkins/

|   Future CI/CD configuration

|

`-- docs/

    Architecture and project documentation

```

---

# 10. Development Environment**

The project was originally developed on an Apple Silicon Mac.

Current development environment:

```text

macOS 26.6.2

Apple Silicon / arm64

Java 21

Python 3.14

Node.js 26

npm 11

Angular CLI 22

Maven

PostgreSQL 17

Docker Desktop / Docker Buildx

OpenShift CLI (`oc`)

Git

VS Code

```

Exact versions are not necessarily required as long as compatible supported versions are installed.

---

# 11. Mac Development Setup**

## Install Apple Command Line Tools**

Check whether they are installed:

```bash

xcode-select -p

```

If they are not installed:

```bash

xcode-select --install

```

---

# 12. Install Homebrew**

Check:

```bash

brew --version

```

If Homebrew is already installed, continue to the next step.

Homebrew is used to install development dependencies such as Java, PostgreSQL, Maven, and Node.js.

---

# 13. Install Git**

Check:

```bash

git --version

```

Git is used for source control and GitHub integration.

---

# 14. Install Java 21**

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

# 15. Install Maven**

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

# 16. Install Node.js and npm**

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

# 17. Install Python**

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

# 18. Install PostgreSQL**

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

# 19. Create PostgreSQL Database**

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

# 20. Clone the Repository**

Clone:

```bash

git clone https://github.com/rajpandya/enterprise-network-automation.git

```

Enter the repository:

```bash

cd enterprise-network-automation

```

---

# 21. Configure Spring Boot Database**

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

# 22. Build Spring Boot**

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

# 23. Compile Without Running Full Package**

Maven can compile the application with:

```bash

./mvnw compile

```

---

# 24. Run Backend Tests**

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

# 25. Run Spring Boot Locally**

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

# 26. Verify Backend**

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

# 27. Run Health Check Directly**

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

# 28. Set Up Python Automation**

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

# 29. Run Python Network Automation**

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

# 30. Set Up Next.js Frontend**

From the repository root:

```bash

cd nextjs-portal

```

Install JavaScript dependencies:

```bash

npm install

```

---

# 31. Configure Next.js API URL**

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

# 32. Run Next.js Locally**

Start the development server:

```bash

npm run dev

```

The frontend should be available at:

```text

http://localhost:3000

```

---

# 33. Local Application Startup Order**

For normal development, start the services in this order.

### Terminal 1: PostgreSQL**

Verify PostgreSQL is running:

```bash

brew services list

```

If necessary:

```bash

brew services start postgresql@17

```

---

### Terminal 2: Spring Boot**

```bash

cd \~/Projects/enterprise-network-automation/network-api

./mvnw spring-boot:run

```

Verify:

```text

http://localhost:8080/api/devices

```

---

### Terminal 3: Next.js**

```bash

cd \~/Projects/enterprise-network-automation/nextjs-portal

npm run dev

```

Open:

```text

http://localhost:3000

```

---

### Optional Terminal 4: Python Automation**

```bash

cd \~/Projects/enterprise-network-automation/python-automation

source .venv/bin/activate

python3 device_check.py

```

---

# 34. Complete Local Runtime Architecture**

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

# 35. CORS**

Next.js and Spring Boot run on different origins:

```text

Next.js

http://localhost:3000

Spring Boot

http://localhost:8080

```

Because the ports are different, browsers treat these as different origins.

Spring Boot therefore needs CORS configuration allowing the frontend origin.

The current development configuration allows both local web applications:

```text
http://localhost:3000   # Next.js
http://localhost:4200   # Angular
```

During Angular/OpenShift integration, the browser returned HTTP 403 because `http://localhost:4200` was not initially included in the Spring Boot CORS configuration. The controller was updated to allow both origins and a new backend container image was deployed to OpenShift.

Example development configuration:

```java
@CrossOrigin(origins = {
    "http://localhost:3000",
    "http://localhost:4200"
})
```

For a production implementation, CORS should be centralized and driven by environment-specific configuration rather than controller-level hardcoding.

---

# 36. Build Next.js**

Before committing or deploying frontend changes, run:

```bash

npm run build

```

This performs the production Next.js build and catches TypeScript and build-time errors.

---

# 37. Run Complete Project Validation**

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

# 38. Important Git Ignore Rules**

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

# 39. Git Development Workflow**

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

# 40. Current End-to-End Request Flow**

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

## Implemented and Validated

```text
Java 21 / Spring Boot backend
REST CRUD APIs
Spring Data JPA / Hibernate
PostgreSQL persistence
Validation and global exception handling
MockMvc API testing
Java CompletableFuture / ThreadPoolTaskExecutor health checks
Python network automation
Next.js / React / TypeScript frontend
Device CRUD and health-check UI
Environment-based frontend API configuration
Docker containerization
Container registry publishing
AMD64 target image builds from Apple Silicon
Red Hat OpenShift deployment
Deployment / Pod / Service / Route
ConfigMap and Secret based runtime configuration
PostgreSQL on OpenShift
PersistentVolumeClaim backed database storage
Persistence validation across pod recreation
Spring Boot Actuator
Readiness and liveness probes
OpenShift troubleshooting and rollout operations
Angular 22 standalone operations dashboard
Angular services and dependency injection
Angular HttpClient / RxJS Observable integration
Angular SSR and hydration
Angular device inventory and dashboard metrics
Angular refresh and Java health-check integration
Angular loading and error states
Angular conditional status class binding
Angular environment-based API configuration
Angular production build validation
Browser-to-OpenShift CORS integration
```

## Roadmap

```text
Explicit CPU and memory requests/limits
TLS-enabled OpenShift Route
Next.js deployment to OpenShift
Angular deployment to OpenShift
MuleSoft API Gateway / integration
ServiceNow integration
Terraform / AWS infrastructure
CI/CD with Jenkins / GitHub / GitLab
Helm packaging
Additional network platform integrations
```

---
# 42. Docker and Red Hat OpenShift Deployment

The backend and PostgreSQL database have been containerized and deployed to a Red Hat OpenShift Developer Sandbox. This phase moved the project from a local-only application to an orchestrated container environment with service discovery, external routing, persistent database storage, runtime configuration, and health management.

## 42.1 Deployed Architecture

```text
External Client
      |
      v
OpenShift Route
      |
      v
network-api-service
      |
      v
Spring Boot Pod
      |
      | JDBC
      v
network-postgres Service
      |
      v
PostgreSQL Pod
      |
      v
PersistentVolumeClaim
      |
      v
Persistent Storage
```

The Spring Boot API is exposed externally through an OpenShift Route. PostgreSQL remains internal to the cluster and is reached through OpenShift service discovery using `network-postgres:5432`.

## 42.2 Docker Installation and Verification

Docker Desktop was installed on the Apple Silicon development machine. Verify the installation with:

```bash
docker --version
docker info
docker buildx version
```

The local development machine is `arm64`, while the OpenShift worker environment used for this project requires an `amd64` compatible image. This difference became an important deployment consideration.

## 42.3 Build the Spring Boot Artifact

Before building the container image:

```bash
cd network-api
./mvnw clean package
```

The Maven build compiles the application, executes tests, and creates the executable Spring Boot JAR under `target/`.

## 42.4 Dockerfile

The backend container uses Java 21. The image also installs `iputils-ping` because the network health-check service executes the operating-system `ping` command.

```dockerfile
FROM eclipse-temurin:21-jre

USER root

RUN apt-get update \
    && apt-get install -y iputils-ping \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

Installing `ping` during image construction is different from running the application as root. OpenShift can still enforce its restricted security policy and run the application container with an assigned non-root UID.

## 42.5 Local Docker Validation

The backend container was first validated locally against PostgreSQL running on the Mac:

```bash
docker run --name network-api \
  -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/networkdb \
  -e DB_USERNAME=<database-user> \
  -e DB_PASSWORD=<database-password> \
  network-api:1.0
```

`host.docker.internal` is used because `localhost` inside a container refers to the container itself, not the Mac host.

Local Docker networking:

```text
Spring Boot Container
        |
        v
host.docker.internal:5432
        |
        v
PostgreSQL on Host
```

## 42.6 Publish the Container Image

Images are published to a container registry so OpenShift can pull them.

```bash
docker login
```

Because the development workstation is Apple Silicon and the target OpenShift environment requires AMD64, the deployable image is built explicitly for the target platform:

```bash
docker buildx build \
  --platform linux/amd64 \
  -t <registry-user>/network-api:<version> \
  --push .
```

Verify the published image architecture:

```bash
docker buildx imagetools inspect <registry-user>/network-api:<version>
```

The current backend image documented after the Angular CORS integration is `rajrpandya/network-api:1.4`.

## 42.7 OpenShift CLI

The OpenShift CLI was installed with Homebrew:

```bash
brew install openshift-cli
```

Verify:

```bash
oc version
```

After authenticating to the Red Hat Developer Sandbox, useful context commands include:

```bash
oc whoami
oc project
oc get pods
oc get deployments
oc get services
oc get routes
```

Authentication tokens are intentionally not documented or committed to the repository.

## 42.8 OpenShift Objects Used

The deployment uses the following OpenShift/Kubernetes resources:

```text
Deployment
Pod
Service
Route
ConfigMap
Secret
PersistentVolumeClaim
```

Their responsibilities are:

```text
Deployment -> desired application version and replica state
Pod        -> running container instance
Service    -> stable internal network endpoint
Route      -> external OpenShift endpoint
ConfigMap  -> non-sensitive runtime configuration
Secret     -> sensitive runtime configuration
PVC        -> persistent storage request
```

## 42.9 Spring Boot Runtime Configuration

The application uses environment-driven database configuration:

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/networkdb}
spring.datasource.username=${DB_USERNAME:networkapp}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
```

In OpenShift, the database URL is supplied by a ConfigMap while credentials are supplied through a Secret.

Example ConfigMap:

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: network-api-config
data:
  DB_URL: jdbc:postgresql://network-postgres:5432/networkdb
```

A public repository should not contain real secret values. A Secret can be created at deployment time:

```bash
oc create secret generic network-api-secret \
  --from-literal=DB_USERNAME=<database-user> \
  --from-literal=DB_PASSWORD=<database-password>
```

## 42.10 Spring Boot Deployment

The Spring Boot Deployment currently runs one replica and obtains its database configuration from the ConfigMap and Secret.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: network-api
spec:
  replicas: 1
  selector:
    matchLabels:
      app: network-api
  template:
    metadata:
      labels:
        app: network-api
    spec:
      containers:
        - name: network-api
          image: rajrpandya/network-api:1.4
          ports:
            - containerPort: 8080
          env:
            - name: DB_URL
              valueFrom:
                configMapKeyRef:
                  name: network-api-config
                  key: DB_URL
            - name: DB_USERNAME
              valueFrom:
                secretKeyRef:
                  name: network-api-secret
                  key: DB_USERNAME
            - name: DB_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: network-api-secret
                  key: DB_PASSWORD
```

Apply manifests with:

```bash
oc apply -f openshift/configmap.yaml
oc apply -f openshift/deployment.yaml
oc apply -f openshift/service.yaml
oc apply -f openshift/route.yaml
```

## 42.11 Service and Route

The Service provides a stable endpoint for the Spring Boot pods:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: network-api-service
spec:
  selector:
    app: network-api
  ports:
    - protocol: TCP
      port: 8080
      targetPort: 8080
```

The OpenShift Route exposes that Service externally:

```yaml
apiVersion: route.openshift.io/v1
kind: Route
metadata:
  name: network-api-route
spec:
  to:
    kind: Service
    name: network-api-service
  port:
    targetPort: 8080
```

Runtime request flow:

```text
Client
  |
  v
OpenShift Route
  |
  v
Service :8080
  |
  v
Spring Boot Pod :8080
```

## 42.12 PostgreSQL on OpenShift

PostgreSQL was moved from the local workstation into OpenShift. A Red Hat PostgreSQL image compatible with OpenShift's restricted security model was used.

The database is reachable internally through:

```text
network-postgres:5432
```

This illustrates an important difference from local Docker:

```text
Local Docker:
Spring Boot container -> host.docker.internal -> PostgreSQL on Mac

OpenShift:
Spring Boot Pod -> network-postgres Service -> PostgreSQL Pod
```

## 42.13 Persistent Storage

A PersistentVolumeClaim was added so PostgreSQL data is not tied to the lifecycle of an individual pod.

```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: network-postgres-pvc
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 1Gi
```

The PVC is mounted into PostgreSQL at:

```text
/var/lib/pgsql/data
```

The volume was attached with:

```bash
oc set volume deployment/network-postgres \
  --add \
  --name=postgres-data \
  --type=pvc \
  --claim-name=network-postgres-pvc \
  --mount-path=/var/lib/pgsql/data
```

Useful verification commands:

```bash
oc get pvc
oc describe pvc network-postgres-pvc
oc set volume deployment/network-postgres
```

The storage class used by the sandbox dynamically provisioned persistent storage. The PVC initially reported `WaitForFirstConsumer`, which is expected for storage classes that wait for a consuming pod before provisioning and binding the volume.

## 42.14 Persistence Validation

Persistence was tested rather than assumed.

A device record was created in PostgreSQL. The PostgreSQL pod was then deliberately deleted:

```bash
oc delete pod -l deployment=network-postgres
```

OpenShift recreated the PostgreSQL pod automatically. The previously created device record was still available after restart, confirming that the data lived on persistent storage rather than in the deleted pod's ephemeral filesystem.

This test demonstrated both Kubernetes/OpenShift self-healing and persistent storage behavior.

## 42.15 Spring Boot Actuator

Spring Boot Actuator was added to provide operational health endpoints.

Maven dependency:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Application configuration:

```properties
management.endpoints.web.exposure.include=health
management.endpoint.health.probes.enabled=true
```

Important endpoints:

```text
/actuator/health
/actuator/health/readiness
/actuator/health/liveness
```

Actuator belongs to the Spring Boot application. OpenShift calls these endpoints to make runtime decisions.

## 42.16 Readiness and Liveness Probes

The OpenShift Deployment uses Actuator endpoints for container health management:

```yaml
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 10
  periodSeconds: 10
  timeoutSeconds: 3
  failureThreshold: 3

livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 20
  periodSeconds: 20
  timeoutSeconds: 3
  failureThreshold: 3
```

The distinction is important:

```text
Readiness -> Should OpenShift send traffic to this pod?
Liveness  -> Is the application alive, or should OpenShift restart it?
```

Apply and verify:

```bash
oc apply -f openshift/deployment.yaml
oc get pods -w
oc describe deployment network-api
```

The deployed configuration was verified to show both readiness and liveness HTTP checks.

## 42.17 Useful OpenShift Operations and Troubleshooting Commands

```bash
# Workload status
oc get pods
oc get deployments
oc get services
oc get routes
oc get pvc

# Detailed diagnostics
oc describe pod <pod-name>
oc describe deployment network-api
oc describe pvc network-postgres-pvc

# Application logs
oc logs <pod-name>
oc logs -f <pod-name>

# Execute inside a pod
oc exec -it <pod-name> -- /bin/sh

# Restart application
oc rollout restart deployment/network-api

# Watch rollout
oc get pods -w

# Update image
oc set image deployment/network-api \
  network-api=<registry-user>/network-api:<version>

# Inspect configured volumes
oc set volume deployment/network-postgres
```

## 42.18 Deployment Challenges and Engineering Lessons

Several deployment issues were intentionally documented because troubleshooting is a critical part of operating enterprise platforms.

### Apple Silicon ARM64 vs OpenShift AMD64

**Symptom**

OpenShift could not start the original image and reported that no compatible image was available for `amd64`.

**Root cause**

Docker initially built the image for the Apple Silicon workstation's ARM64 architecture, while the OpenShift runtime required AMD64.

**Resolution**

```bash
docker buildx build \
  --platform linux/amd64 \
  -t <registry-user>/network-api:<version> \
  --push .
```

**Lesson**

Container portability still depends on CPU architecture unless the image is built for the target platform or published as a multi-architecture image.

### PostgreSQL Image and OpenShift Security

**Symptom**

The initial PostgreSQL container failed under OpenShift's restricted security policy.

**Root cause**

The image expected permissions and user behavior that conflicted with OpenShift's dynamically assigned non-root UID security model.

**Resolution**

A Red Hat PostgreSQL image designed to operate correctly under OpenShift security constraints was used.

**Lesson**

An image that works in local Docker is not automatically suitable for a hardened Kubernetes/OpenShift environment.

### `ping` Missing from the Java Runtime Image

**Symptom**

The Java health-check feature marked even `127.0.0.1` as OFFLINE inside the container.

Inspection showed that the `ping` executable was not installed.

**Root cause**

Minimal Java runtime images intentionally omit many operating-system utilities.

**Resolution**

`iputils-ping` was installed during Docker image construction.

**Lesson**

Container images contain only the dependencies explicitly included in the image. Application code that invokes operating-system tools must declare those runtime dependencies.

### New Persistent Database Had No `devices` Table

**Symptom**

After moving PostgreSQL to new PVC-backed storage, API requests returned HTTP 500 and PostgreSQL reported:

```text
relation "devices" does not exist
```

**Root cause**

The new persistent volume represented a fresh database. The running Spring Boot pod had started before the replacement database and had not initialized the schema on that new database instance.

**Resolution**

```bash
oc rollout restart deployment/network-api
```

With Hibernate schema update enabled, Spring Boot initialized the required schema.

**Lesson**

Database lifecycle and application lifecycle are separate concerns. A healthy database process does not guarantee that the application schema exists.

### PVC Initially Pending

**Symptom**

The PVC initially remained in `Pending` state and reported `WaitForFirstConsumer`.

**Root cause**

The storage class deferred volume provisioning until a pod actually requested the claim.

**Resolution**

The PVC was mounted into the PostgreSQL Deployment. The claim then bound successfully.

**Lesson**

A pending PVC is not automatically an error. StorageClass binding behavior and PVC events must be inspected before troubleshooting further.

### Actuator Route Initially Appeared Unavailable

**Symptom**

Immediately after deploying the Actuator-enabled image, the health URL temporarily returned an OpenShift application-unavailable response.

**Diagnosis**

Pod status and application logs were checked instead of assuming the deployment had failed.

```bash
oc get pods
oc logs <network-api-pod>
```

The logs confirmed Tomcat startup, PostgreSQL connectivity, and Actuator endpoint exposure. Once startup completed, `/actuator/health` returned successfully.

**Lesson**

During rolling deployment and application startup, external route availability may lag behind container creation. Pod state, readiness, and application logs provide better diagnostic evidence than a single external request.

## 42.19 Key OpenShift Concepts Demonstrated

This deployment provides hands-on examples of:

```text
Container image construction
Image registries
CPU architecture compatibility
OpenShift Deployments
Pods
Services
Routes
ConfigMaps
Secrets
Internal DNS / service discovery
PersistentVolumeClaims
Dynamic storage provisioning
Persistent database storage
Restricted container security
Rolling deployments
Pod self-healing
Spring Boot Actuator
Readiness probes
Liveness probes
Application logging
Runtime troubleshooting
```

---

# 43. Angular Network Operations Dashboard

The Angular operations dashboard is now implemented and validated against the Spring Boot backend running on OpenShift.

## 43.1 Angular Installation and Verification

Angular CLI was installed globally:

```bash
npm install -g @angular/cli
```

Verify the local development toolchain:

```bash
node --version
npm --version
ng version
```

The development environment used for this implementation included Angular CLI 22.1.8, Node.js 26.8.2, and npm 11.19.1.

## 43.2 Create the Angular Application

From the repository root:

```bash
ng new angular-dashboard
```

The application was created with CSS and server-side rendering / static generation enabled. SSR was intentionally retained.

Start the development server:

```bash
cd angular-dashboard
ng serve
```

The dashboard is available at:

```text
http://localhost:4200
```

Generated SSR-related files include:

```text
src/main.server.ts
src/server.ts
src/app/app.config.server.ts
src/app/app.routes.server.ts
```

## 43.3 Generate Angular Component, Model, and Service

Generate the operations dashboard component:

```bash
ng generate component dashboard
```

Generate the device TypeScript interface:

```bash
ng generate interface models/device
```

Generate the Angular service:

```bash
ng generate service services/device
```

The `Device` interface mirrors the Spring Boot API model:

```typescript
export interface Device {
  hostname: string;
  ip: string;
  status: string;
  latency: number;
  deviceType: string;
}
```

## 43.4 Enable Angular HttpClient

`HttpClient` is registered at application level using `provideHttpClient()` while preserving SSR hydration:

```typescript
import { provideHttpClient } from '@angular/common/http';
import { provideClientHydration } from '@angular/platform-browser';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(),
    provideClientHydration()
  ]
};
```

## 43.5 Angular Service and Dependency Injection

The Angular service encapsulates backend access rather than placing HTTP calls directly in the component.

```text
Dashboard Component
       |
       v
DeviceService
       |
       v
Angular HttpClient
       |
       v
OpenShift Route
       |
       v
Spring Boot REST API
```

Modern Angular dependency injection is used with `inject()`:

```typescript
private readonly deviceService = inject(DeviceService);
```

This resolved a runtime issue where the service reference was undefined during component initialization.

## 43.6 Device Inventory and Dashboard Metrics

The dashboard retrieves devices and calculates:

```text
Total Devices
Online Devices
Offline Devices
Average Latency
```

Angular's built-in control flow renders one inventory row per device:

```html
@for (device of devices; track device.ip) {
  <!-- device row -->
}
```

`device.ip` is used as the tracking key because IP is the backend device identifier.

## 43.7 Refresh Devices

The dashboard provides a refresh button using Angular event binding:

```html
<button type="button" (click)="loadDevices()">
  Refresh Devices
</button>
```

Flow:

```text
User Click
   |
   v
loadDevices()
   |
   v
DeviceService.getDevices()
   |
   v
GET /api/devices
   |
   v
OpenShift Spring Boot API
   |
   v
PostgreSQL
   |
   v
Angular updates the table and metrics
```

## 43.8 Run Java Health Check from Angular

The Angular dashboard also invokes the existing Java multithreaded health-check endpoint:

```text
GET /api/devices/health-check
```

The Angular service method calls the endpoint and the dashboard refreshes the returned device state.

End-to-end flow:

```text
Angular Dashboard
       |
       v
DeviceService.runHealthCheck()
       |
       v
GET /api/devices/health-check
       |
       v
OpenShift Route
       |
       v
DeviceController
       |
       v
DeviceHealthCheckService
       |
       v
CompletableFuture + ThreadPoolTaskExecutor
       |
       v
Concurrent Device Checks
       |
       v
PostgreSQL
       |
       v
Updated Angular Dashboard
```

## 43.9 Loading and Error States

The dashboard tracks request state with component properties such as:

```typescript
isLoading = false;
errorMessage = '';
```

Angular built-in conditional rendering is used for user-visible state:

```html
@if (isLoading) {
  <p>Loading devices...</p>
}

@if (errorMessage) {
  <p>{{ errorMessage }}</p>
}
```

This avoids relying only on browser-console messages for runtime failures.

## 43.10 Conditional Status Styling

Angular class binding is used to assign status-specific CSS classes:

```html
<span
  [class.online]="device.status === 'ONLINE'"
  [class.offline]="device.status === 'OFFLINE'"
  [class.unknown]="device.status !== 'ONLINE' && device.status !== 'OFFLINE'"
>
  {{ device.status }}
</span>
```

This demonstrates conditional class binding based on application state.

## 43.11 Angular Environment Configuration

The backend URL was moved out of `DeviceService` into:

```text
src/environments/environment.ts
```

Example:

```typescript
export const environment = {
  production: false,
  apiUrl:
    'http://network-api-route-raj-r-pandya-dev.apps.rm1.0a51.p1.openshiftapps.com/api/devices'
};
```

`DeviceService` then consumes:

```typescript
private readonly apiUrl = environment.apiUrl;
```

This separates environment-specific configuration from service logic.

## 43.12 Angular Production Build

Stop `ng serve` with `Control + C` and run:

```bash
cd ~/Projects/enterprise-network-automation/angular-dashboard
ng build
```

The validated production build completed successfully and generated output under:

```text
angular-dashboard/dist/angular-dashboard
```

The build produced both browser and server bundles because SSR is enabled.

A Node.js deprecation warning about `module.register()` appeared during the build, but it did not fail or invalidate the Angular build.

## 43.13 Angular and OpenShift CORS Troubleshooting

### Browser Returned HTTP 403 from OpenShift Route

**Symptom**

Angular running on `http://localhost:4200` could load the UI but browser calls to the OpenShift Route failed with:

```text
Origin http://localhost:4200 is not allowed by Access-Control-Allow-Origin.
Status code: 403
```

**Diagnosis**

The backend route was healthy because direct `curl` requests returned HTTP 200:

```bash
curl http://network-api-route-raj-r-pandya-dev.apps.rm1.0a51.p1.openshiftapps.com/api/devices
curl http://network-api-route-raj-r-pandya-dev.apps.rm1.0a51.p1.openshiftapps.com/api/devices/health-check
```

This isolated the issue to browser CORS rather than OpenShift routing, Spring Boot startup, or PostgreSQL.

**Resolution**

Spring Boot CORS configuration was updated to allow both frontend development origins:

```java
@CrossOrigin(origins = {
    "http://localhost:3000",
    "http://localhost:4200"
})
```

The backend was rebuilt and pushed as an AMD64 image:

```bash
cd ~/Projects/enterprise-network-automation/network-api
./mvnw clean package

docker buildx build \
  --platform linux/amd64 \
  -t rajrpandya/network-api:1.4 \
  --push .
```

The OpenShift manifest was updated from image `1.3` to `1.4`, then applied:

```bash
oc apply -f openshift/deployment.yaml
oc rollout status deployment/network-api
```

Verify the deployed image:

```bash
oc get deployment network-api \
  -o jsonpath='{.spec.template.spec.containers[0].image}'
```

**Lesson**

`curl` does not enforce browser CORS rules. A backend can return HTTP 200 to `curl` while a browser blocks the same endpoint. Testing both the server endpoint and the browser request helps identify the correct layer quickly.

## 43.14 Angular Development Cache Troubleshooting

### UI Reverted or Did Not Reflect the Latest Template/Data

**Symptom**

During development, a template or refreshed record appeared briefly or the application continued showing an earlier result.

**Resolution**

Stop Angular:

```text
Control + C
```

Clear the Angular development cache:

```bash
cd ~/Projects/enterprise-network-automation/angular-dashboard
rm -rf .angular/cache
```

Restart:

```bash
ng serve
```

Then perform a browser hard refresh if needed.

**Lesson**

When live development output appears inconsistent with saved source, verify the source file first, then reset the framework development cache before changing working application logic.

## 43.15 Debugging Angular API Calls

Temporary browser-console logging was used to isolate UI, service, and HTTP behavior:

```typescript
console.log('Refresh button clicked');
console.log('DeviceService.getDevices() called');
console.log('Health check response:', devices);
```

A useful diagnostic sequence is:

```text
Button event works?
       |
       v
Component method runs?
       |
       v
Service method runs?
       |
       v
Network request exists?
       |
       v
HTTP response succeeds?
       |
       v
Angular state updates?
```

The browser Network tab and `curl` were used together to distinguish application bugs from CORS and backend issues.

## 43.16 Angular Concepts Demonstrated

```text
Angular CLI
Standalone components
Component composition
TypeScript interfaces
Template interpolation
Built-in @for control flow
Built-in @if control flow
Event binding
Conditional class binding
Services
Dependency injection
HttpClient
RxJS Observables
subscribe()
OnInit lifecycle
Loading and error state
Environment configuration
SSR / hydration
REST API integration
OpenShift Route integration
Production builds
Runtime troubleshooting
```

---

# 43.17 Next Major Phase: MuleSoft API Gateway and Integration

MuleSoft is the next major implementation phase. The first example will intentionally stay small and interview-focused: route the device API through MuleSoft so the Angular dashboard no longer calls Spring Boot directly.

Target architecture:

```text
Angular Dashboard
       |
       v
MuleSoft API Gateway / Integration Layer
       |
       v
OpenShift Route
       |
       v
Spring Boot API
       |
       v
PostgreSQL
```

The first MuleSoft use case will focus on one endpoint such as:

```text
GET /api/devices
```

Potential gateway capabilities to demonstrate include API routing, a simple policy such as rate limiting or authentication, request logging, and internal/external API governance.

Future integrations may include ServiceNow, IPAM, DNS, DHCP, F5, and other network-management platforms.

---

# 44. Long-Term Target Architecture**

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

                         /          \\

                        /            \\

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

# 45. Quick Command Reference

This section summarizes commands used repeatedly during implementation and troubleshooting.

## Backend

```bash
cd ~/Projects/enterprise-network-automation/network-api
./mvnw clean test
./mvnw clean package
```

## Angular

```bash
cd ~/Projects/enterprise-network-automation/angular-dashboard
ng serve
rm -rf .angular/cache
ng build
```

## Next.js

```bash
cd ~/Projects/enterprise-network-automation/nextjs-portal
npm install
npm run dev
npm run build
```

## Python

```bash
cd ~/Projects/enterprise-network-automation/python-automation
source .venv/bin/activate
python3 device_check.py
```

## Docker

```bash
docker --version
docker info
docker buildx version

docker buildx build \
  --platform linux/amd64 \
  -t rajrpandya/network-api:<version> \
  --push .

docker buildx imagetools inspect rajrpandya/network-api:<version>
```

## OpenShift

```bash
oc whoami
oc project
oc get pods
oc get deployments
oc get services
oc get routes
oc get pvc
oc logs <pod-name>
oc describe deployment network-api
oc rollout status deployment/network-api
oc rollout restart deployment/network-api
oc apply -f openshift/deployment.yaml
```

Verify backend endpoints through the OpenShift Route:

```bash
curl http://network-api-route-raj-r-pandya-dev.apps.rm1.0a51.p1.openshiftapps.com/api/devices
curl http://network-api-route-raj-r-pandya-dev.apps.rm1.0a51.p1.openshiftapps.com/api/devices/health-check
```

## Git

```bash
git branch --show-current
git status
git add angular-dashboard
git add network-api
git add openshift/deployment.yaml
git add .vscode/settings.json
git commit -m "Add Angular network operations dashboard and OpenShift API integration"
git push -u origin feature/angular-dashboard
```

Before pushing, verify that generated files, local environments, and credentials are not staged.

Recommended ignore patterns include:

```text
node_modules/
.angular/
dist/
.env
.env.local
.venv/
__pycache__/
*.pyc
target/
```

---

# 46. Purpose of This Repository**

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