# Expense Tracker – Backend + Android

This is a small expense tracking system built for an internship assessment.

## Tech Stack

- **Backend**: Java, Spring Boot, Spring Data MongoDB, Maven  
- **Database**: MongoDB (NoSQL)  
- **Android Frontend**: Java, Retrofit2, Material Design, RecyclerView

---

## Backend (Spring Boot)

Location: `/` (root project)

### Main Features

- REST API for expenses:
  - `POST /expenses` – Create expense
  - `PUT /expenses/{id}` – Update expense
  - `DELETE /expenses/{id}` – Delete expense
  - `GET /expenses` – List / filter expenses
    - Supports `userId`, `category`, `startDate`, `endDate` as filters
- MongoDB schema:
  - `id`, `userId`, `amount`, `description`, `date`, `category`
- Basic validation and error handling.

### How to run backend

```bash
mvn spring-boot:run
