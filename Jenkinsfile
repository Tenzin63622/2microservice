pipeline {
    agent any

    tools {
        jdk 'jdk-21'
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
                bat """
                    echo Starting Eureka Server...
                    start /B java -jar eureka-server\\target\\*.jar

                    echo Starting User Service...
                    start /B java -jar user-service\\target\\*.jar

                    echo Starting Post Service...
                    start /B java -jar post-service\\target\\*.jar

                    echo Starting Like Service...
                    start /B java -jar like-service\\target\\*.jar

                    echo Starting Comment Service...
                    start /B java -jar comment-service\\target\\*.jar

                    echo Starting Follow Service...
                    start /B java -jar follow-service\\target\\*.jar

                    echo Starting Search Service...
                    start /B java -jar search-service\\target\\*.jar

                    echo Starting API Gateway...
                    start /B java -jar api-gateway\\target\\*.jar
                """

                powershell "Start-Sleep -Seconds 25"
            }
        }

        stage('Health Check') {
            steps {
                bat "curl http://localhost:8761 || echo Eureka not ready"
            }
        }
    }

    post {
        success {
            echo "PIPELINE SUCCESS"
        }

        failure {
            echo "PIPELINE FAILED - CHECK JAVA_HOME OR BUILD LOGS"
        }
    }
}