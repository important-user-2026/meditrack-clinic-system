# MediTrack — Clinic Appointment & Patient Records System

A Spring Boot web application for a network of outpatient clinics. It manages
patient registration, appointment booking, and appointment lifecycle
(request → confirm → cancel → complete). Built as the software artefact for a
**Software Quality Management** group assessment and designed to be analysed
with **SonarQube Cloud**.

> **Scenario (for the report):** *MediTrack Solutions* has built this system for
> a client clinic and is about to hand it over for go-live. Before deployment,
> the quality/QA team (your group) must produce a software quality management
> report and run a SonarQube analysis to prove the system is reliable,
> maintainable, and secure enough to handle patient data in production.

---

## 1. Tech stack

| Layer | Choice |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.3.5 (Web, Data JPA, Validation) |
| Database | H2 (in-memory) |
| Build | Maven |
| Tests | JUnit 5 + Mockito |
| Coverage | JaCoCo → `target/site/jacoco/jacoco.xml` |
| Analysis | SonarQube Cloud |

---

## 2. Run it locally

You need **JDK 21** and **Maven** installed.

```bash
# from the project root
mvn spring-boot:run
```

The app starts on `http://localhost:8080`.

Try the API (another terminal):

```bash
# register a patient
curl -X POST http://localhost:8080/api/patients \
  -H "Content-Type: application/json" \
  -d '{"name":"Jane Doe","email":"jane@x.com","phone":"07000000000","nhsNumber":"NHS001","age":34}'

# list patients
curl http://localhost:8080/api/patients

# book an appointment for patient 1
curl -X POST http://localhost:8080/api/appointments \
  -H "Content-Type: application/json" \
  -d '{"patientId":1,"clinicianName":"Dr Smith","department":"general","scheduledFor":"2026-11-10T10:00:00"}'
```

H2 console (to see the tables): `http://localhost:8080/h2-console`
(JDBC URL `jdbc:h2:mem:meditrack`, user `sa`).

---

## 3. Run the tests + coverage

```bash
mvn clean verify
```

This runs all unit tests and writes the coverage report to:

```
target/site/jacoco/jacoco.xml      <- machine-readable, read by SonarQube
target/site/jacoco/index.html      <- open in a browser to view coverage
```

If this command passes and that `jacoco.xml` file exists, your coverage
pipeline works and SonarQube will be able to report Coverage.

---

## 4. Analyse with SonarQube Cloud

There are two routes. **Route B is the one that gives you Coverage**, so for the
assessment you want Route B.

### Route A — Automatic Analysis (fast, but no coverage)

1. Push this repo to GitHub (see section 5).
2. Go to <https://sonarcloud.io> → **Log in → With GitHub**.
3. Create an organization, then **Import** this repository (it becomes a project).
4. Keep **Automatic Analysis** on. Within minutes you get: Bugs, Code Smells,
   Vulnerabilities, Security Hotspots, Duplications.
5. **Coverage will show 0%** — Automatic Analysis does not build or run tests.

### Route B — CI analysis via GitHub Actions (all six metrics, incl. coverage)

A ready-made workflow is included at `.github/workflows/build.yml`. To use it:

1. Import the project into SonarQube Cloud as in Route A, but **turn OFF
   Automatic Analysis** (Project → Administration → Analysis Method → choose
   "with GitHub Actions"). You can't run both.
2. In SonarQube Cloud, open your project → **Information** and copy your
   **Project Key** and **Organization Key**.
3. Edit `.github/workflows/build.yml` and replace `YOUR_PROJECT_KEY` and
   `YOUR_ORG_KEY` with those values. (You can also set them in `pom.xml` under
   `<sonar.projectKey>` / `<sonar.organization>`.)
4. In SonarQube Cloud, generate a token (**My Account → Security → Generate Token**).
5. In GitHub: **repo → Settings → Secrets and variables → Actions → New
   repository secret**, name it `SONAR_TOKEN`, paste the token.
6. Push a commit. The Action runs `mvn verify` (tests + JaCoCo) and uploads
   code **and coverage** to SonarQube Cloud.

### Route B alternative — scan from your own laptop

If you'd rather not use GitHub Actions:

```bash
mvn clean verify
mvn sonar:sonar \
  -Dsonar.projectKey=YOUR_PROJECT_KEY \
  -Dsonar.organization=YOUR_ORG_KEY \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.token=YOUR_SONAR_TOKEN
```

---

## 5. Push this project to GitHub

```bash
cd meditrack
git init
git add .
git commit -m "Initial commit: MediTrack clinic system"
git branch -M main
git remote add origin https://github.com/<your-username>/meditrack.git
git push -u origin main
```

Make the repository **Public** so SonarQube Cloud analysis is free with no
line-of-code limit.

---

## 6. What SonarQube will find (and why it's here)

This codebase intentionally contains realistic, junior-level quality issues so
your report's analysis sections have genuine findings to discuss. Nothing here
is malicious — these are the everyday mistakes a quality gate is meant to catch.
Use this table as a starting checklist; confirm the exact issues against your
own dashboard once you scan.

| SonarQube category | Where to look | What it is |
|---|---|---|
| **Security Hotspot** | `util/SecurityUtil.java` | Uses **MD5** (weak hashing algorithm) |
| **Security Hotspot** | `util/SecurityUtil.java` | **Hardcoded** salt constant |
| **Security Hotspot** | `resources/application.properties` | **Hardcoded** database password |
| **Code Smell** | `util/SecurityUtil.java` | **Empty catch block** |
| **Code Smell** | `service/AppointmentService.java` | `System.out.println` left in code |
| **Code Smell** | `service/AppointmentService.java` | **Unused field** `bookingCounter` |
| **Code Smell** | `service/AppointmentService.java` | High **cognitive complexity** + **magic numbers** in `reminderLeadTimeHours` |
| **Code Smell** | `service/PatientService.java` | **Magic numbers** + complexity in `ageBand` |
| **Code Smell / Duplication** | `PatientService` & `AppointmentService` | The validation block is **duplicated** across both services |

After your first scan, read each real finding on the dashboard (it names the
file, line, severity, and an explanation) and write your analysis against the
MediTrack healthcare context — e.g. frame the MD5 hotspot as a risk to
pseudonymised patient identifiers, not just a generic weakness.

---

## 7. Project structure

```
meditrack/
├── pom.xml                        # Maven build + JaCoCo + Sonar config
├── README.md
├── .gitignore
├── .github/workflows/build.yml    # CI: build, test, coverage, Sonar scan
└── src/
    ├── main/java/com/meditrack/
    │   ├── MediTrackApplication.java
    │   ├── model/        (Patient, Appointment, AppointmentStatus)
    │   ├── repository/   (PatientRepository, AppointmentRepository)
    │   ├── service/      (PatientService, AppointmentService)
    │   ├── controller/   (PatientController, AppointmentController)
    │   ├── util/         (SecurityUtil)
    │   └── exception/    (ResourceNotFoundException)
    ├── main/resources/
    │   └── application.properties
    └── test/java/com/meditrack/
        ├── model/        (PatientTest)
        ├── service/      (PatientServiceTest, AppointmentServiceTest)
        └── util/         (SecurityUtilTest)
```
