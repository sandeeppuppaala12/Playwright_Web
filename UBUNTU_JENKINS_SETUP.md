# Ubuntu Jenkins Deployment Guide for Playwright Web Automation

## Overview
This guide provides step-by-step instructions for deploying the Playwright Web Automation framework on an Ubuntu Jenkins server for parallel execution across Chrome, Firefox, and Edge browsers.

---

## Prerequisites

### System Requirements
- **OS**: Ubuntu 20.04 LTS or later
- **Architecture**: x86_64
- **Disk Space**: Minimum 20GB (for browser binaries and artifacts)
- **RAM**: Minimum 8GB

### Software Requirements
- Java 17 or higher
- Maven 3.8.0 or higher
- Git
- sudo access for package installation

---

## Step 1: Jenkins Agent Configuration

### 1.1 Prepare Ubuntu for Jenkins

```bash
# Update system packages
sudo apt-get update
sudo apt-get upgrade -y

# Install Java 17
sudo apt-get install -y openjdk-17-jdk-headless

# Verify Java installation
java -version
```

### 1.2 Install Maven

```bash
# Download and install Maven
sudo apt-get install -y maven

# Verify Maven installation
mvn -version
```

### 1.3 Install Git

```bash
sudo apt-get install -y git
```

---

## Step 2: Install Playwright Dependencies

### 2.1 Install System Dependencies

Playwright requires specific system libraries to run browsers on Linux:

```bash
# Core dependencies
sudo apt-get install -y \
    libxss1 \
    libgconf-2-4 \
    libx11-6 \
    libx11-xcb1 \
    libxcb1 \
    libxext6 \
    libxrandr2 \
    libnss3 \
    libdrm2 \
    libgbm1 \
    libpango-1.0-0 \
    libpango-gobject-1.0-0 \
    fonts-liberation \
    xdg-utils \
    wget
```

### 2.2 Install Browsers

Option A: Install via package manager (Recommended)

```bash
# Chromium
sudo apt-get install -y chromium-browser

# Firefox
sudo apt-get install -y firefox

# Note: Edge is not available via default Ubuntu repos
# You can install from Microsoft Edge for Linux sources:
curl https://packages.microsoft.com/keys/microsoft.asc | gpg --dearmor > microsoft.gpg
sudo install -o root -g root -m 644 microsoft.gpg /etc/apt/trusted.gpg.d/
sudo sh -c 'echo "deb [arch=amd64,arm64,armhf signed-by=/etc/apt/trusted.gpg.d/microsoft.gpg] https://packages.microsoft.com/repos/edge stable main" > /etc/apt/sources.list.d/microsoft-edge-dev.list'
sudo apt-get update
sudo apt-get install -y microsoft-edge-stable
```

Option B: Let Playwright handle browser installation

```bash
# Playwright will download browsers on first run
# This is handled by the Maven build process
```

---

## Step 3: Configure Jenkins

### 3.1 Create Jenkins Credentials

1. Go to Jenkins Dashboard → Manage Jenkins → Manage Credentials
2. Add credentials for Git repository (if private)
3. Configure email notifications for test results

### 3.2 Create New Pipeline Job

1. Jenkins Dashboard → New Item
2. Enter job name: `Playwright_Web_Automation`
3. Select "Pipeline"
4. Click OK

### 3.3 Configure Pipeline

In the Pipeline section:

```
Definition: Pipeline script from SCM
SCM: Git
Repository URL: <your-github-repo-url>
Credentials: <select appropriate credentials>
Branch Specifier: */main (or your branch)
Script Path: Jenkinsfile
```

### 3.4 Configure Build Triggers (Optional)

```
Trigger builds remotely (e.g., from scripts):
  - Authentication token: <generate-token>

Or use:
- GitHub hook trigger for GITScm polling
- Poll SCM: H/30 * * * * (every 30 minutes)
```

---

## Step 4: Prepare Project Repository

### 4.1 Ensure Project Structure

```
playwright_web/
├── pom.xml
├── testng.xml
├── Jenkinsfile
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/automation/web/
│   │   │       ├── manager/
│   │   │       ├── pages/
│   │   │       └── utils/
│   │   └── resources/
│   │       ├── Config.properties
│   │       └── logback.xml
│   └── test/
│       ├── java/
│       │   └── com/automation/web/
│       │       ├── listeners/
│       │       └── tests/
│       └── resources/
├── Reports/
│   └── Screenshots/
├── logs/
└── Samples/
```

### 4.2 Update Config.properties for Linux

```bash
# src/main/resources/Config.properties
# Ensure these settings are Linux-compatible

URL=https://www.globalsqa.com/demo-site/
DEFAULT_BROWSER=Chrome
HEADLESS=true
ENVIRONMENT=QA
VERSION=1.0.0

# Note: Use relative paths, File.separator handles path separators
```

### 4.3 Update logback.xml for Linux

```bash
# Ensure log directory is writable by Jenkins user
# src/main/resources/logback.xml should have:
# <property name="LOG_DIR" value="${user.dir}/logs"/>
```

---

## Step 5: Directory Permissions

### 5.1 Set Proper Permissions

```bash
# Navigate to project directory
cd /path/to/playwright_web

# Set permissions for Jenkins user
sudo chown -R jenkins:jenkins /path/to/playwright_web
sudo chmod -R 755 /path/to/playwright_web

# Ensure critical directories are writable
chmod -R 777 Reports
chmod -R 777 logs
chmod -R 777 Samples
chmod -R 777 target
```

---

## Step 6: Test the Setup

### 6.1 Manual Build Test

```bash
# Login as jenkins user
sudo su - jenkins

# Navigate to project directory
cd /path/to/playwright_web

# Run Maven build
mvn clean test -DsuiteXmlFile=testng.xml

# Check if reports are generated
ls -la Reports/
```

### 6.2 Verify Parallel Execution

The test should generate reports with:
- `validateMenuLinks_Chrome`
- `validateMenuLinks_Firefox`
- `validateMenuLinks_Edge`

Check Reports folder:
```bash
ls -la Reports/Report_*.html
# Should show single report with all three browser executions
```

---

## Step 7: Jenkins Job Execution

### 7.1 Trigger Build

1. Go to Jenkins Dashboard
2. Click on `Playwright_Web_Automation` job
3. Click "Build Now"
4. Monitor the build progress in Console Output

### 7.2 Monitor Execution

```bash
# View Console Output
Click on build number → Console Output

# Check system logs
journalctl -u jenkins -f

# Monitor resource usage
top
# Press 'j' to filter Java processes
```

---

## Step 8: Troubleshooting

### Common Issues and Solutions

#### Issue 1: "Browser not found" Error

**Symptom**: 
```
Error: Failed to initialize driver: Browser binary not found
```

**Solution**:
```bash
# Ensure browsers are installed
which chromium-browser
which firefox
which microsoft-edge

# Or install them
sudo apt-get install -y chromium-browser firefox-esr microsoft-edge-stable
```

#### Issue 2: "Permission Denied" in Reports Directory

**Symptom**:
```
Error: Permission denied creating Reports/Screenshots directory
```

**Solution**:
```bash
# Fix permissions
sudo chown -R jenkins:jenkins /path/to/playwright_web
sudo chmod -R 777 Reports
sudo chmod -R 777 logs
```

#### Issue 3: Out of Memory During Parallel Execution

**Symptom**:
```
java.lang.OutOfMemoryError: Java heap space
```

**Solution**:
Update Jenkins agent JVM settings:

```bash
# Linux: Edit jenkins service file
sudo nano /etc/default/jenkins

# Add to JAVA_ARGS:
JAVA_ARGS="-Xmx2g -Xms1g -XX:+UseG1GC"

# Restart Jenkins
sudo systemctl restart jenkins
```

#### Issue 4: Timeout During Browser Launch

**Symptom**:
```
Timeout: PlayWright browser launch exceeded timeout
```

**Solution**:
Increase timeout in `initDriver()` method or use:
```bash
export PLAYWRIGHT_DOWNLOAD_HOST=https://download.nightly.playwright.dev
```

#### Issue 5: Log Files Not Generated

**Symptom**:
```
logs/ directory is empty
```

**Solution**:
Check logback.xml configuration:
```bash
# Verify LOG_DIR path
cat src/main/resources/logback.xml | grep LOG_DIR

# Create directory manually if needed
mkdir -p logs
chmod 777 logs
```

---

## Step 9: Performance Optimization

### 9.1 Optimize Maven Build

Update pom.xml maven-surefire-plugin:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.1.2</version>
    <configuration>
        <suiteXmlFiles>
            <suiteXmlFile>testng.xml</suiteXmlFile>
        </suiteXmlFiles>
        <argLine>-Xmx2g -XX:+UseG1GC</argLine>
        <reuseForks>true</reuseForks>
        <threadCount>3</threadCount>
    </configuration>
</plugin>
```

### 9.2 Jenkins Agent Configuration

In Jenkins → Manage Jenkins → Manage Nodes and Clouds:

```
Node Configuration:
- Name: Playwright-Agent
- Number of executors: 3
- Labels: playwright linux
- Launch method: Launch agents via SSH
- Host: <ubuntu-server-ip>
- Credentials: <jenkins-ssh-key>
```

### 9.3 Disk Space Management

```bash
# Setup automatic cleanup of old builds
# Jenkins → Configure System → Disk Space Monitoring
# Set up Log Rotation:
# - Days to keep builds: 30
# - Max # of builds to keep: 20
```

---

## Step 10: Monitoring and Maintenance

### 10.1 Setup Log Rotation

In Jenkinsfile (already included):
```groovy
options {
    buildDiscarder(logRotator(numToKeepStr: '10'))
}
```

### 10.2 Regular Maintenance Tasks

```bash
# Weekly: Update system packages
sudo apt-get update && sudo apt-get upgrade -y

# Monthly: Update Maven dependencies
cd /path/to/playwright_web
mvn versions:display-updates

# Quarterly: Clean old artifacts
rm -rf target/
mvn clean
```

### 10.3 Monitoring Script

Create `monitor_tests.sh`:

```bash
#!/bin/bash
# Check test status
curl -X GET http://jenkins:8080/api/json

# Archive old reports
find Reports -name "Report_*.html" -mtime +30 -exec gzip {} \;

# Check disk usage
df -h | grep -E "^/dev/"
```

---

## Step 11: Email Notifications Setup

### 11.1 Configure Email in Jenkins

1. Jenkins → Manage Jenkins → Configure System
2. Find "E-mail Notification" section
3. Configure SMTP:
   - SMTP server: `smtp.gmail.com`
   - SMTP port: `587`
   - Credentials: (your Gmail account)
   - Use TLS: ✓

### 11.2 Gmail Settings for Jenkins

1. Enable 2-Step Verification on Gmail
2. Create App Password
3. Use App Password in Jenkins configuration

---

## Step 12: Docker Alternative (Recommended)

For consistency across environments, consider using Docker:

Create `Dockerfile`:

```dockerfile
FROM ubuntu:22.04

RUN apt-get update && apt-get install -y \
    openjdk-17-jdk-headless \
    maven \
    git \
    chromium-browser \
    firefox \
    microsoft-edge-stable \
    libxss1 libgconf-2-4 libx11-6 libx11-xcb1 \
    libxcb1 libxext6 libxrandr2 libnss3 libdrm2 \
    libgbm1 libpango-1.0-0 libpango-gobject-1.0-0 \
    fonts-liberation xdg-utils

WORKDIR /app
COPY . .

CMD ["mvn", "clean", "test", "-DsuiteXmlFile=testng.xml"]
```

---

## Step 13: CI/CD Best Practices

1. **Secrets Management**: Use Jenkins Credentials Store for sensitive data
2. **Artifact Retention**: Keep reports for 30 days minimum
3. **Notifications**: Configure Slack/Email for test results
4. **Version Control**: Always store testng.xml changes in Git
5. **Backup**: Regular backup of Jenkins configuration
6. **Security**: Keep Java, Maven, and browsels updated

---

## Success Indicators

After proper setup, you should see:

✓ All 3 browser tests executing in parallel (~50% faster)
✓ Single Extent HTML report with unique test names per browser
✓ Screenshots attached for failures
✓ Logs generated with thread information
✓ Reports archived in Jenkins
✓ Zero permission errors in Jenkins logs
✓ Consistent execution times across runs

---

## Appendix A: Environment Variables

```bash
# Add to Jenkins agent environment
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export MAVEN_HOME=/usr/share/maven
export PATH=$JAVA_HOME/bin:$MAVEN_HOME/bin:$PATH

# Playwright-specific
export PW_EXPERIMENTAL_GRID_CLIENT=1
export PLAYWRIGHT_DOWNLOAD_HOST=https://download.nightly.playwright.dev
```

---

## Appendix B: Useful Jenkins Groovy Scripts

### Check Java Version on Agents

```groovy
def verifyJavaVersion() {
    node {
        sh '''
            echo "===== Java Version Check ====="
            java -version 2>&1 | grep -E "version|openjdk"
        '''
    }
}
```

### Generate Build Summary

```groovy
def generateBuildSummary() {
    sh '''
        cat > build_summary.txt << EOF
        Build Summary Report
        ====================
        Build Number: ${BUILD_NUMBER}
        Build Status: ${BUILD_RESULT}
        Build Duration: ${BUILD_DURATION}
        Test Reports: ${BUILD_URL}artifact/Reports/Report_*.html
        EOF
        cat build_summary.txt
    '''
}
```

---

## Support & References

- Playwright Documentation: https://playwright.dev/java/
- TestNG Documentation: https://testng.org/
- Extent Reports: https://www.extentreports.com/
- Jenkins Documentation: https://www.jenkins.io/doc/
- Ubuntu Server: https://ubuntu.com/

---

**Last Updated**: June 2026
**Version**: 1.0
**Status**: Production Ready
