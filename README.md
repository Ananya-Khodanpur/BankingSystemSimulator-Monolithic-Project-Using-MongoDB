# 💳 Banking System Simulator – Spring Boot + MongoDB

A monolithic Spring Boot backend application that simulates a banking system with features like account creation, deposits, withdrawals, transfers, and transaction history.

This project is part of the **Java Full Stack Development Program – Virtusa (Phase-1 Mini Project)**.

---

# 🧾 Table of Contents
1. Overview  
2. Features  
3. Architecture Overview  
4. Technologies Used  
5. Project Structure  
6. API Documentation  
7. Setup Instructions  
8. Running the Application  
9. Testing & Code Coverage  
10. Sample Postman Requests  
11. Author  

---

# 📌 1. Overview

This backend system provides essential banking operations:

- Create accounts  
- Deposit / Withdraw money  
- Transfer funds  
- Store and fetch transaction history  
- Get / Update / Delete accounts  

The system uses **MongoDB** as the data store, includes **global exception handling**, **DTO-based requests**, and **unit tests** with **Jacoco coverage**.

---

# 🌟 2. Features

### 👤 Account Management
- Create new accounts  
- Auto-generate account numbers  
- Update account details  
- Delete account  
- View single or all accounts  

### 💰 Transaction Features
- Deposit money  
- Withdraw money  
- Transfer between accounts  
- Track transaction history  

### 🛡 Exception Handling
- AccountNotFoundException  
- InvalidAmountException  
- InsufficientBalanceException  
- Global exception handler  

### 🧪 Testing
- JUnit + Mockito service-layer tests  
- Jacoco coverage report  

---

# 🏗 3. Architecture Overview

Client (Postman/UI)
|
v
REST Controller → Service Layer → Repository → MongoDB
| |
DTO Validation Business Logic
|
Global Exception Handler


---

# 🗂 4. Technologies Used

| Technology | Purpose |
|------------|----------|
| Java 17+ | Programming Language |
| Spring Boot | Application Framework |
| Spring Web | REST APIs |
| Spring Data MongoDB | Database Interaction |
| MongoDB | NoSQL Database |
| JUnit + Mockito | Unit Testing |
| Jacoco | Test Coverage |
| Maven | Build Tool |

---

# 📁 5. Project Structure

com.bankingsystem
│
├── controller # REST Controllers
├── service # Business logic
├── repository # MongoDB data access
├── model # Account, Transaction documents + DTOs
├── exception # Custom exceptions + global handler
└── config # Optional configs (CORS / Mongo configs)

# 📚 6. API Documentation

Below are the REST endpoints for the Banking System.

Create Account
POST /api/accounts

Request Body: { “holderName”: “Ananya” }

➤ Get Account by Account Number
GET /api/accounts/{accountNumber}

Update Account
PUT /api/accounts/{accountNumber}

Request Body: { “holderName”: “New Name”, “status”: “INACTIVE” }

Delete Account
DELETE /api/accounts/{accountNumber}

Get All Accounts
GET /api/accounts

Transaction APIs
Deposit Amount
PUT /api/accounts/{accountNumber}/deposit

Request Body: { “amount”: 500 }

Withdraw Amount
PUT /api/accounts/{accountNumber}/withdraw

Request Body: { “amount”: 200 }

Transfer Amount
POST /api/accounts/transfer

Request Body: { “sourceAccount”: “ANN1234”, “destinationAccount”:
“JOH5678”, “amount”: 100 }

Get All Transactions for an Account
GET /api/accounts/{accountNumber}/transactions

# 🛠 7. Setup Instructions
✔ Prerequisites

Java 17+

Maven 3+

MongoDB installed and running on:
mongodb://localhost:27017

✔ Clone the project
git clone https://github.com/<your-username>/banking-system-mongodb.git
cd banking-system-mongodb

✔ Configure MongoDB

Edit
application.properties:
spring.data.mongodb.database=bankdb
spring.data.mongodb.uri=mongodb://localhost:27017/bankdb

# 🚀 8. Running the Application
mvn spring-boot:run
Application will start on:

👉 http://localhost:8080

# 🧪 9. Testing & Coverage
Run Unit Tests
mvn test

Generate Coverage Report (Jacoco)
target/site/jacoco/index.html

<img width="2876" height="622" alt="image" src="https://github.com/user-attachments/assets/313a8de8-4369-490f-87fe-75205dd56647" />


# 📬 10. Sample Postman Collection (Optional)

You may export your Postman collection and include it as:
postman_collection.json

# ✍️ 11. Author

Ananya Khodanpur
Java Full Stack Developer – Virtusa FSD Program
GitHub: https://github.com/Ananya-Khodanpur


