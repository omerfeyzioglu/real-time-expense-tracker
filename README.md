# Real-Time Expense Tracker

A robust real-time expense tracking system built with Apache Kafka, Apache Spark, and Apache Cassandra, featuring a Spring Boot web interface. This project implements a complete data pipeline for tracking and analyzing credit card expenses in real-time.

## 🚀 System Architecture

The system consists of several interconnected components:

### Data Flow
     ![img](https://github.com/user-attachments/assets/c389b4c6-815a-4f88-81c1-87f32aff57d8)
### Components
- **Data Generator**: Generates synthetic credit card transaction data every second
- **Apache Kafka**: Message queue system for data streaming with dedicated topics (v3.9.0)
- **Apache Spark**: Real-time data processing and analytics (v3.5.3)
- **Apache Cassandra**: NoSQL database for storing transaction data (v4.1.7)
- **Spring Boot**: Web interface for viewing employee data and expenses (v3.2.0)
- **PostgreSQL**: Stores employee and department information
- **HDFS**: Stores employee images (AWS S3 or Google Drive can be used alternatively)

## 💡 Features

- Real-time expense tracking and processing (1-second intervals)
- Automated data generation for all employees
- Per-user expense tracking and storage
- Instant cumulative expense reporting
- Employee information display with images
- Department-wise expense tracking
- Comprehensive logging with SLF4J
- Employee management (CRUD operations)
- Image storage and retrieval via HDFS
- Manager hierarchy tracking

## 🛠 Technical Requirements

- Java JDK 11
- Apache Kafka 3.9.0
- Apache Spark 3.5.3
- Apache Cassandra 4.1.7
- Spring Boot 3.2.0
- PostgreSQL
- Maven
- Lombok

## 📦 Installation & Setup

1. **Clone the Repository**
   ```bash
   git clone https://github.com/omerfeyzioglu/real-time-expense-tracker.git
   cd real-time-expense-tracker
   ```

2. **Database Setup**
   ```sql
   -- Cassandra Setup
   CREATE KEYSPACE IF NOT EXISTS your_keyspace 
   WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

   CREATE TABLE expenses (
     empno text,
     date_time timestamp,
     description text,
     type text,
     count int,
     payment double,
     PRIMARY KEY (empno, date_time)
   );
   ```

3. **Start Services**
   ```bash
   # Start Zookeeper
   bin/zookeeper-server-start.sh config/zookeeper.properties

   # Start Kafka
   bin/kafka-server-start.sh config/server.properties

   # Start Cassandra
   cassandra -f
   ```

4. **Build and Run**
   ```bash
   mvn clean install
   java -jar target/project3-0.0.1-SNAPSHOT.jar
   ```

## 🔧 Configuration

### Application Properties
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/your_database
    username: your_username
    password: your_password
  
  cassandra:
    keyspace-name: your_keyspace
    contact-points: localhost
    local-datacenter: datacenter1
    port: 9042

  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

kafka:
  bootstrap-servers: localhost:9092
  topic: expenses-topic
  group-id: expense-group
```

## 🌐 API Endpoints

### Employee Management
- `GET /employee`: List all employees with their expenses
  - Optional query param: `empnos` (List<Integer>) for specific employees
- `POST /employee/add`: Add new employee
  - Requires employee details and optional image file
- `POST /employee/update`: Update existing employee
  - Requires employee details and optional new image
- `POST /employee/delete`: Delete employee and related data
  - Requires `empno`
- `GET /employee/image`: Retrieve employee image
  - Requires `imageName`

## 📊 Data Models

### Employee
```java
{
    Integer empno;      // Employee number (auto-generated)
    String ename;       // Employee name
    String job;         // Job title
    Integer mgr;        // Manager's employee number
    Double sal;         // Salary
    Double comm;        // Commission
    Integer deptno;     // Department number
    String img;         // Image filename
}
```

### Expense
```java
{
    String empno;       // Employee number
    String dateTime;    // Transaction timestamp
    String description; // Expense description
    String type;        // Expense type
    Integer count;      // Quantity
    Double payment;     // Amount paid
}
```

### ExpenseSummary
```java
{
    Double totalAmount;     // Total expenses
    Integer transactionCount; // Number of transactions
    List<Expense> expenses; // Detailed expense records
}
```

## Error Handling

The application includes comprehensive error handling:
- Image upload/download validation
- Department validation
- Employee existence checks
- Cascade deletion (employee, expenses, images)
- Transaction logging
- Proper error responses with meaningful messages

## 👥 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

## 🙏 Acknowledgments

- Based on SWE 307 Big Data Project requirements
- Original project structure from [SWE307-2024](https://github.com/ozmen54/SWE307-2024)
