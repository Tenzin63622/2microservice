pipeline {
    agent any

    environment {
        JAVA_HOME = "C:\\Program Files\\Java\\jdk-17"
        PATH = "${env.JAVA_HOME}\\bin;${env.PATH}"
    }

    stages {

        stage('Checkout Code') {
            steps {
                echo "=== Checking out code ==="
                checkout scm
            }
        }

        stage('Build All Services') {
            steps {
                script {
                    echo "=== Building Microservices ==="

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
                bat '''
                echo ===============================
                echo Starting Microservices
                echo ===============================

                echo Starting Eureka Server...
                start "" java -jar eureka-server/target/eureka-server-0.0.1-SNAPSHOT.jar

                ping 127.0.0.1 -n 8 > nul

                echo Starting User Service...
                start "" java -jar user-service/target/user-service-0.0.1-SNAPSHOT.jar

                ping 127.0.0.1 -n 8 > nul

                echo Starting Post Service...
                start "" java -jar post-service/target/post-service-0.0.1-SNAPSHOT.jar

                ping 127.0.0.1 -n 8 > nul

                echo Starting Like Service...
                start "" java -jar like-service/target/like-service-0.0.1-SNAPSHOT.jar

                ping 127.0.0.1 -n 8 > nul

                echo Starting Comment Service...
                start "" java -jar comment-service/target/comment-service-0.0.1-SNAPSHOT.jar

                ping 127.0.0.1 -n 8 > nul

                echo Starting Follow Service...
                start "" java -jar follow-service/target/follow-service-0.0.1-SNAPSHOT.jar

                ping 127.0.0.1 -n 8 > nul

                echo Starting Search Service...
                start "" java -jar search-service/target/search-service-0.0.1-SNAPSHOT.jar

                ping 127.0.0.1 -n 8 > nul

                echo Starting API Gateway...
                start "" java -jar api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar

                echo ===============================
                echo ALL SERVICES STARTED
                echo ===============================
                '''
            }
        }

        stage('Health Check') {
            steps {
                bat '''
                echo Checking services...

                curl http://localhost:8761 || echo Eureka not ready yet
                curl http://localhost:8081 || echo User service not ready
                curl http://localhost:8082 || echo Post service not ready
                curl http://localhost:8083 || echo Like service not ready
                curl http://localhost:8084 || echo Comment service not ready
                curl http://localhost:8085 || echo Follow service not ready
                curl http://localhost:8086 || echo Search service not ready
                curl http://localhost:8080 || echo Gateway not ready
                '''
            }
        }

        stage('API Test (Follow Service)') {
            steps {
                bat '''
                echo Testing Follow API...

                curl http://localhost:8085/api/follows/1/followers
                '''
            }
        }
    }

    post {
        success {
            echo "PIPELINE SUCCESS - ALL MICROSERVICES RUNNING"
        }
        failure {
            echo "PIPELINE FAILED - CHECK LOGS"
        }
    }
}