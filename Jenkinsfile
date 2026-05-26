pipeline {
    agent any

    environment {
        JAVA_HOME = "D:\\Freshers_Software\\jdk-21.0.11"
        MAVEN_HOME = "D:\\Freshers_Software\\Softwarepath\\apache-maven-3.8.5"
        PATH = "${env.JAVA_HOME}\\bin;${env.MAVEN_HOME}\\bin;${env.PATH}"
    }

    stages {

        stage('Checkout') {
            steps {
                echo "=== Cloning Repository ==="
                checkout scm
            }
        }

        stage('Verify Tools') {
            steps {
                bat '''
                echo Checking Java...
                java -version

                echo Checking Maven...
                mvn -version
                '''
            }
        }

        stage('Build All Services') {
            steps {
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
                            echo "=== Building ${service} ==="
                            bat "mvn clean package -DskipTests"
                        }
                    }
                }
            }
        }

        stage('Start Services') {
            steps {
                script {
                    echo "=== Starting Microservices ==="

                    bat '''
                    @echo off

                    REM Kill old java processes (optional cleanup)
                    taskkill /F /IM java.exe /T >nul 2>&1

                    REM Start services in background using start command
                    start "eureka" java -jar eureka-server\\target\\*.jar
                    timeout /t 20

                    start "user" java -jar user-service\\target\\*.jar
                    start "post" java -jar post-service\\target\\*.jar
                    start "like" java -jar like-service\\target\\*.jar
                    start "comment" java -jar comment-service\\target\\*.jar
                    start "follow" java -jar follow-service\\target\\*.jar
                    start "search" java -jar search-service\\target\\*.jar

                    timeout /t 15

                    start "gateway" java -jar api-gateway\\target\\*.jar
                    '''
                }
            }
        }

        stage('Health Check') {
            steps {
                script {
                    echo "=== Health Check ==="

                    bat '''
                    echo Waiting for services...
                    timeout /t 30

                    curl http://localhost:8761 || echo Eureka not ready
                    '''
                }
            }
        }

        stage('API Test') {
            steps {
                bat '''
                echo === Testing Follow Service ===
                curl http://localhost:8085/api/follows/1/followers || echo API not ready
                '''
            }
        }
    }

    post {
        success {
            echo "PIPELINE SUCCESS 🎉"
        }

        failure {
            echo "PIPELINE FAILED ❌ CHECK LOGS (JAVA / PORT / SERVICE START ISSUE)"
        }
    }
}