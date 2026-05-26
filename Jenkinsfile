pipeline {
    agent any

    stages {

        stage('Checkout Code') {
            steps {
                echo "=== Checkout Code ==="
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

                    services.each { service ->
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
                    echo Starting Eureka Server...
                    start java -jar eureka-server/target/*.jar

                    timeout /t 10

                    echo Starting User Service...
                    start java -jar user-service/target/*.jar

                    echo Starting Post Service...
                    start java -jar post-service/target/*.jar

                    echo Starting Like Service...
                    start java -jar like-service/target/*.jar

                    echo Starting Comment Service...
                    start java -jar comment-service/target/*.jar

                    echo Starting Follow Service...
                    start java -jar follow-service/target/*.jar

                    echo Starting Search Service...
                    start java -jar search-service/target/*.jar

                    echo Starting API Gateway...
                    start java -jar api-gateway/target/*.jar
                '''
            }
        }

        stage('Health Check + API Response') {
            steps {
                bat '''
                    echo ===============================
                    echo WAITING FOR SERVICES TO START
                    echo ===============================

                    timeout /t 20

                    echo ===============================
                    echo CHECKING FOLLOW SERVICE HEALTH
                    echo ===============================

                    :retry
                    curl -s http://localhost:8085/actuator/health
                    IF %ERRORLEVEL% NEQ 0 (
                        echo Service not ready... retrying in 5 seconds
                        timeout /t 5
                        goto retry
                    )

                    echo ===============================
                    echo SERVICE IS UP
                    echo ===============================

                    echo ===============================
                    echo CALLING FOLLOWERS API
                    echo ===============================

                    curl http://localhost:8085/api/follows/1/followers

                    echo ===============================
                    echo PIPELINE SUCCESS
                    echo ===============================
                '''
            }
        }
    }

    post {
        success {
            echo "PIPELINE SUCCESS - ALL SERVICES RUNNING"
        }

        failure {
            echo "PIPELINE FAILED - CHECK LOGS / JAVA_HOME / SERVICE STARTUP"
        }
    }
}