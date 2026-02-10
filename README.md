# 🛡️ SensorGuard

**Cloud-Native Real-Time Sensor Monitoring System with Anomaly Detection**

SensorGuard este o aplicație cloud-native pentru monitorizarea în timp real a senzorilor virtuali (temperatură, vibrații, presiune, umiditate) cu detectare automată de anomalii folosind Machine Learning.

---

## 📋 Cuprins

- [Arhitectura Sistemului](#-arhitectura-sistemului)
- [Tehnologii Folosite](#-tehnologii-folosite)
- [Caracteristici Principale](#-caracteristici-principale)
- [Prerequisite](#-prerequisite)
- [Instalare și Rulare](#-instalare-și-rulare)
- [Utilizare](#-utilizare)
- [API Documentation](#-api-documentation)
- [Arhitectura Detaliată](#-arhitectura-detaliată)
- [Secret Management](#-secret-management)
- [Observabilitate](#-observabilitate)
- [CI/CD](#-cicd)
- [Dezvoltare](#-dezvoltare)
- [Contribuție](#-contribuție)

---

## 🏗️ Arhitectura Sistemului
```
┌─────────────────┐
│   Frontend      │  ← React/HTML + WebSocket
│   (Port 8080)   │
└────────┬────────┘
         │
┌────────▼────────────────────────────────────────┐
│         Spring Boot Gateway                     │
│         - REST API (Swagger)                    │
│         - WebSocket Server                      │
│         - Health Checks                         │
│         (Port 8080)                            │
└──────┬──────────────────┬──────────────────────┘
       │                  │
       │                  │
┌──────▼──────┐    ┌──────▼────────────────┐
│ PostgreSQL  │    │  Python Microservice  │
│   Database  │    │  Anomaly Detector     │
│ (Port 5432) │    │  (Z-score detection)  │
└─────────────┘    │  (Port 5000)          │
                   └───────────────────────┘
```

---

## 🛠️ Tehnologii Folosite

### Backend
- **Java 17** - Limbaj de programare
- **Spring Boot 3.2.2** - Framework pentru Gateway
- **Spring Data JPA** - Persistență date
- **Spring WebSocket** - Comunicare real-time
- **PostgreSQL 15** - Bază de date relațională
- **Maven** - Build tool

### Microserviciu ML
- **Python 3.11** - Limbaj de programare
- **Flask 3.0.0** - Framework web
- **NumPy 1.26.2** - Calcule numerice
- **Z-score Algorithm** - Detectare anomalii statistică

### DevOps & Infrastructure
- **Docker** - Containerizare
- **Docker Compose** - Orchestrare multi-container
- **Docker Secrets** - Management secrete
- **GitHub Actions** - CI/CD Pipeline

### Documentation & API
- **SpringDoc OpenAPI 3** - Swagger UI
- **Actuator** - Health checks & Metrics

---

## ✨ Caracteristici Principale

### 🔹 Funcționalități Core

1. **Ingestie Date în Timp Real**
   - REST API endpoint pentru primirea valorilor de la senzori
   - Validare și persistență automată în PostgreSQL

2. **Detectare Automată de Anomalii**
   - Algoritm Z-score implementat în Python
   - Analiză statistică cu fereastră rolling
   - Calculare anomaly score (0.0 - 1.0)

3. **Sistem de Alertare**
   - Generare automată de alerte la detectarea anomaliilor
   - 4 niveluri de severitate: LOW, MEDIUM, HIGH, CRITICAL
   - Notificări real-time prin WebSocket

4. **Comunicare Real-Time (WebSocket)**
   - Push instant al valorilor noi către client
   - Notificări live pentru alerte
   - Actualizare automată a interfeței

5. **Monitorizare Multi-Senzor**
   - Suport pentru 4 tipuri: TEMPERATURE, VIBRATION, PRESSURE, HUMIDITY
   - Seed data cu 4 senzori preconfigurati
   - Extensibil pentru noi tipuri

### 🔹 Observabilitate

- **Health Checks**: Endpoints `/actuator/health` și `/api/health`
- **Metrics**: Expuse prin Spring Actuator
- **Structured Logging**: Loguri formatate cu timestamp
- **Docker Health Checks**: Pentru fiecare serviciu

### 🔹 Securitate

- **Docker Secrets**: Parolele sunt gestionate prin Docker Secrets
- **Environment Variables**: Configurare externalizată
- **No Hardcoded Credentials**: Toate secretele sunt externalizate

---

## 📦 Prerequisite

- **Docker** (versiunea 20.10+)
- **Docker Compose** (versiunea 2.0+)
- **Git**

*Opțional pentru dezvoltare locală:*
- Java 17 JDK
- Maven 3.8+
- Python 3.11+
- PostgreSQL 15

---

## 🚀 Instalare și Rulare

### Metoda 1: Docker Compose (Recomandat)
```bash
# 1. Clonează repository-ul
git clone <repository-url>
cd SensorGuard

# 2. Creează fișierele de secrets
mkdir -p secrets
echo "sensorguard" > secrets/postgres_db.txt
echo "sensorguard_user" > secrets/postgres_user.txt
echo "sensorguard_secure_pass_2026" > secrets/postgres_password.txt

# 3. Pornește aplicația
docker-compose up --build

# Aplicația va fi disponibilă la:
# - Frontend: http://localhost:8080
# - Swagger UI: http://localhost:8080/swagger-ui.html
# - API: http://localhost:8080/api
# - Python Service: http://localhost:5000
```

### Metoda 2: Dezvoltare Locală
```bash
# 1. Pornește PostgreSQL
docker run -d -p 5432:5432 \
  -e POSTGRES_DB=sensorguard \
  -e POSTGRES_USER=sensorguard_user \
  -e POSTGRES_PASSWORD=sensorguard_pass \
  postgres:15-alpine

# 2. Pornește Python Microservice
cd anomaly-detector/app
pip install -r requirements.txt
python main.py

# 3. Pornește Spring Boot Gateway
cd ../../
mvn clean package
java -jar target/gateway-1.0.0.jar
```

---

## 💻 Utilizare

### 1. Accesează Frontend-ul
```
http://localhost:8080
```

### 2. Vizualizează Senzorii Disponibili

Frontend-ul va încărca automat senzorii activi din sistem.

### 3. Injectează Valori

- Selectează un senzor din dropdown
- Introdu o valoare numerică
- Click pe "Submit Reading"
- Vezi rezultatul în secțiunea "Recent Readings"

### 4. Testează Detectarea Anomaliilor

**Trimite valori normale:**
```bash
curl -X POST http://localhost:8080/api/readings/ingest \
  -H "Content-Type: application/json" \
  -d '{"sensorId":"TEMP-001","value":22.0}'
```

**Trimite o anomalie:**
```bash
curl -X POST http://localhost:8080/api/readings/ingest \
  -H "Content-Type: application/json" \
  -d '{"sensorId":"TEMP-001","value":100.0}'
```

---

## 📚 API Documentation

### Swagger UI
```
http://localhost:8080/swagger-ui.html
```

### Endpoint-uri Principale

#### Sensors
- `GET /api/sensors` - Lista tuturor senzorilor
- `GET /api/sensors/{sensorId}` - Detalii senzor
- `POST /api/sensors` - Creează senzor nou

#### Sensor Readings
- `POST /api/readings/ingest` - Ingestie valoare nouă
- `GET /api/readings` - Toate citirile
- `GET /api/readings/sensor/{sensorId}` - Citiri pentru un senzor
- `GET /api/readings/anomalies` - Doar anomalii

#### Alerts
- `GET /api/alerts` - Toate alertele
- `GET /api/alerts/unacknowledged` - Alerte neconfirmate
- `PUT /api/alerts/{id}/acknowledge` - Confirmă alertă

#### Health
- `GET /api/health` - Health check custom
- `GET /actuator/health` - Spring Actuator health

---

## 🔧 Arhitectura Detaliată

### Gateway (Spring Boot)

**Layers:**
```
Controller → Service → Repository → Database
                ↓
        WebSocket Handler
                ↓
        Python Microservice (HTTP)
```

**Entități Principale:**
- `Sensor` - Definiție senzor
- `SensorReading` - Valoare citită + anomaly flag
- `Alert` - Alertă generată

### Anomaly Detector (Python)

**Algoritm Z-Score:**
```python
z_score = abs((value - mean) / std)
is_anomaly = z_score > threshold  # threshold = 2.5
```

**Caracteristici:**
- Rolling window: 50 valori
- Minim 3 valori pentru analiză
- Normalizare score: 0.0 - 1.0

---

## 🔐 Secret Management

**Fișiere de secrets:**
```
secrets/
├── postgres_db.txt
├── postgres_user.txt
└── postgres_password.txt
```

**Utilizare în Docker Compose:**
```yaml
secrets:
  postgres_password:
    file: ./secrets/postgres_password.txt
```

**Important:** Folderul `secrets/` este în `.gitignore`!

---

## 📊 Observabilitate

### Health Checks

**Gateway:**
```bash
curl http://localhost:8080/api/health
```

**Python Service:**
```bash
curl http://localhost:5000/health
```

**Actuator:**
```bash
curl http://localhost:8080/actuator/health
```

### Metrics
```bash
curl http://localhost:8080/actuator/metrics
```

### Logs
```bash
# Vezi toate logurile
docker-compose logs

# Follow logs pentru un serviciu
docker-compose logs -f gateway

# Loguri Python
docker-compose logs -f anomaly-detector
```

---

## 🔄 CI/CD

Pipeline GitHub Actions include:

1. **Build & Test**
   - Maven build pentru Gateway
   - Python dependencies install
   - Unit tests

2. **Docker Build**
   - Build imagini Docker
   - Tag cu versiune

3. **Security Scan**
   - Trivy vulnerability scanner

4. **Integration Tests**
   - Pornire stack complet
   - Health checks
   - API tests

**Pipeline file:** `.github/workflows/ci.yml`

---

## 👨‍💻 Dezvoltare

### Structura Proiectului
```
SensorGuard/
├── gateway/                      # Spring Boot Gateway
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/sensorguard/gateway/
│   │   │   │   ├── model/       # Entități
│   │   │   │   ├── repository/  # Repositories
│   │   │   │   ├── service/     # Business logic
│   │   │   │   ├── controller/  # REST Controllers
│   │   │   │   ├── dto/         # Data Transfer Objects
│   │   │   │   ├── config/      # Configurări
│   │   │   │   ├── websocket/   # WebSocket
│   │   │   │   └── bootstrap/   # Data seeding
│   │   │   └── resources/
│   │   │       ├── static/      # Frontend
│   │   │       └── application.properties
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
├── anomaly-detector/             # Python Microservice
│   ├── app/
│   │   ├── main.py              # Flask app
│   │   ├── detector.py          # Anomaly detection logic
│   │   └── requirements.txt
│   └── Dockerfile
├── postgres/
│   └── init.sql                 # Database init
├── secrets/                     # Docker secrets
│   ├── postgres_db.txt
│   ├── postgres_user.txt
│   └── postgres_password.txt
├── .github/
│   └── workflows/
│       └── ci.yml               # CI/CD pipeline
├── docker-compose.yml
├── .gitignore
└── README.md
```

### Adăugare Senzor Nou
```sql
INSERT INTO sensors (sensor_id, name, type, location, unit, active, created_at)
VALUES ('NEW-001', 'New Sensor', 'TEMPERATURE', 'Location', '°C', true, NOW());
```

### Testare Locală
```bash
# Test Gateway
mvn test

# Test Python service
cd anomaly-detector/app
python -m pytest
```

---

## 📝 Licență

MIT License

---

## 👥 Autor

Proiect realizat pentru cursul de Cloud Computing

---

## 📞 Contact & Support

Pentru întrebări sau probleme, deschide un issue pe GitHub.

---

**Built with ❤️ using Spring Boot, Python, Docker & PostgreSQL**
