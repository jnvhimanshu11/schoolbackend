# ATLAS Academy — Spring Boot Backend

A clean, production-ready REST API for the ATLAS Academy school website.
Admin login uses **Gmail OTP only** — no passwords stored anywhere.

---

## Project Structure

```
src/main/java/com/atlas/academy/
├── AtlasAcademyBackendApplication.java   ← Entry point
│
├── config/
│   ├── DataSeeder.java                   ← Seeds first admin on startup
│   ├── OtpCleanupScheduler.java          ← Deletes expired OTPs daily
│   ├── SecurityConfig.java               ← Which routes are public vs protected
│   └── WebConfig.java                    ← Serves uploaded gallery images
│
├── controller/                           ← HTTP layer (receives requests, returns responses)
│   ├── AuthController.java               ← POST /auth/request-otp, POST /auth/verify-otp
│   ├── AdmissionController.java          ← /admissions
│   ├── InquiryController.java            ← /inquiries
│   ├── GalleryController.java            ← /gallery
│   ├── NoticeController.java             ← /notices
│   ├── FacultyController.java            ← /faculty
│   └── SettingController.java            ← /settings
│
├── dto/                                  ← Data Transfer Objects (request/response shapes)
│   ├── ApiResponse.java                  ← { success, message, data } wrapper for ALL responses
│   ├── PagedResponse.java                ← { content, pageNumber, totalPages, ... }
│   ├── auth/                             ← RequestOtpRequest, VerifyOtpRequest, LoginResponse
│   ├── admission/, inquiry/, notice/
│   ├── faculty/, setting/
│
├── entity/                               ← JPA entities = DB tables
│   ├── Admin.java                        ← id, email, name, active (NO password)
│   ├── OtpToken.java                     ← email, otpHash, expiresAt, used, attemptCount
│   ├── Admission.java, Inquiry.java
│   ├── GalleryItem.java, Notice.java
│   ├── Faculty.java, Setting.java
│
├── exception/                            ← Error handling
│   ├── AuthException.java
│   ├── ResourceNotFoundException.java
│   └── GlobalExceptionHandler.java       ← Converts ALL exceptions to { success: false, message }
│
├── repository/                           ← Database queries (Spring Data JPA)
│   └── *Repository.java (one per entity)
│
├── security/
│   ├── JwtUtil.java                      ← Generates & validates JWT tokens
│   ├── JwtAuthFilter.java                ← Reads Bearer token from every request header
│   └── JwtAuthEntryPoint.java            ← Returns clean 401 JSON instead of HTML error page
│
├── service/                              ← Business logic layer
│   ├── AuthService.java                  ← OTP creation + verification logic
│   ├── MailService.java                  ← Sends email via Gmail SMTP
│   ├── FileStorageService.java           ← Saves gallery image files to disk
│   └── *Service.java (one per entity)
│
└── util/
    └── OtpGenerator.java                 ← Generates random 6-digit OTP using SecureRandom
```

---

## Prerequisites

| Tool | Version | Download |
|------|---------|----------|
| Java JDK | 17 or later | https://adoptium.net |
| Maven | 3.8+ | https://maven.apache.org |
| MySQL | 8.0+ | https://dev.mysql.com/downloads |

---

## Step 1 — Set up MySQL

```sql
-- Open MySQL CLI and run:
CREATE DATABASE atlas_academy;
-- That's it. Hibernate creates all tables automatically on first run.
```

---

## Step 2 — Get a Gmail App Password

> ⚠️ This is the most important setup step. A Gmail App Password is a
> 16-character code that lets our app send email *on behalf of* your Gmail
> account, without needing your real password. It is NOT your normal password.

1. Make sure **2-Step Verification** is turned ON for your Google account:
   → https://myaccount.google.com/security

2. Go to **App Passwords**:
   → https://myaccount.google.com/apppasswords

3. Under "Select app" choose **Mail**, under "Select device" choose **Other**,
   type "Atlas Academy" and click **Generate**.

4. Copy the 16-character code shown (e.g. `abcd efgh ijkl mnop`).
   **Remove the spaces** when you paste it into `application.properties`.

---

## Step 3 — Configure `application.properties`

Open `src/main/resources/application.properties` and fill in:

```properties
# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/atlas_academy?...
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_ROOT_PASSWORD

# Gmail — replace these two lines
spring.mail.username=youremail@gmail.com
spring.mail.password=abcdefghijklmnop     # 16-char App Password, no spaces

# JWT Secret — generate a long random string (minimum 32 characters)
app.jwt.secret=ChangeThisToALongRandomSecretKeyAtLeast256BitsForHS256AlgoSecurity

# The email that gets auto-created as the first admin on startup
app.seed.admin-email=admin@atlasacademy.in
app.seed.admin-name=Admin

# Your frontend URL (for CORS)
app.cors.allowed-origins=http://localhost:5173
```

---

## Step 4 — Run the Backend

```bash
# From the project root (where pom.xml is):
mvn spring-boot:run
```

You should see:
```
Tomcat started on port(s): 8080
Seeded default admin: admin@atlasacademy.in
Started AtlasAcademyBackendApplication
```

All API routes are now available at `http://localhost:8080/api/`

---

## Step 5 — Update Frontend to Point to Backend

In your frontend `src/services/api.js`, make sure the base URL is:
```js
baseURL: 'http://localhost:8080/api'
```

---

## Login Flow (How OTP Auth Works)

```
Admin opens /admin/login
     │
     ▼
Types their email → Frontend calls POST /api/auth/request-otp { email }
     │
     ▼
Backend checks: is this email in the admins table AND active = true?
     │
     ├── No  → Silently do nothing (same response either way — prevents
     │          attackers from knowing which emails are valid admins)
     │
     └── Yes → Generate 6-digit OTP using SecureRandom
               → Hash it with BCrypt (NEVER store plain OTP)
               → Save hashed OTP + expiry (5 min) to otp_tokens table
               → Send plain OTP to admin's email via Gmail SMTP
               → Return: { success: true, message: "If registered, OTP sent" }
     │
     ▼
Admin receives email with 6-digit code, enters it on the login page
     │
     ▼
Frontend calls POST /api/auth/verify-otp { email, otp }
     │
     ▼
Backend:
  1. Find latest unused OTP for this email
  2. Check it hasn't expired (5 min window)
  3. Check attempt count < 5 (brute-force protection)
  4. BCrypt.matches(submitted_otp, stored_hash)
  5. Mark OTP as used (can't replay it)
  6. Generate JWT → return { success: true, data: { token, email, name } }
     │
     ▼
Frontend stores token in localStorage, attaches it as
"Authorization: Bearer <token>" header on all future requests
```

---

## API Endpoints Reference

### Auth (No login required)
| Method | URL | Body | Description |
|--------|-----|------|-------------|
| POST | `/auth/request-otp` | `{ email }` | Step 1: send OTP to email |
| POST | `/auth/verify-otp` | `{ email, otp }` | Step 2: verify OTP, get JWT |

### Public Data (No login required)
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/gallery` | All gallery images (optional `?category=`) |
| GET | `/notices` | All notices |
| GET | `/faculty` | All faculty |
| GET | `/settings/public` | Public site settings as `{ key: value }` map |
| POST | `/admissions` | Submit admission form (public) |
| POST | `/inquiries` | Submit contact/inquiry form (public) |

### Admin Only (JWT required — `Authorization: Bearer <token>`)
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/admissions?page=0&status=PENDING` | List admissions (paginated) |
| PUT | `/admissions/{id}/status?status=APPROVED` | Update admission status |
| DELETE | `/admissions/{id}` | Delete admission |
| GET | `/inquiries?page=0` | List inquiries (paginated) |
| PUT | `/inquiries/{id}` | Update inquiry (mark resolved etc.) |
| DELETE | `/inquiries/{id}` | Delete inquiry |
| POST | `/gallery` | Upload image (multipart/form-data) |
| DELETE | `/gallery/{id}` | Delete gallery image |
| POST | `/notices` | Create notice |
| PUT | `/notices/{id}` | Update notice |
| DELETE | `/notices/{id}` | Delete notice |
| POST | `/faculty` | Add faculty member |
| PUT | `/faculty/{id}` | Update faculty |
| DELETE | `/faculty/{id}` | Delete faculty |
| GET | `/settings` | All settings (including private) |
| PUT | `/settings` | Upsert a setting `{ key, value, isPublic? }` |

---

## All Responses Use the Same Shape

Every endpoint — success or error — returns:
```json
{
  "success": true,
  "message": "Login successful",
  "data": { ... }
}
```

Errors return `"success": false` and an appropriate HTTP status code:
- `400 Bad Request` — validation errors (body has field-level errors in `data`)
- `401 Unauthorized` — missing or invalid JWT
- `404 Not Found` — resource doesn't exist
- `500 Internal Server Error` — unexpected error

---

## Adding More Admins

There is no admin signup endpoint by design. To add a new admin:

```sql
-- In MySQL:
INSERT INTO admins (email, name, active, created_at)
VALUES ('newadmin@school.in', 'New Admin', true, NOW());
```

To deactivate an admin (they immediately lose access):
```sql
UPDATE admins SET active = false WHERE email = 'oldadmin@school.in';
```

---

## Frontend Files to Update

Two files in your frontend need to be updated to use the OTP flow.
The updated versions are in the `frontend-updates/` folder:

| Updated File | Replaces |
|-------------|---------|
| `frontend-updates/src/context/AuthContext.jsx` | `src/context/AuthContext.jsx` |
| `frontend-updates/src/pages/admin/Login.jsx` | `src/pages/admin/Login.jsx` |

Just copy them over your existing frontend files.

---

## Security Features Built In

| Threat | Protection |
|--------|-----------|
| OTP stored in plain text | ❌ Never — BCrypt hashed before saving |
| OTP reuse (replay attack) | ✅ Marked `used=true` after successful verify |
| OTP brute-force | ✅ Max 5 attempts per token before lockout |
| OTP spray (email spamming) | ✅ 60-second cooldown between requests per email |
| OTP expiry | ✅ 5-minute TTL; daily cleanup job removes old tokens |
| Admin email enumeration | ✅ Same generic response whether email is registered or not |
| CORS | ✅ Only configured origins (`app.cors.allowed-origins`) allowed |
| JWT tampering | ✅ HS256 signed with strong secret; verified on every request |
| Deactivated admin | ✅ JwtAuthFilter re-checks `active=true` on every request |
| SQL injection | ✅ Spring Data JPA uses parameterised queries everywhere |

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 3.3.4 |
| Security | Spring Security + JJWT 0.12.6 |
| Database | MySQL 8 via Spring Data JPA / Hibernate |
| Email | Spring Mail + Gmail SMTP |
| Build | Maven |
| Java | 17+ |
| Code reduction | Lombok |
