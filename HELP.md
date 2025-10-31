# 🧾 Order Processing System

A **Spring Boot 2.7.x** backend service for managing orders in the **eMart Order Processing System**.  
This application demonstrates a RESTful microservice architecture with CRUD operations for orders,  
H2 in-memory database integration, Swagger-based API documentation, and DevTools for live reload.

---

## ⚙️ Tech Stack

| Component | Technology |
|------------|-------------|
| Language | Java 11 |
| Framework | Spring Boot 2.7.18 |
| Database | H2 (In-memory) |
| ORM | Spring Data JPA |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Validation | Jakarta Validation (JSR-380) |
| Build Tool | Gradle |
| Testing | JUnit 5 |
| Hot Reload | Spring Boot DevTools |

---

## 📁 Project Structure
com.emart.ops
├── config
│ └── SwaggerConfig.java
├── controller
│ └── OrderController.java
├── dto
│ └── CreateOrderRequest.java
├── exception
│ ├── GlobalExceptionHandler.java
│ └── ResourceNotFoundException.java
├── model
│ ├── Order.java
│ ├── OrderItem.java
│ └── OrderStatus.java
├── repository
│ └── OrderRepository.java
├── service
│ └── OrderService.java
├── OrderProcessingSystemApplication.java
└── test
├── OrderServiceTest.java
├── OrderControllerTest.java
└── OrderProcessingSystemApplicationTests.java


---

## 🚀 Getting Started

### 1️⃣ Prerequisites

- Java 11 or higher  
- Gradle 7.x or higher  
- IDE (IntelliJ / Eclipse / VS Code)

---

### 2️⃣ Clone the repository

```bash
git clone https://github.com/<your-username>/OrderProcessingSystem.git
cd OrderProcessingSystem


