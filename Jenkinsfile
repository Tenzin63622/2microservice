pipeline {
    agent any

    environment {
        JAVA_HOME = "D:\\Freshers_Software\\jdk-21.0.11"
        PATH = "${env.JAVA_HOME}\\bin;${env.PATH}"
    }

    stages {

        stage('Checkout Code') {
            steps {
                echo "=== Checking out code ==="
                checkout scm
            }
        }

        stage('Verify Tools') {
            steps {
                bat """
                echo Checking Java...
                java -version

                echo Checking Maven...
                mvn -version
                """
            }
        }

        stage('Build All Microservices') {
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

                    // Eureka first
                    bat "start \"eureka\" java -jar eureka-server/target/*.jar"
                    sleep 15

                    bat "start \"user\" java -jar user-service/target/*.jar"
                    bat "start \"post\" java -jar post-service/target/*.jar"
                    bat "start \"like\" java -jar like-service/target/*.jar"
                    bat "start \"comment\" java -jar comment-service/target/*.jar"
                    bat "start \"follow\" java -jar follow-service/target/*.jar"
                    bat "start \"search\" java -jar search-service/target/*.jar"

                    sleep 10

                    bat "start \"gateway\" java -jar api-gateway/target/*.jar"
                }
            }
        }

        stage('Health Check') {
            steps {
                script {
                    echo "=== Health Check ==="

                    bat """
                    curl http://localhost:8761 || exit 0
                    curl http://localhost:8081/actuator/health || exit 0
                    """
                }
            }
        }

        stage('API Test - Follow Service') {
            steps {
                script {
                    echo "=== Testing Follow API ==="

                    bat """
                    curl http://localhost:8085/api/follows/1/followers
                    """
                }
            }
        }
    }

    post {
        success {
            echo "PIPELINE SUCCESS - ALL MICROSERVICES RUNNING"
        }

        failure {
            echo "PIPELINE FAILED - CHECK JAVA_HOME / BUILD / PORTS"
        }
    }
}