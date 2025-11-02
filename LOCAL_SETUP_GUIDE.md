# 🚀 EDU-NGEN LOCAL SETUP GUIDE - 3 ALTERNATIVES

## 📋 TABLE OF CONTENTS
- [Alternative 1: Full Local Setup (PostgreSQL + Java + Node.js)](#alternative-1-full-local-setup)
- [Alternative 2: H2 Database (Java + Node.js Only)](#alternative-2-h2-database)
- [Alternative 3: GitHub Codespace (No Installation)](#alternative-3-github-codespace)

---

## 🔍 STEP 0: CHECK WHAT YOU HAVE

Open PowerShell and run:
```powershell
java -version
node --version
npm --version
psql --version
```

Tell me what you see - this will help determine which setup is best for you!

---

## 🎯 Alternative 1: Full Local Setup (PostgreSQL + Java + Node.js)

### Required Downloads:
1. **PostgreSQL**: https://www.postgresql.org/download/windows/ (Version 16)
2. **Java 17 JDK**: https://adoptium.net/temurin/releases/?version=17
3. **Node.js**: https://nodejs.org/en/download/

### Installation Steps:

#### 1. PostgreSQL Database (10 minutes)
- Download and run PostgreSQL 16 installer
- Set password: `admin123` (IMPORTANT!)
- Complete installation with default settings
- Open pgAdmin to verify installation

#### 2. Create Database
Open Command Prompt:
```cmd
psql -U postgres
# Enter password: admin123
CREATE DATABASE edu_ngen;
\q
```

#### 3. Run Application
```powershell
# Navigate to project
cd C:\Users\HP\Downloads\EDU-NGEN

# Run backend
./mvnw spring-boot:run

# In NEW PowerShell window for frontend:
cd C:\Users\HP\Downloads\EDU-NGEN\frontend
npm install
npm start
```

---

## ⚡ Alternative 2: H2 Database (Java + Node.js Only)

**BEST OPTION if your PC can't handle PostgreSQL!**

### Required Downloads:
1. **Java 17 JDK**: https://adoptium.net/temurin/releases/?version=17
2. **Node.js**: https://nodejs.org/en/download/

### Installation Steps:

#### 1. Install Java 17 JDK (5 minutes)
- Download and run the installer
- Accept all defaults
- Restart Command Prompt

#### 2. Install Node.js (5 minutes)
- Download and run the installer
- Accept all defaults

#### 3. Run with H2 Database
```powershell
# Navigate to project
cd C:\Users\HP\Downloads\EDU-NGEN

# Run backend with H2 profile
./mvnw spring-boot:run -Dspring.profiles.active=h2

# In NEW PowerShell window for frontend:
cd C:\Users\HP\Downloads\EDU-NGEN\frontend
npm install
npm start
```

**Benefits:**
✅ No PostgreSQL installation needed
✅ Database runs in memory (much faster)
✅ Less resource usage
✅ Perfect for testing and development

---

## 🌐 Alternative 3: GitHub Codespace (No Installation)

**BEST OPTION if you want to run everything online!**

### Steps:
1. Go to: https://github.com/Saumaydev/EDU-NGEN
2. Click green **"Code"** button
3. Click **"Codespaces"** tab
4. Click **"Create codespace on main"**
5. Wait 2-3 minutes for setup
6. In terminal, run:
```bash
docker-compose up --build
```

**Benefits:**
✅ No installation required
✅ Runs on GitHub's servers
✅ Free for personal use
✅ Works on any computer with internet

---

## 🚀 RUNNING THE APPLICATION

### For All Alternatives:

#### Backend Commands:
```powershell
# Alternative 1 (PostgreSQL):
./mvnw spring-boot:run

# Alternative 2 (H2 Database):
./mvnw spring-boot:run -Dspring.profiles.active=h2
```

#### Frontend Commands:
```powershell
cd frontend
npm install
npm start
```

### Access URLs:
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api
- **API Documentation**: http://localhost:8080/api/swagger-ui.html
- **H2 Console** (Alternative 2): http://localhost:8080/api/h2-console

---

## 🧪 TEST LOGIN

Use these credentials to test:
```
Email: teacher@example.com
Password: password123
```

---

## 📱 WHAT WORKS IN EACH ALTERNATIVE

| Feature | Alt 1 (PostgreSQL) | Alt 2 (H2) | Alt 3 (Codespace) |
|---------|-------------------|------------|-------------------|
| All Backend APIs | ✅ | ✅ | ✅ |
| Frontend Pages | ✅ | ✅ | ✅ |
| Database | PostgreSQL | H2 (Memory) | PostgreSQL |
| Email System | ✅ | ✅ | ✅ |
| Performance | Best | Good | Best |
| Setup Time | 30 mins | 15 mins | 5 mins |

---

## 🆘 TROUBLESHOOTING

### Common Issues:

#### "java is not recognized"
- Restart Command Prompt after installing Java
- Check: `java -version`

#### "node is not recognized"
- Restart Command Prompt after installing Node.js
- Check: `node --version`

#### "psql: command not found"
- PostgreSQL not installed or not in PATH
- Use Alternative 2 (H2) instead

#### "Port already in use"
```powershell
# Find and kill process using port 3000 or 8080
netstat -ano | findstr :3000
taskkill /PID <PID> /F
```

#### "Connection refused"
- Make sure backend is running before starting frontend
- Wait 30 seconds after starting backend

---

## 🎯 MY RECOMMENDATION

**Start with Alternative 2 (H2 Database)** because:
- ✅ Fastest setup (15 minutes)
- ✅ Lightest on resources
- ✅ All features work
- ✅ Perfect for development

Once it's working, you can try Alternative 1 if you need persistent data storage.

---

## 📞 NEED HELP?

**Tell me:**
1. What you see when you run `java -version`
2. What you see when you run `node --version`
3. Which alternative you want to try

**I'll guide you step-by-step through the setup!**

---

**Ready to start? Pick your alternative and let's get EDU-NGEN running!** 🚀