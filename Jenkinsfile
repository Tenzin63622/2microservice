pipeline {
    agent any

    environment {
        FOLLOW_PORT = "8085"
        FOLLOW_URL = "http://localhost:8085/api/follows/1/followers"
    }

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

        stage('Run Follow Service') {
            steps {
                script {
                    dir("follow-service") {

                        echo "Starting follow-service..."

                        bat """
                        if exist follow.log del follow.log
                        start "follow-service" java -jar target\\follow-service-0.0.1-SNAPSHOT.jar > follow.log
                        """

                        echo "Waiting for follow-service to start..."

                        powershell """
                        Start-Sleep -Seconds 25
                        """
                    }
                }
            }
        }

        stage('Test Follow API') {
            steps {
                script {
                    echo "Testing endpoint: ${FOLLOW_URL}"

                    powershell """
                    try {
                        \$response = Invoke-RestMethod -Uri "${FOLLOW_URL}" -Method GET
                        Write-Output "API RESPONSE:"
                        Write-Output \$response
                    }
                    catch {
                        Write-Output "❌ API TEST FAILED"
                        Write-Output \$_
                        exit 1
                    }
                    """
                }
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline SUCCESS - All services built and follow API works"
        }

        failure {
            echo "❌ Pipeline FAILED - Check logs"
        }
    }
}