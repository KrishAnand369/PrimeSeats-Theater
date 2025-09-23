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

``` POST /api/auth/signup``` → Register user

``` POST /api/auth/login ``` → Get JWT token

``` Theater / Screen ``` (Manager/Admin only)

``` POST /api/theaters ``` → Create theater

``` POST /api/theaters/{id}/screens ``` → Add screen

Seat Layout

``` POST /api/screens/{id}/layouts ``` → Create seat layout

``` GET /api/screens/{id}/layouts ``` → List layouts

Shows

``` POST /api/screens/{id}/shows ``` → Create show

``` GET /api/theaters/{id}/shows ``` → List shows by theater

Booking

``` POST /api/shows/{id}/bookings ``` → Book seats (user/guest)

``` GET /api/bookings/{id} ``` → View booking details

## ⚡ Concurrency Handling

* Optimistic Locking with @Version on ShowSeat

* Reservation Timeout → Reserved seats auto-release after 15 mins if payment is not completed

* Prevents double booking in high-traffic scenarios

## 📃 Prerequisites

### Software to be installed

#### JDK 21

  - Link-https://www.oracle.com/in/java/technologies/downloads/#java21

  - After downloading , go to C drive open program files and then open folder named Java.

  - In java folder select jdk21 and go to bin folder and copy the path.

Path should be like this- C:\Program Files\Java\jdk-21\bin.

Open environment variable and in user variable select path and paste the path for bin.

#### Maven

  - Link-https://maven.apache.org/download.cgi

Download path should be the App folder created above in Project folder.
Open downloaded maven folder and copy bin path .
Set environment variable.

#### PostGresql & pgAdmin4
  - Download PostGresql  https://www.postgresql.org/download/

  - pgAdmin4 https://www.pgadmin.org/download/

### Database Setup (PostgreSQL)

1. Open pgAdmin and connect to your PostgreSQL server.

2. Create a new database:


  * Right-click on Databases → Create → Database.

  * Enter the name: ticket_booking.

  * Set the owner (e.g., postgres).

  * Click Save.
## ▶ Getting Started

### Command to Run
Open command prompt in the folder to which the project has cloned to

Run the below given commands in command prompt

1. ``` mvn clean install ```

2. ``` java -jar target/ticket-booking-0.0.1-SNAPSHOT.jar ```

### Access Swagger API docs:
Visit http://localhost:8080/swagger-ui/index.html
