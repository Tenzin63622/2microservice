pipeline {
    agent any

    environment {
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
                dir('follow-service') {
                    echo "Starting follow-service..."

                    bat '''
                    if exist follow.log del follow.log
                    start /b java -jar target\\follow-service-0.0.1-SNAPSHOT.jar > follow.log 2>&1
                    '''

                    echo "Waiting for follow-service to be ready..."
                }
            }
        }

        stage('Wait for Follow Service') {
            steps {
                powershell '''
                $url = "http://localhost:8085/api/follows/1/followers"
                $maxRetries = 30
                $count = 0
                $success = $false

                while ($count -lt $maxRetries) {
                    Start-Sleep -Seconds 5

                    try {
                        Write-Host "Checking service... attempt $count"
                        $response = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 5

                        Write-Host "✅ Follow service is UP!"
                        Write-Host $response.Content

                        $success = $true
                        break
                    }
                    catch {
                        Write-Host "❌ Not ready yet..."
                    }

                    $count++
                }

                if (-not $success) {
                    throw "Follow Service failed to start"
                }
                '''
            }
        }

        stage('Test Follow API') {
            steps {
                echo "Testing endpoint: ${env.FOLLOW_URL}"

                powershell '''
                try {
                    $response = Invoke-WebRequest -Uri $env:FOLLOW_URL -UseBasicParsing
                    Write-Host "API RESPONSE:"
                    Write-Host $response.Content
                }
                catch {
                    throw "API Test Failed: Service not reachable"
                }
                '''
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline SUCCESS - All services built and Follow API works"
        }

        failure {
            echo "❌ Pipeline FAILED - Check logs"
        }
    }
}