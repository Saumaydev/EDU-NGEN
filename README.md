# 🎓 EDU-NGEN - Complete Online Examination Platform

A comprehensive Java-based online examination management system designed for schools, colleges, and educational institutions.

## 🚀 Features

### Core Platform
- **Multi-Role System**: Student, Teacher, Admin access levels
- **Exam Management**: Create, schedule, and conduct online examinations
- **Real-time Results**: Instant score calculation and detailed analytics
- **Secure Environment**: Tab-switch detection, timer validation, proctoring features

### Advanced Features
- **CGPA Calculator**: Academic performance tracking and prediction
- **Analytics Dashboard**: Comprehensive performance insights
- **Email Notifications**: Automated reports and reminders
- **Leaderboard System**: Performance ranking and motivation
- **Parental Reports**: Detailed progress sharing

### Technical Features
- **Spring Boot 3.2** backend with 40+ REST APIs
- **React 18** frontend with Redux state management
- **MySQL/PostgreSQL/H2** database support
- **JWT Authentication** with refresh tokens
- **Docker** containerization ready
- **Email Integration** with SendGrid

---

## 📋 Quick Start

### Option 1: VS Code + MySQL (Recommended)
📖 **Complete Guide**: [VS_CODE_MYSQL_SETUP.md](VS_CODE_MYSQL_SETUP.md)

1. **Create MySQL database**:
   ```sql
   CREATE DATABASE edungen;
   ```

2. **Clone and open in VS Code**:
   ```bash
   git clone https://github.com/Saumaydev/EDU-NGEN.git
   cd EDU-NGEN
   ```

3. **Run backend** (VS Code Run button or terminal):
   ```bash
   ./mvnw spring-boot:run -Dspring.profiles.active=mysql
   ```

4. **Run frontend** (new terminal):
   ```bash
   cd frontend && npm start
   ```

5. **Access**: http://localhost:3000
   - **Login**: `teacher@example.com` / `password123`

### Option 2: Docker (All-in-One)
```bash
docker-compose up --build
```

### Option 3: H2 Database (No MySQL needed)
```bash
./mvnw spring-boot:run -Dspring.profiles.active=h2
```

---

## 🗂️ Project Structure

```
EDU-NGEN/
├── 📁 src/main/java/com/edugen/
│   ├── 📁 entity/          # 23 JPA entities
│   ├── 📁 repository/      # 23 data access layers
│   ├── 📁 service/         # 8 core services
│   ├── 📁 controller/      # 7 REST controllers
│   ├── 📁 dto/             # Data transfer objects
│   ├── 📁 security/        # JWT authentication
│   └── 📁 exception/       # Global error handling
├── 📁 frontend/
│   ├── 📁 src/
│   │   ├── 📁 pages/       # 10+ React pages
│   │   ├── 📁 components/  # Reusable UI components
│   │   └── 📁 slices/      # Redux state management
│   └── 📄 package.json
├── 📄 pom.xml
├── 📄 application.yml
├── 📄 application-mysql.yml
├── 📄 application-h2.yml
└── 📄 docker-compose.yml
```

---

## 🛠️ Technology Stack

### Backend
- **Spring Boot 3.2** - Java framework
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Database ORM
- **MySQL/PostgreSQL** - Primary databases
- **H2** - In-memory database for testing
- **JWT** - Token-based authentication
- **SendGrid** - Email service
- **Redis** - Caching (optional)

### Frontend
- **React 18** - UI framework
- **Redux Toolkit** - State management
- **Material-UI** - Component library
- **Axios** - HTTP client
- **React Router** - Navigation

### DevOps & Testing
- **Docker** - Containerization
- **Maven** - Build management
- **JUnit** - Unit testing
- **GitHub Actions** - CI/CD pipeline

---

## 📊 Database Schema

### Core Entities
- **User** (Student/Teacher/Admin profiles)
- **School** (Institution management)
- **Exam** (Exam configuration)
- **Question** (Question bank)
- **ExamAttempt** (Student attempts)
- **StudentResponse** (Answer tracking)
- **CGPAHistory** (Academic records)
- **LeaderboardCache** (Performance rankings)

---

## 🔐 Security Features

- **JWT Authentication** with refresh tokens
- **Role-based Access Control** (RBAC)
- **BCrypt Password Hashing**
- **CORS Configuration** for frontend
- **SQL Injection Protection** via JPA
- **Tab Switch Detection** during exams
- **Exam Timer Validation**
- **Input Validation & Sanitization**

---

## 📱 API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Token refresh
- `POST /api/auth/logout` - User logout

### Teacher Operations
- `GET /api/teacher/exams` - List exams
- `POST /api/teacher/exams` - Create exam
- `POST /api/teacher/questions` - Add questions
- `GET /api/teacher/analytics` - Performance analytics

### Student Operations
- `GET /api/student/exams` - Available exams
- `POST /api/student/attempt` - Start exam
- `POST /api/student/submit` - Submit answers
- `GET /api/student/results` - View results

### Admin Operations
- `GET /api/admin/dashboard` - Admin dashboard
- `GET /api/admin/users` - User management
- `GET /api/admin/reports` - System reports

---

## 🚀 Deployment

### Environment Variables
```yaml
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/edungen
spring.datasource.username=your_username
spring.datasource.password=your_password

# JWT
jwt.secret=your-super-secret-key-32-characters-min
jwt.expiration=3600000

# Email
spring.mail.host=smtp.sendgrid.net
spring.mail.username=apikey
spring.mail.password=your-sendgrid-api-key
```

---

## 📚 Documentation

| Document | Purpose |
|----------|---------|
| [VS_CODE_MYSQL_SETUP.md](VS_CODE_MYSQL_SETUP.md) | VS Code + MySQL development setup |
| [LOCAL_SETUP_GUIDE.md](LOCAL_SETUP_GUIDE.md) | Multiple setup alternatives |
| [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md) | Production deployment instructions |
| [IMPLEMENTATION.md](IMPLEMENTATION.md) | Detailed implementation progress |
| [NEXT_STEPS.md](NEXT_STEPS.md) | Development roadmap |

---

## 🧪 Testing

### Backend Tests
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=AuthServiceTest
```

### Frontend Tests
```bash
cd frontend
npm test
```

---

## 🤝 Contributing

1. **Fork** the repository
2. **Create** feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** changes (`git commit -m 'Add amazing feature'`)
4. **Push** to branch (`git push origin feature/amazing-feature`)
5. **Open** Pull Request

---

## 📄 License

This project is licensed under the MIT License.

---

## 🆘 Support

### Common Issues
- **Database connection**: Check MySQL service is running
- **Port conflicts**: Ensure ports 3000, 8080, 3306 are available
- **Java version**: Requires JDK 17 or higher
- **Node.js version**: Requires Node.js 16 or higher

### Get Help
- 📖 Check the [Setup Guide](VS_CODE_MYSQL_SETUP.md)
- 🐛 Report issues on GitHub
- 💬 Join discussions for feature requests

---

## 🎉 Acknowledgments

- **Spring Boot Team** - Excellent framework
- **React Team** - Amazing UI library
- **MySQL Community** - Reliable database
- **Open Source Contributors** - Tools and libraries

---

## 📞 Contact

- **Repository**: https://github.com/Saumaydev/EDU-NGEN
- **Issues**: https://github.com/Saumaydev/EDU-NGEN/issues

---

**🎓 EDU-NGEN - Empowering Education Through Technology** 🚀