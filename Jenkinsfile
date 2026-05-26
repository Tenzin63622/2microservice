pipeline {
    agent any

    environment {
        GATEWAY_URL = "http://localhost:8082/api/follows/1/following"
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

                bat '''
                echo Starting Eureka Server...
                for /f %%i in ('dir /b eureka-server\\target\\*.jar') do start /B java -jar eureka-server\\target\\%%i

                timeout /t 25

                echo Starting Microservices...
                for /f %%i in ('dir /b user-service\\target\\*.jar') do start /B java -jar user-service\\target\\%%i
                for /f %%i in ('dir /b post-service\\target\\*.jar') do start /B java -jar post-service\\target\\%%i
                for /f %%i in ('dir /b like-service\\target\\*.jar') do start /B java -jar like-service\\target\\%%i
                for /f %%i in ('dir /b comment-service\\target\\*.jar') do start /B java -jar comment-service\\target\\%%i
                for /f %%i in ('dir /b follow-service\\target\\*.jar') do start /B java -jar follow-service\\target\\%%i
                for /f %%i in ('dir /b search-service\\target\\*.jar') do start /B java -jar search-service\\target\\%%i

                timeout /t 20

                echo Starting API Gateway...
                for /f %%i in ('dir /b api-gateway\\target\\*.jar') do start /B java -jar api-gateway\\target\\%%i
                '''
            }
        }

        stage('Wait for Startup') {
            steps {
                echo "Waiting for services to stabilize..."
                bat "timeout /t 30"
            }
        }

        stage('Test Follow API') {
            steps {
                echo "=== Testing API ==="
                bat """
                curl -i ${GATEWAY_URL}
                """
            }
        }
    }

    post {
        success {
            echo "====================================="
            echo "PIPELINE SUCCESS"
            echo "FOLLOW API TEST COMPLETED"
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