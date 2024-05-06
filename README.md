Prerequisites
Before you begin, ensure you have the following installed:

Java JDK 11 or newer
Gradle (for building the project)
PostgreSQL (for the database)
Installation
Step 1: Install PostgreSQL
To install PostgreSQL on Windows, follow these steps:

Download the Windows installer from the PostgreSQL official site.
Run the installer and follow the on-screen instructions to install PostgreSQL.
Set a password for the database superuser (postgres) when prompted.
Make a note of the port number if you change it from the default 5432.
Choose the default options for the rest of the installation steps.
Step 2: Clone the Repository
Clone this repository to your local machine:

bash
Copy code
git clone [repository-url]
cd [local-repository]
Step 3: Configure Application Properties
Navigate to src/main/resources/ and update the application.properties file with your PostgreSQL credentials and database details:

properties
Copy code
spring.datasource.url=jdbc:postgresql://localhost:5432/auth_service_db
spring.datasource.username=[your-username]
spring.datasource.password=[your-password]
# Replace [your-username] and [your-password] with your PostgreSQL credentials
Step 4: Build the Project
Build the project using Gradle:

bash
Copy code
gradle build
Step 5: Run the Application
Run the Spring Boot application using Gradle:

bash
Copy code
gradle bootRun
API Endpoints
The service defines the following REST endpoints:

POST /auth/register: Register a new user.
POST /auth/login: Login an existing user and receive a JWT.
GET /auth/me: Retrieve the current user's details based on the provided JWT.
POST /auth/logout: Logout the user by invalidating the JWT.
Testing the API
You can test the API using tools like Postman or cURL by sending requests to the endpoints defined above.
