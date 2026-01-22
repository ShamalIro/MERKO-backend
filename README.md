# MERKO Backend

A comprehensive B2B E-commerce and Marketplace platform backend built with Spring Boot, providing robust APIs for multi-user roles including customers, merchants, suppliers, admins, and delivery personnel.

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Database Configuration](#database-configuration)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Project Structure](#project-structure)
- [Key Features](#key-features)
- [Configuration](#configuration)
- [File Upload](#file-upload)
- [CORS Configuration](#cors-configuration)
- [Development](#development)
- [Troubleshooting](#troubleshooting)

## Project Overview

**MERKO** is a full-featured B2B marketplace platform that connects buyers, sellers (merchants), suppliers, and delivery personnel. The backend provides REST APIs for managing products, orders, users, inquiries, and administrative operations.

## Technology Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 17 | Programming Language |
| **Spring Boot** | 3.5.5 | Framework |
| **Spring Data JPA** | Latest | ORM |
| **Spring Security** | Latest | Authentication & Authorization |
| **JWT (JJWT)** | 0.11.5 | Token-based Authentication |
| **MySQL** | 5.7+ | Database |
| **Lombok** | Latest | Reduce Boilerplate Code |
| **Maven** | 3.6+ | Build Tool |

## Prerequisites

Before running this application, ensure you have the following installed:

- **Java 17 or higher** - [Download](https://www.oracle.com/java/technologies/downloads/#java17)
- **Maven 3.6+** - [Download](https://maven.apache.org/download.cgi)
- **MySQL 5.7 or higher** - [Download](https://www.mysql.com/downloads/)
- **Git** - [Download](https://git-scm.com/)
- **IDE** (Optional) - Visual Studio Code, IntelliJ IDEA, or Eclipse



**Server will start at:** `http://localhost:8090`

## API Endpoints

### Authentication
```
POST   /api/auth/login              - User/Admin login with JWT token
```

### Products
```
GET    /api/products                 - Get all products
GET    /api/products/{id}            - Get product by ID
GET    /api/products/check-sku/{sku} - Check SKU availability
POST   /api/products/add             - Add new product (with images)
PUT    /api/products/{id}            - Update product
DELETE /api/products/{id}            - Delete product
```

### Cart
```
POST   /api/cart/add                 - Add item to cart
GET    /api/cart/{userId}            - Get user cart
PUT    /api/cart/update              - Update cart item
DELETE /api/cart/remove/{itemId}     - Remove item from cart
```

### Orders
```
POST   /api/orders                   - Create new order
GET    /api/orders/{userId}          - Get user orders
GET    /api/orders/{id}              - Get order details
PUT    /api/orders/{id}              - Update order status
DELETE /api/orders/{id}              - Cancel order
```

### Checkout
```
POST   /api/checkout                 - Process checkout
```

### Users
```
GET    /api/users/{id}               - Get user profile
PUT    /api/users/{id}               - Update user profile
POST   /api/users/register           - Register new user
GET    /api/users/search             - Search users
```

### Merchants
```
GET    /api/merchants                - Get all merchants
GET    /api/merchants/{id}           - Get merchant details
POST   /api/merchants/register       - Register merchant
PUT    /api/merchants/{id}           - Update merchant profile
```

### Admin
```
GET    /api/admin/stats              - Get dashboard statistics
GET    /api/admin/users              - Get all users
POST   /api/admin/users              - Manage users
```

### Inquiries
```
POST   /api/inquiries                - Create inquiry
GET    /api/inquiries/{id}           - Get inquiry details
POST   /api/inquiries/{id}/reply     - Reply to inquiry
```

### Delivery
```
GET    /api/delivery/orders          - Get delivery orders
PUT    /api/delivery/orders/{id}     - Update delivery status
```

## Project Structure

```
MERKO_backend/
├── src/main/
│   ├── java/com/merko/merko_backend/
│   │   ├── MerkoBackendApplication.java    # Main Spring Boot application
│   │   ├── config/                         # Configuration classes
│   │   │   ├── GlobalExceptionHandler.java # Global exception handling
│   │   │   ├── JwtFilter.java              # JWT token filter
│   │   │   ├── SecurityConfig.java         # Spring Security configuration
│   │   │   └── WebConfig.java              # Web configuration (CORS)
│   │   ├── controller/                     # REST Controllers
│   │   │   ├── AuthController.java
│   │   │   ├── ProductController.java
│   │   │   ├── CartController.java
│   │   │   ├── OrderController.java
│   │   │   ├── CheckoutController.java
│   │   │   ├── UserController.java
│   │   │   ├── MerchantController.java
│   │   │   ├── AdminController.java
│   │   │   ├── DeliveryController.java
│   │   │   └── ... other controllers
│   │   ├── dto/                            # Data Transfer Objects
│   │   │   ├── LoginDto.java
│   │   │   ├── AdminLoginDto.java
│   │   │   ├── CartDTO.java
│   │   │   ├── OrderDTO.java
│   │   │   └── ... other DTOs
│   │   ├── entity/                         # JPA Entities
│   │   │   ├── User.java
│   │   │   ├── Admin.java
│   │   │   ├── Product.java
│   │   │   ├── Order.java
│   │   │   ├── Cart.java
│   │   │   └── ... other entities
│   │   ├── repository/                     # Spring Data JPA Repositories
│   │   │   ├── UserRepository.java
│   │   │   ├── ProductRepository.java
│   │   │   ├── OrderRepository.java
│   │   │   └── ... other repositories
│   │   ├── service/                        # Business Logic Layer
│   │   │   ├── UserService.java
│   │   │   ├── ProductService.java
│   │   │   ├── OrderService.java
│   │   │   ├── CartService.java
│   │   │   └── ... other services
│   │   └── util/                           # Utility classes
│   │       └── JwtUtil.java
│   └── resources/
│       ├── application.properties           # Application configuration
│       ├── insert_admin.sql                 # Initial admin data
│       ├── fix_admin_table.sql              # Admin table fixes
│       └── static/templates/                # Static files
├── src/test/                               # Unit tests
├── pom.xml                                 # Maven configuration
├── mvnw / mvnw.cmd                        # Maven wrapper
└── uploads/                                # File uploads directory
    └── products/                           # Product images
```

## Key Features

### 🔐 Authentication & Authorization
- JWT token-based authentication
- Role-based access control (RBAC)
- Admin, User, Merchant, Supplier, and Delivery Person roles
- Secure password handling with Spring Security

### 🛒 E-Commerce Features
- Product catalog with images
- Shopping cart management
- Order processing and tracking
- Checkout with validation
- Order status management (Pending, Processing, Shipped, Delivered, Cancelled)

### 👥 Multi-Role System
- **Admin** - Platform administration, statistics, user management
- **User/Customer** - Browse products, create orders, manage cart
- **Merchant** - Manage products, view sales
- **Supplier** - Manage supplier orders
- **Delivery Person** - Manage deliveries

### 📊 Admin Dashboard
- User statistics
- Order analytics
- Sales reports
- Platform monitoring

### 💬 Communication
- Inquiry/Support system
- Admin replies to inquiries
- Notification system

### 📦 File Management
- Product image uploads
- Multipart file handling
- File validation (10MB max per file)

## Configuration

### Server Settings
```properties
server.port=8090                           # Server port
```

### CORS Configuration
```properties
spring.web.cors.allowed-origins=http://localhost:5173,http://localhost:5174,http://localhost:3000
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*
spring.web.cors.allow-credentials=true
```

**Allowed Frontends:**
- `http://localhost:5173` (Vite dev server)
- `http://localhost:5174` (Secondary Vite dev server)
- `http://localhost:3000` (React dev server)

### Logging
```properties
logging.level.com.merko.merko_backend=DEBUG
logging.level.org.springframework.web=INFO
logging.level.org.springframework.web.servlet=DEBUG
```

