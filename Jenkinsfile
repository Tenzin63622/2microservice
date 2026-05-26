pipeline {
    agent any

    environment {
        JAVA_HOME = "C:\\Program Files\\Java\\jdk-17"
        PATH = "${env.JAVA_HOME}\\bin;${env.PATH}"
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

        stage('Start Services') {
            steps {
                echo "=== Starting Services ==="

                bat """
                    echo Starting Eureka Server...
                    start /B java -jar eureka-server\\target\\eureka-server-*.jar

                    echo Starting User Service...
                    start /B java -jar user-service\\target\\user-service-*.jar

                    echo Starting Post Service...
                    start /B java -jar post-service\\target\\post-service-*.jar

                    echo Starting Like Service...
                    start /B java -jar like-service\\target\\like-service-*.jar

                    echo Starting Comment Service...
                    start /B java -jar comment-service\\target\\comment-service-*.jar

                    echo Starting Follow Service...
                    start /B java -jar follow-service\\target\\follow-service-*.jar

                    echo Starting Search Service...
                    start /B java -jar search-service\\target\\search-service-*.jar

                    echo Starting API Gateway...
                    start /B java -jar api-gateway\\target\\api-gateway-*.jar
                """

                // FIX: Replace timeout with PowerShell (Windows safe)
                powershell """
                    Start-Sleep -Seconds 25
                """
            }
        }

        stage('Health Check') {
            steps {
                echo "=== Basic Health Check ==="
                bat """
                    curl http://localhost:8761 || echo Eureka not ready yet
                """
            }
        }
    }

    post {
        success {
            echo "====================================="
            echo "PIPELINE SUCCESS"
            echo "====================================="
        }

        failure {
            echo "====================================="
            echo "PIPELINE FAILED"
            echo "CHECK LOGS"
            echo "====================================="
        }
    }
}