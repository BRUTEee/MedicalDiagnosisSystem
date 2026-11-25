# Medical Diagnosis Assistance System

Java web application for patient records, appointment scheduling, educational symptom assessments, reports, and an optional medical-assistant chat backed by Ollama.

> **Important — educational prototype, not production-ready clinical software.** Assessments and assistant responses are informational only; they are not diagnoses or treatment instructions. A qualified healthcare professional must make all clinical decisions. Do not use real patient data or expose this application publicly until the production requirements below have been addressed and independently reviewed.

## Current verification status

As of November 14, 2026:

- The appointments migration was reported as successfully executed in MySQL Workbench.
- The deployed local application connected to MySQL successfully. The homepage, login page, invalid-login-CSRF rejection, demo login (before the public seed was removed), dashboard, database health route, and appointments page were smoke-tested against local Tomcat.
- Before the latest AI safety changes, `mvn -o -B clean package` completed with 51 tests passing and the generated WAR excluded the local `WEB-INF/app.properties` secrets file.
- The latest verified source state compiled all main and test Java sources with JDK 23, and all 72 JUnit tests ran successfully using the cached JUnit Platform; Maven is not available on PATH, so a fresh Maven package/WAR check was not possible.
- This verifies local source compilation and automated tests only. It does **not** establish production security, clinical validity, deployment readiness, or complete end-to-end testing of every workflow.

## Features

- Sign-in, sign-out, profile settings, and password changes.
- A separate synthetic-data-only patient portal demo; it does not access or link to live patient records and is disabled until configured.
- Patient search, patient record creation and editing, patient details, and assessment history.
- Appointment scheduling and status updates (scheduled, completed, cancelled).
- Rule-based symptom assessments saved as patient history, with warning-sign keyword checks.
- Assessment summaries and CSV export.
- Optional assistant chat using a locally hosted Ollama model.
- CSRF protection for sign-in and authenticated state-changing requests; authenticated pages are marked not to cache.

Patients with saved assessments or appointments cannot be deleted. The application has a single signed-in access level; the `role` field is not currently used for authorization.

## Product vision and delivery plan

### Vision

Build a trustworthy, accessible, doctor-led health services platform for people who need care—including people in rural communities—and for qualified doctors who may work from home or outside a conventional clinic. Patients should be able to find genuinely verified doctors, understand their services, and book online or in-person appointments. Doctors should control their profiles, availability, and how they participate. The service should make the relationship between patient and doctor central; AI is optional support, never a substitute for a clinician.

This is the agreed product direction for planning, not a claim that these marketplace capabilities exist today. The application is currently a clinician-facing local prototype. It has no patient login, public doctor directory, doctor verification workflow, patient-to-doctor booking, doctor-owned availability, patient-doctor messaging, or telehealth consultation feature.

### Planned phases

Phases are intentionally sequenced. Do not open registration, accept real patient information, or launch a public pilot just because a phase is implemented; each release needs its own security, privacy, clinical, and operational readiness review.

1. **Product and safety foundation:** Define the initial India pilot's users, care scope, eligibility, support model, language/accessibility needs, consent, data retention, emergency escalation, and operating responsibilities. Obtain qualified India-specific legal, privacy, security, and clinical advice before real-world use. Map current data and authorization risks; preserve existing local prototype behavior while preparing the data model for multiple doctors and patients.
2. **Doctor onboarding and verification:** Create doctor accounts and profiles, collect only necessary identity and professional credentials through a controlled process, and require a human-admin review before a profile can be marked verified or publicly listed. Record verification state, reviewer, timestamps, expiry/recheck needs, and changes. Define a takedown, complaint, and re-verification process with qualified local advice. Never let a user self-assign a verified badge.
3. **Patient accounts and informed choice:** Add patient registration, secure sign-in and account recovery, privacy notices, consent capture and withdrawal, and patient-controlled profile/history sharing. Keep patient account data separate from the existing staff-entered patient records until an explicit, reviewed migration and ownership model exists.

### Current security findings and data-isolation boundary

The present staff prototype has one effective trust level: any signed-in account can access global patient, diagnosis, appointment, and report data because authorization is not scoped by owner. `PatientDAO` has global search/list, count, aggregation, and direct-ID queries; `DiagnosisDAO` and `AppointmentDAO` expose global lists, recent records, and counts; `ReportServlet` returns all assessment rows and aggregate patient statistics in both its page and CSV export. These are all owner-scope migration touchpoints, not just the detail views. Do not add multiple clinicians, patient accounts, or public access on top of this schema. Before multi-user workflows, add explicit ownership relationships and migrate current records under a documented, operator-approved mapping; do not guess which clinician owns historic records. Define how unassignable legacy records remain restricted, and verify a backed-up, reversible migration before execution. Enforce owner/role checks in every DAO operation and endpoint, including search, aggregate reports, direct-ID reads, updates, deletes, and exports, and test cross-account denial at the service boundary. The current shared-data behavior remains restricted to a trusted local prototype.
4. **Doctor discovery and booking:** Publish only approved doctor profiles; allow patients to browse services, languages, appointment modes (online or in-person), and available times. Add doctor-managed availability, time-zone-aware slots, conflict-safe booking, rescheduling/cancellation rules, and clear booking confirmations. The planned first booking model is for patients to book an open slot. Handle double-booking safely and provide an operational fallback when notifications or the service are unavailable.
5. **Care communication:** Add secure patient-doctor messaging and, if approved and selected, integrate a dedicated telehealth provider for online appointments. Define consent, identity checks, records, access, retention, outage, escalation, and support workflows. The existing AI chat is not doctor messaging or a consultation channel.
6. **Governed AI assistance:** Keep a basic, clearly labeled educational assistant as an optional feature. Improve it in controlled steps under clinician, privacy, and security governance as described below. Release changes only after documented evaluations, approvals, monitoring, and a tested disable/rollback path.
7. **Controlled pilot and operations:** Begin with a limited, invited pilot only after the preceding product, legal/privacy, security, clinical, support, and operational gates are signed off. Measure access, booking completion, reliability, complaints, safety issues, and clinician/patient experience without treating usage volume as evidence of clinical benefit. Expand only after reviewing pilot outcomes and resolving failures.

### What doctor verification means

“Verified” must describe a documented process, not an unreviewed profile claim or a platform guarantee of care quality. The future workflow should show patients what was checked and when, distinguish identity/credential checks from endorsements, make review status and expiry visible to authorized staff, prevent unapproved profiles from appearing in search, and provide a way to report problems. Verification criteria and records require qualified review for the pilot jurisdiction and profession.

### AI behavior and clinician-guided improvement

**What exists now:** The optional Ollama chat is a prototype prompted to provide general information; the separate symptom assessment uses fixed rules and warning-sign keywords. Neither is trained or fine-tuned by doctors, clinically validated, or a reliable emergency detector. The chat only permits HTTPS remote Ollama endpoints or local HTTP loopback endpoints; this transport check does not establish the endpoint's retention, access, security, or clinical suitability. A patient message or doctor review in the future would not automatically make the model trained, correct, safe, or suitable for care.

**Near-term behavior target:** Keep AI bounded to plain-language health education, helping users organize questions or describe concerns for a clinician, and general navigation of the service. The assistant must identify itself as AI, avoid claiming to be a doctor, clearly state uncertainty, avoid definitive diagnosis or individualized prescriptions/treatment plans, and direct users to qualified care for personal medical decisions. It must not discourage urgent care or use a reassuring answer to override a possible emergency. Until clinician-approved local escalation wording and pathways are implemented and tested, do not present the assistant as triage or emergency support. Minimize requests for identifying or sensitive details, explain that the chat is not a substitute for a consultation, and make human care options visible.

**Doctor and patient contributions:** The product should give doctors and patients meaningful, separate roles: doctors contribute expertise and review educational material; patients may voluntarily provide feedback about clarity, usefulness, accessibility, and missing topics. Patient feedback is not medical evidence or consent to train a model. Never automatically train on conversations, patient records, consultations, or uploaded documents. Before using any patient-originated information beyond the immediate service, specify the purpose, obtain appropriate informed consent, minimize and protect the data, assess re-identification risk, define retention/deletion, and obtain privacy, legal, security, and clinical approval. Do not use identifiable real patient data for development or evaluation without an approved governance process.

**Improvement path:** Start with clinician-curated, dated, source-tracked educational content and reviewed example responses. A retrieval system that presents reviewed sources to a model (RAG) is different from fine-tuning: it does not change model parameters and is not, by itself, proof of correctness or safety. Any later model training/fine-tuning must be a separately approved project with documented rights and consent, dataset provenance and minimization, de-identification and re-identification risk review, security controls, clinician-led labels, independent evaluation, and a release/rollback process. Version the model/provider, prompt, rules, and source set; evaluate factuality, harmful omissions, escalation, language/accessibility, and subgroup performance before each release; monitor incidents; and keep a tested option to disable the AI. A clinician reviewing outputs remains essential and is not replaced by model training.

## Requirements

- JDK 23 or newer
- Apache Tomcat 11 (Jakarta Servlet 6.1)
- MySQL 8
- Maven 3.8.7 or newer to build
- Optional: Ollama and the configured model (default `qwen2.5:1.5b`)

The default local application address is `http://localhost:8080/MedicalDiagnosisSystem/`.

## Database setup

### New installation

Use MySQL Workbench or a MySQL command-line client with an administrator account to run [`Database/schema.sql`](Database/schema.sql). In Workbench, connect to the intended server, open the SQL file, review it, and execute the script. It creates the `medical_diagnosis` database and the required `users`, `patients`, `diagnoses`, and `appointments` tables, creates no application login, and grants table-specific privileges to the application account (no DELETE on diagnoses or appointments, and no INSERT on users).

Before using the script, replace `CHANGE_ME_STRONG_PASSWORD` with a strong, unique database password. For an existing `medical_app@localhost` account, set its password as an administrator; `CREATE USER IF NOT EXISTS` does not reset an existing account's password. Use the same password in the application configuration. Never grant the application account administrator or schema-modification privileges. Existing installations that previously received schema-wide CRUD grants must run [`Database/restrict-app-user.sql`](Database/restrict-app-user.sql) as an administrator. The script prints the account's grants afterward, but it cannot revoke grants inherited globally, through roles, or from other matching MySQL accounts; inspect `SHOW GRANTS FOR 'medical_app'@'localhost'` and all applicable role/global grants, then remove any excess privileges explicitly. Back up the database and verify access on a local test instance before use. These table grants do not provide row-level patient isolation: all signed-in application accounts still share the same data.

### Existing installation needing appointments

The appointments migration has been applied to the current local database. For another existing database that lacks the table, first make and verify a backup. Then, in MySQL Workbench connected to that database's server, open [`Database/migrate-appointments.sql`](Database/migrate-appointments.sql), confirm the script targets `medical_diagnosis`, and execute it with an account allowed to create tables. Verify with:

```sql
USE medical_diagnosis;
SHOW CREATE TABLE appointments;
```

The migration creates the appointments table if it does not already exist. It does not replace a full backup or rollback plan.

## Application configuration

Copy [`WebContent/WEB-INF/app.properties.example`](WebContent/WEB-INF/app.properties.example) to `WebContent/WEB-INF/app.properties` for local development and set the database password. The completed file is ignored by Git and excluded from the WAR. Do not commit credentials or put them in screenshots, logs, or support messages.

The demo patient portal is disabled until configured. Set a synthetic patient ID and name plus `patient-demo.password-hash` in the protected local `WEB-INF/app.properties`; generate the PBKDF2 hash with `java -cp target/classes com.medical.util.PasswordHashTool` in an interactive terminal. It is intentionally not linked to the clinical database and must not be configured with real patient identity or data.


| Property | Environment variable | Default |
| --- | --- | --- |
| `db.url` | `MDS_DB_URL` | `jdbc:mysql://localhost:3306/medical_diagnosis` |
| `db.username` | `MDS_DB_USERNAME` | `medical_app` |
| `db.password` | `MDS_DB_PASSWORD` | Not set |
| `ollama.url` | `MDS_OLLAMA_URL` | `http://localhost:11434/api/chat` |
| `ollama.model` | `MDS_OLLAMA_MODEL` | `qwen2.5:1.5b` |
| `security.require-https` | `MDS_SECURITY_REQUIRE_HTTPS` | `false` (local development only) |
| `login.max-failures` | `MDS_LOGIN_MAX_FAILURES` | `5` |
| `login.window-seconds` | `MDS_LOGIN_WINDOW_SECONDS` | `900` |
| `login.lock-seconds` | `MDS_LOGIN_LOCK_SECONDS` | `900` |
| `login.max-clients` | `MDS_LOGIN_MAX_CLIENTS` | `10000` |


Configuration precedence is environment variable, JVM system property, then `WEB-INF/app.properties`, then the documented default. For a deployed WAR, configure these values in the Tomcat process environment or use an appropriately protected configuration file in the extracted application directory. Restart/redeploy after changing the Tomcat process environment. Keep production secrets in an approved secret manager, not in the WAR or source tree.

The assistant is optional. To use the default local model, install Ollama, start its service, and download the model:

```text
ollama pull qwen2.5:1.5b
```

Patient, appointment, assessment, and report features do not require Ollama. Restrict access to the Ollama endpoint; do not expose it to the public internet.

## Build and deploy

Build and run unit tests from the project root:

```text
mvn clean package
```

For the verified offline build used in this environment:

```text
mvn -o -B clean package
```

Copy `target/MedicalDiagnosisSystem.war` into Tomcat's `webapps` directory and start or restart Tomcat. Ensure the **Tomcat process itself** uses JDK 23 or newer. Provide the database configuration to that process before starting it. Visit `/MedicalDiagnosisSystem/` under the Tomcat host and sign in.

The schema creates no application login. After building the application, create the first account yourself using a unique, strong password. Generate a PBKDF2 hash in an interactive terminal (the password is read without echo and is not supplied as a command-line argument):

```text
java -cp target/classes com.medical.util.PasswordHashTool
```

Insert the account using the generated hash and your own name/email:

```sql
INSERT INTO users (name, email, password, role)
VALUES ('Your Name', 'you@example.com', '<generated-pbkdf2-hash>', 'DOCTOR');
```

Do not put the plaintext password in SQL, source control, or shell history. The schema change does not remove or alter accounts in an existing database. If that database contains the old publicly known demo account, inspect it and disable or replace it before enabling shared access; do not change unrelated accounts. This schema is not a user migration and does not make existing accounts private or owner-scoped.

When redeploying to an existing Tomcat installation, check the extracted application's configuration as well as the WAR. Tomcat may preserve files from an earlier deployment; confirm it is using the intended database and secrets before entering or processing data.

## Main pages and routes

Routes below are relative to `/MedicalDiagnosisSystem` and require sign-in unless noted.

| Route | Purpose |
| --- | --- |
| `/` | Public home page |
| `/login` | Sign-in (public; POST requires a CSRF token) |
| `/logout` | Sign-out via POST |
| `/dashboard` | Authenticated application dashboard |
| `/patients` | Patient list/search and patient record operations |
| `/patient-details?id=ID` | Patient details and saved assessments |
| `/diagnosis` | New symptom assessment; `?id=ID` shows a saved assessment |
| `/appointments` | Appointment list, scheduling, and status updates |
| `/reports` | Reports; add `?format=csv` to download assessment CSV |
| `/settings` | Profile and password settings |
| `/medical-assistant` | Optional Ollama-backed chat page |
| `/database-test` | Authenticated database connectivity check; reports only success/failure |

If the appointments table is unavailable, the appointments page displays a setup message instead of making appointment operations available.

## Tests and validation

Run tests with:

```text
mvn test
```

The unit suite covers CSV escaping, application configuration cleanup, password hashing/verification, login throttling, patient validation, symptom analysis, HTML escaping, HTTPS enforcement, and AI assistant request/policy behavior. It does not replace integration, security, usability, or clinical validation. After the login-throttling change, all main and test Java sources compiled with JDK 23 and all 72 JUnit tests passed using the cached JUnit Platform and runtime dependencies, including servlet-level verification of HTTP 429 and `Retry-After` behavior. Maven is unavailable on PATH, so a fresh Maven package/WAR check remains unverified; rerun `mvn -o -B clean package` when Maven is available.

## Security, privacy, clinical governance, and launch gates

The application currently includes password hashing with PBKDF2-HMAC-SHA256, login and authenticated-request CSRF checks, per-client-IP sign-in throttling (five failures in 15 minutes trigger a 15-minute lock by default), security headers including a nonce-based Content Security Policy, HttpOnly session cookies with `SameSite=Lax`, cookie-only session tracking, and no-store responses for authenticated pages. The throttle is bounded and process-local; it is not cluster-wide and must be complemented by edge/WAF controls and shared state before a multi-instance deployment. It keys only on the remote client IP and does not trust forwarded headers, so deployments behind a proxy must correctly configure trusted client addresses. `security.require-https=true` rejects requests unless the servlet container marks them secure and automatically enables the session cookie's Secure attribute; validate forwarded-proto handling only at a trusted proxy and prevent direct untrusted access to Tomcat. HTTPS enforcement defaults to `false` for local HTTP development. These safeguards are not a complete production security review.

The intended initial market is India, with patient booking of open slots for both online and in-person appointments. This is a product-planning decision only. No pilot state/city, clinical specialties, credential-verification criteria, telehealth provider, deployment environment, privacy/legal advice, clinical governance process, or production support model has yet been selected or verified. Obtain qualified India-specific legal, privacy, regulatory, security, and clinical review for the precise intended service; this README makes no claim of compliance or legal interpretation.

Before public registration, a pilot, or use with real patient information, close and independently verify at least these gates:

1. **Jurisdiction and operating model:** Confirm pilot location, user eligibility, appointment scope, online-care boundaries, language/accessibility, support coverage, emergency process, complaints, incident response, and who operates each service.
2. **Doctor and patient trust:** Implement a human-reviewed verification lifecycle; distinct patient, doctor, and administrator roles; server-enforced least-privilege authorization; safe account recovery; consent, privacy notice, sharing controls, retention/deletion rules, and auditable access. Existing data is not yet safely partitioned by doctor or patient account.
3. **Secure deployment:** Set `security.require-https=true`; configure and verify TLS and trusted proxy handling at the reverse proxy/Tomcat connector; restrict direct network access; verify session cookies are Secure, HttpOnly, and SameSite=Lax in the deployed response; protect secrets; configure shared/edge rate limits; scan dependencies; and test authorization, CSRF, session handling, and error paths. HTTPS enforcement and cookie attributes must be tested in the actual deployment, not inferred from configuration alone.
4. **Patient-data operations:** Use least-privileged database credentials, restricted network paths, appropriate encryption, monitored backups and tested restores, audit logging, monitoring/alerting, data retention and deletion processes, incident response, patching, and rollback procedures.
5. **Clinical and AI governance:** Define accountable clinicians and review/change-approval processes; validate user journeys, symptom guidance, escalation wording, AI content and known limitations; evaluate safety, factuality, omissions, language/accessibility and relevant subgroups; monitor incidents; version components; and test disabling/rolling back the assistant. Do not claim doctor training or clinical validation that has not occurred.
6. **End-to-end launch review:** Test the exact production deployment, appointment concurrency and notifications, identity/verification operations, patient consent and account flows, online/in-person workflows, support procedures, backup restoration, and outage/incident response with qualified reviewers before inviting users.

Until these gates have evidence and sign-off, keep this as a local development/educational prototype, use fictional or synthetic data, and do not market it as a medical service or production-ready platform. The current application is confirmed working in the tested local setup only; **production readiness is not confirmed**.
