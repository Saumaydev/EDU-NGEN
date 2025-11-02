# 🚀 EDU-NGEN VS CODE + MYSQL SETUP GUIDE

## 🎯 PERFECT DEVELOPMENT SETUP

You have the ideal combination! VS Code + MySQL is the best way to develop EDU-NGEN locally.

---

## 📋 QUICK OVERVIEW

| Tool | Purpose | Why It's Great |
|------|---------|----------------|
| **VS Code** | Code editor | Free, powerful, great Java support |
| **MySQL** | Database | Robust, widely used, excellent tools |
| **Maven** | Build tool | Java dependency management |
| **Node.js** | Frontend build | React development server |

---

## 🗄️ STEP 1: CREATE MYSQL DATABASE

### **Option A: MySQL Workbench (Recommended)**
1. **Open MySQL Workbench**
2. **Click your local connection**
3. **In query editor, run:**
```sql
CREATE DATABASE edungen;
USE edungen;
SHOW DATABASES;
```
4. **Verify `edungen` appears in the list**

### **Option B: MySQL Command Line**
1. **Open Command Prompt**
2. **Run:**
```cmd
mysql -u root -p
```
3. **Enter your MySQL password**
4. **Create database:**
```sql
CREATE DATABASE edungen;
EXIT;
```

---

## 💻 STEP 2: SET UP VS CODE

### **Install VS Code Extensions**
Open VS Code → Extensions (Ctrl+Shift+X) → Install:

1. **Extension Pack for Java** (Microsoft)
2. **Spring Boot Extension Pack** (Microsoft)
3. **MySQL** (formulahendry)
4. **ES7+ React/Redux/React-Native snippets** (dsznajder)
5. **Live Server** (Ritwick Dey)

### **Clone and Open Project**
```bash
# In VS Code terminal (Ctrl + `)
cd C:\Users\HP\Downloads
git clone https://github.com/Saumaydev/EDU-NGEN.git
cd EDU-NGEN
```

**File → Open Folder → Select `C:\Users\HP\Downloads\EDU-NGEN`**

---

## 📦 STEP 3: PROJECT SETUP

### **VS Code Will Auto-Detect**
- **Java project** → Install suggested extensions
- **Maven project** → Download dependencies
- **Spring Boot** → Configure run/debug

### **Check Java Installation**
In VS Code terminal:
```bash
java -version
javac -version
```

**If Java not found:**
1. Download: https://adoptium.net/temurin/releases/?version=17
2. Install JDK 17
3. Restart VS Code

### **Check Node.js Installation**
```bash
node --version
npm --version
```

**If Node.js not found:**
1. Download: https://nodejs.org/en/download/
2. Install Node.js
3. Restart VS Code

---

## 🚀 STEP 4: RUN BACKEND

### **Method 1: VS Code Run Button**
1. **Open:** `src/main/java/com/edugen/EduNgenApplication.java`
2. **Click the green "Run" button above main method
3. **Select "Run" or "Debug"**

### **Method 2: Terminal Command**
In VS Code terminal:
```bash
# Using Maven wrapper
./mvnw spring-boot:run -Dspring.profiles.active=mysql

# Or using system Maven (if installed)
mvn spring-boot:run -Dspring.profiles.active=mysql
```

### **Method 3: VS Code Launch Configuration**
1. **Press F5** or go to "Run → Start Debugging"
2. **Select "Java"** if prompted
3. **Choose main class:** `com.edugen.EduNgenApplication`

**Wait 2-3 minutes** - you'll see logs like:
```
Started EduNgenApplication in 15.234 seconds
Tomcat started on port(s): 8080 (http)
```

---

## ⚡ STEP 5: RUN FRONTEND

### **Open New Terminal**
In VS Code: **Terminal → New Terminal** (Ctrl+Shift+`)

```bash
cd frontend
npm install
npm start
```

**Wait 1-2 minutes** - you'll see:
```
Successfully compiled!
Compiled successfully!
You can now view edu-ngen-frontend in the browser.

  Local:            http://localhost:3000
```

---

## 🧪 STEP 6: TEST YOUR APPLICATION

### **Access URLs**
- **Frontend:** http://localhost:3000
- **Backend API:** http://localhost:8080/api
- **API Documentation:** http://localhost:8080/api/swagger-ui.html

### **Test Login**
```
Email: teacher@example.com
Password: password123
```

### **Verify Database Connection**
In MySQL Workbench:
```sql
USE edungen;
SHOW TABLES;
```
You should see tables like `users`, `exams`, `questions`, etc.

---

## 🎯 DEBUGGING IN VS CODE

### **Set Breakpoints**
1. **Open any Java file** (e.g., `AuthService.java`)
2. **Click left of line numbers** to set breakpoints
3. **Press F5** to start debugging
4. **Application will pause at breakpoints**

### **Debug Frontend**
1. **Open React component** in `frontend/src/`
2. **Click left of line numbers** to set breakpoints
3. **Open Developer Tools** in browser (F12)
4. **Go to Sources tab** → Your breakpoints will appear

---

## 🛠️ DEVELOPMENT WORKFLOW

### **Daily Development**
1. **Start MySQL** (if not running)
2. **Open VS Code** → EDU-NGEN project
3. **Start backend** (F5 or Run button)
4. **Start frontend** (new terminal → `npm start`)
5. **Make changes** → VS Code auto-reloads
6. **Test in browser** at http://localhost:3000

### **Database Management**
- **View data:** MySQL Workbench
- **Run queries:** VS Code MySQL extension
- **Backup data:** MySQL Workbench → Server → Data Export

### **Git Integration**
- **View changes:** VS Code Source Control (Ctrl+Shift+G)
- **Commit changes:** Type message → Click ✓
- **Push to GitHub:** Click sync icon

---

## 📱 USEFUL VS CODE SHORTCUTS

| Shortcut | Action |
|----------|--------|
| `Ctrl+Shift+P` | Command Palette |
| `Ctrl+`` | Toggle Terminal |
| `Ctrl+Shift+G` | Source Control |
| `F5` | Start Debugging |
| `Ctrl+Shift+X` | Extensions |
| `Ctrl+P` | Quick Open File |
| `Ctrl+Shift+O` | Go to Symbol |
| `F12` | Go to Definition |
| `Shift+F12` | Find All References |

---

## 🆘 TROUBLESHOOTING

### **"Could not connect to MySQL"**
- Check MySQL is running: Services → MySQL → Start
- Verify database exists: `CREATE DATABASE edungen;`
- Check credentials in `application-mysql.yml`

### **"Java not found"**
- Install JDK 17: https://adoptium.net/temurin/releases/?version=17
- Restart VS Code
- Check: `java -version`

### **"Node.js not found"**
- Install Node.js: https://nodejs.org/en/download/
- Restart VS Code
- Check: `node --version`

### **"Port 3000 already in use"**
```bash
# Find process using port
netstat -ano | findstr :3000

# Kill the process
taskkill /PID <PID> /F
```

### **"Maven build failed"**
```bash
# Clean and rebuild
./mvnw clean install

# Or delete .m2 folder and retry
```

---

## 🎉 BENEFITS OF THIS SETUP

✅ **Professional development environment**
✅ **Fast local development** (no Docker delays)
✅ **Full debugging capabilities**
✅ **Database control** (MySQL Workbench)
✅ **Git integration** built into VS Code
✅ **Free and open source tools**
✅ **Industry standard setup**

---

## 🚀 YOU'RE READY!

Your EDU-NGEN development environment is now perfectly configured with VS Code + MySQL!

**Next steps:**
1. **Create the MySQL database**
2. **Run the backend** (F5 or Run button)
3. **Run the frontend** (npm start)
4. **Start developing!**

**Happy coding!** 🎉