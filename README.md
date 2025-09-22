# 🎬 Movie Ticket Booking System

A backend application for managing theaters, screens, seat layouts, shows, and bookings — built with Spring Boot, Spring Security (JWT), and JPA/Hibernate.
This system supports both registered users and guests (email-only bookings), with proper concurrency handling to prevent double booking of seats.

## ✨ Features

- Authentication & Authorization

  - JWT-based authentication

  - Role-based access: ADMIN, MANAGER, USER, GUEST

  - Only ADMIN and MANAGER routes are protected; rest APIs are public

* Theater & Screen Management

  - Admins can register theaters

  - Managers can manage screens under their theaters

* Seat Layouts

  - Define seat layouts (STANDARD, IMAX, 4DX)

  - Rows and seats with categories (Regular, Premium, VIP)

  - Dynamic pricing per seat

* Show Management

  - Register shows for a screen & movie

  - Extra charges (e.g., 3D surcharge) supported

  - Auto-generate ShowSeats from the screen’s layout

* Booking Flow

  - Users or guests can book seats

  - Seats move from AVAILABLE → RESERVED → BOOKED

  - Reservation timeout (15 mins) implemented

  - Optimistic locking (@Version) used to handle concurrency

* Ticket Generation

  - Ticket linked to booking

  - QR code placeholder included

  - Email support (can be extended)

## 🛠️ Tech Stack

* Java 21

* Spring Boot 3.x

* Spring Security + JWT

* Spring Data JPA (Hibernate)

* PostgreSQL / MySQL (configurable)

* Lombok

* MapStruct for DTO mapping

* Swagger/OpenAPI for API documentation

## 📂 Project Structure
```bash
src/main/java/com/krish/ticket_booking
│
├── config/             # Security & app config
├── controller/         # REST controllers
├── dto/                # Request & Response DTOs
├── entity/             # JPA entities (User, Theater, Screen, Seat, Show, Booking, Ticket, etc.)
├── exception/          # Custom exceptions
├── mapper/             # MapStruct mappers
├── repository/         # Spring Data JPA repositories
├── service/            # Business logic interfaces
└── service/impl/       # Service implementations
```

## 📌 API Endpoints
Auth

```bash POST /api/auth/signup``` → Register user

```bash POST /api/auth/login ``` → Get JWT token

```bash Theater / Screen ``` (Manager/Admin only)

```bash POST /api/theaters ``` → Create theater

```bash POST /api/theaters/{id}/screens ``` → Add screen

Seat Layout

```bash POST /api/screens/{id}/layouts ``` → Create seat layout

```bash GET /api/screens/{id}/layouts ``` → List layouts

Shows

```bash POST /api/screens/{id}/shows ``` → Create show

```bash GET /api/theaters/{id}/shows ``` → List shows by theater

Booking

```bash POST /api/shows/{id}/bookings ``` → Book seats (user/guest)

```bash GET /api/bookings/{id} ``` → View booking details

## ⚡ Concurrency Handling

* Optimistic Locking with @Version on ShowSeat

* Reservation Timeout → Reserved seats auto-release after 15 mins if payment is not completed

* Prevents double booking in high-traffic scenarios
