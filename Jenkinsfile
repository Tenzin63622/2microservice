pipeline {
    agent any

    tools {
        jdk 'JDK21'
    }

    stages {

        stage('Checkout') {
            steps {
                echo "=== Checkout Code ==="
                checkout scm
            }
        }

        stage('Build All Services') {
            steps {
                echo "=== Building Microservices ==="

                script {
                    def services = [
                        "eureka-server",
                        "user-service",
                        "post-service",
                        "like-service",
                        "comment-service",
                        "follow-service",
                        "search-service",
                        "api-gateway"
                    ]

                    for (service in services) {
                        dir(service) {
                            bat "mvn clean package -DskipTests"
                        }
                    }
                }
            }
        }

        /*
        OPTIONAL: START SERVICES (NOT RECOMMENDED IN REAL CI/CD)
        */

        stage('Start Services') {
            steps {
                bat """
                    echo Starting Eureka Server...
                    start java -jar eureka-server\\target\\*.jar

                    echo Starting User Service...
                    start java -jar user-service\\target\\*.jar

                    echo Starting Post Service...
                    start java -jar post-service\\target\\*.jar

                    echo Starting Like Service...
                    start java -jar like-service\\target\\*.jar

                    echo Starting Comment Service...
                    start java -jar comment-service\\target\\*.jar

                    echo Starting Follow Service...
                    start java -jar follow-service\\target\\*.jar

                    echo Starting Search Service...
                    start java -jar search-service\\target\\*.jar

                    echo Starting API Gateway...
                    start java -jar api-gateway\\target\\*.jar
                """

                // FIXED: Windows safe sleep (no timeout /t)
                powershell "Start-Sleep -Seconds 25"
            }
        }

        stage('Health Check') {
            steps {
                // safer retry-based health check
                powershell """
                    for ($i = 0; $i -lt 10; $i++) {
                        try {
                            Invoke-WebRequest http://localhost:8761 -UseBasicParsing
                            Write-Host "Eureka is UP"
                            exit 0
                        } catch {
                            Write-Host "Waiting for Eureka..."
                            Start-Sleep -Seconds 5
                        }
                    }
                    exit 1
                """
            }
        }
    }

    post {
        success {
            echo "PIPELINE SUCCESS"
        }

        failure {
            echo "PIPELINE FAILED - CHECK LOGS / JAVA_HOME / SERVICES"
        }
    }
}