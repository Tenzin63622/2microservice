pipeline {
    agent any

    tools {
        jdk 'jdk-17'
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
                    for /f %%i in ('dir /b eureka-server\\target\\*.jar') do start java -jar eureka-server\\target\\%%i

                    timeout /t 20 >nul

                    echo Starting User Service...
                    for /f %%i in ('dir /b user-service\\target\\*.jar') do start java -jar user-service\\target\\%%i

                    echo Starting Post Service...
                    for /f %%i in ('dir /b post-service\\target\\*.jar') do start java -jar post-service\\target\\%%i

                    echo Starting Like Service...
                    for /f %%i in ('dir /b like-service\\target\\*.jar') do start java -jar like-service\\target\\%%i

                    echo Starting Comment Service...
                    for /f %%i in ('dir /b comment-service\\target\\*.jar') do start java -jar comment-service\\target\\%%i

                    echo Starting Follow Service...
                    for /f %%i in ('dir /b follow-service\\target\\*.jar') do start java -jar follow-service\\target\\%%i

                    echo Starting Search Service...
                    for /f %%i in ('dir /b search-service\\target\\*.jar') do start java -jar search-service\\target\\%%i

                    timeout /t 15 >nul

                    echo Starting API Gateway...
                    for /f %%i in ('dir /b api-gateway\\target\\*.jar') do start java -jar api-gateway\\target\\%%i
                """
            }
        }

        stage('Health Check') {
            steps {
                bat """
                    echo Checking Eureka...
                    curl http://localhost:8761 || echo Eureka not ready
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
            echo "CHECK JAVA / MAVEN / STARTUP LOGS"
            echo "====================================="
        }
    }
}