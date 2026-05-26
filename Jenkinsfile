pipeline {
    agent any

    stages {

        stage('Checkout Code') {
            steps {
                checkout scm
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
                            echo "Building ${service}"
                            bat "mvn clean package -DskipTests"
                        }
                    }
                }
            }
        }

        stage('Run Follow Service + API Test') {
            steps {
                script {

                    dir("follow-service") {

                        echo "Starting follow-service in background..."

                        // Start Spring Boot in background
                        bat """
                        start "follow-service" /B java -jar target\\follow-service-0.0.1-SNAPSHOT.jar > follow.log
                        """

                        echo "Waiting for service to start..."
                        bat "timeout 20"

                        echo "Hitting API endpoint..."

                        // CALL YOUR API
                        bat """
                        curl http://localhost:8085/api/follows/1/followers
                        """

                        echo "Stopping follow-service..."

                        // Kill process using port 8085
                        bat """
                        for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8085') do taskkill /F /PID %%a
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline SUCCESS - API tested successfully"
        }
        failure {
            echo "❌ Pipeline FAILED"
        }
        always {
            echo "Pipeline finished"
        }
    }
}