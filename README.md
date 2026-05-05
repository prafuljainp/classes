# Sales Insight Agent

A Java Maven application that reads CRM sales records from MySQL, asks Google Gemini to generate business insights, and emails the report through Gmail SMTP.

## Features

- Fetches sales data from a MySQL or MariaDB database
- Uses Google Gemini for sales analysis
- Sends the generated report by email
- Supports local `.env` configuration
- Uses SLF4J and Logback for logging
- Uses prepared statements for database access
- Includes JUnit 5 tests

## Tech Stack

- Java 11 target
- Maven
- MySQL or MariaDB
- Google Gemini API
- Gmail SMTP
- SLF4J + Logback
- JUnit 5 + Mockito

## Project Structure

```text
src/
  main/
    java/com/salesinsight/
      Main.java
      analysis/InsightAnalyzer.java
      api/GeminiClient.java
      config/ConfigLoader.java
      crm/CRMDataFetcher.java
      email/EmailSender.java
      exception/
    resources/
      config.properties
      logback.xml
  test/java/com/salesinsight/
```

## Prerequisites

- Java 11 or newer
- Maven 3.6 or newer
- MySQL or MariaDB running locally
- Google Gemini API key
- Gmail account with an App Password

For XAMPP users, start **MySQL** from the XAMPP Control Panel before running the app.

## Configuration

Do not commit real credentials. Use a local `.env` file for secrets.

Copy the example file:

```powershell
Copy-Item .env.example .env
```

Then update `.env` with your real values:

```env
DB_URL=jdbc:mysql://localhost:3306/sales
DB_USERNAME=root
DB_PASSWORD=

MAIL_SMTP_HOST=smtp.gmail.com
MAIL_SMTP_PORT=587
MAIL_SMTP_USERNAME=your-email@gmail.com
MAIL_SMTP_PASSWORD=your_gmail_app_password
MAIL_RECIPIENT=recipient@example.com

GEMINI_API_URL=https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent
GEMINI_API_KEY=your_gemini_api_key
```

`src/main/resources/config.properties` contains safe placeholder defaults. Keep real values in `.env`, environment variables, or Java system properties.

Configuration priority:

1. Java system properties
2. Environment variables
3. `.env`
4. `config.properties`

## Database Setup

Create the database and sample table:

```sql
CREATE DATABASE IF NOT EXISTS sales;
USE sales;

CREATE TABLE IF NOT EXISTS sales_data (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(255) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO sales_data (customer_name, amount, date) VALUES
('Acme Corp', 5000.00, '2024-01-15'),
('Tech Innovations', 7500.50, '2024-01-16'),
('Global Solutions', 3200.00, '2024-01-17');
```

With XAMPP, you can run this in phpMyAdmin:

```text
http://localhost/phpmyadmin
```

## Run

Normal Maven command:

```powershell
mvn exec:java
```

If PowerShell cannot run `mvn.cmd`, use the full Maven path:

```powershell
cmd /d /c call "C:\Program Files\Apache\apache-maven-3.9.9\bin\mvn.cmd" exec:java
```

## Test

```powershell
mvn test
```

Full path version:

```powershell
cmd /d /c call "C:\Program Files\Apache\apache-maven-3.9.9\bin\mvn.cmd" test
```

## Gmail App Password

Gmail SMTP requires an App Password, not your normal Gmail password.

1. Enable 2-Step Verification on your Google account
2. Open Google Account security settings
3. Create an App Password for Mail
4. Put the generated password in `MAIL_SMTP_PASSWORD`

## Security Notes

- Never commit `.env`
- Never commit real API keys or email passwords
- Rotate credentials immediately if they are accidentally pushed
- `target/`, `logs/`, and `.env` are ignored by `.gitignore`

## Troubleshooting

**Unknown database `sales`**

Create the database using the SQL in the Database Setup section.

**Gemini API key is not configured**

Set `GEMINI_API_KEY` in `.env`.

**Gmail authentication failed**

Use a Gmail App Password and remove spaces from the password.

**`mvn exec:java` fails in PowerShell**

Use the full Maven command shown in the Run section, or add Maven's `bin` folder to your Windows PATH.

## Current Status

- Application run verified
- Email delivery verified
- Maven tests passing
- Secrets kept out of tracked config
