# FixMed - Medical Platform Backend

A comprehensive Spring Boot-based backend application for a medical platform that facilitates communication and appointment management between doctors, patients, and medical facilities.

## 📋 Table of Contents

- [Overview](#overview)
- [Quick Start](#quick-start)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Project Structure](#project-structure)
- [API Documentation](#api-documentation)
- [Database](#database)
- [Authentication](#authentication)
- [Docker](#docker)
- [Development](#development)
- [Security](#security)
- [Contributing](#contributing)

---

## 🚀 Quick Start

**For Development Setup Instructions:**

👉 **[SETUP.md](./SETUP.md)** - Complete setup guide for developers

**For Security Policies and Best Practices:**

🔒 **[SECURITY.md](./SECURITY.md)** - Security policies and guidelines

**Preparing for Production/GitHub:**

📋 **[GITHUB_PUBLICATION_CHECKLIST.md](./GITHUB_PUBLICATION_CHECKLIST.md)** - Pre-publication checklist

---

## 🎯 Overview

FixMed is a backend REST API for a comprehensive medical platform that connects patients with qualified doctors and medical facilities. The platform enables:

- **User Management**: Registration and authentication for patients, doctors, and facilities
- **Appointment Scheduling**: Book, manage, and track medical appointments
- **Real-time Messaging**: Secure communication between doctors and patients
- **Medical Records**: Store and manage appointment history with medical updates
- **Facility Management**: Manage medical facilities and their services
- **File Management**: Store medical attachments and doctor profiles using MinIO
- **Review System**: Rate and review doctors and facilities

---

## ✨ Features

### Authentication & Authorization
- User registration and login with role-based access control (RBAC)
- JWT token-based authentication with token revocation
- Support for different user roles: PATIENT, DOCTOR, ADMIN, FACILITY
- BCrypt password encryption for enhanced security

### User Management
- Patient profiles with medical history
- Doctor profiles with specialization and license verification
- Facility/Hospital management
- User profile management and updates

### Appointments
- Create, retrieve, and manage medical appointments
- Appointment history with medical updates
- Track appointment status (pending, completed, cancelled)
- Attach medical files to appointments
- Search and filter appointments

### Messaging System
- Direct messaging between doctors and patients
- Conversation history management
- View patients associated with doctors
- View doctors associated with patients

### Medical Services
- Manage facility medical services
- Service details including pricing and duration
- Service categorization

### File Management
- Upload and manage doctor profile photos
- Store medical attachments
- File validation (size and type)
- Integration with MinIO object storage

### Reviews & Ratings
- Rate doctors and facilities
- Review history tracking
- Average rating calculation

---

## 🛠 Technology Stack

### Core Framework
- **Spring Boot 3.4.4** - Modern Java application framework
- **Java 17** - Latest LTS version of Java
- **Maven 3.9** - Dependency management

### Data & Database
- **Spring Data JPA** - ORM for database operations
- **Spring Data JDBC** - Low-level database access
- **MySQL 8.0+** - Relational database
- **Liquibase** - Database schema versioning and migration

### Security
- **Spring Security** - Authentication and authorization framework
- **JWT (JJWT 0.12.6)** - Token-based authentication
- **BCrypt** - Password hashing

### External Services
- **MinIO 8.5.17** - S3-compatible object storage for files
- **RabbitMQ (AMQP)** - Message broker for asynchronous tasks

### Development Tools
- **Lombok 1.18.38** - Reduce boilerplate code
- **Spring Boot DevTools** - Enhanced development experience
- **JUnit 5** - Testing framework
- **Spring Security Test** - Security testing utilities

### Validation
- **Spring Validation** - Bean validation support
- **Jakarta Bean Validation** - Declarative validation

---
