pipeline {

    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK17'
    }

    environment {
        FOLLOW_URL = "http://localhost:8085/api/follows/1/followers"
        FOLLOW_PORT = "8085"
    }

    stages {

        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Stop Existing Follow Service') {
            steps {
                echo 'Stopping old follow-service if running...'

                bat '''
                @echo off

                FOR /F "tokens=5" %%A IN ('netstat -aon ^| findstr :8085') DO (
                    echo Killing PID %%A on port 8085
                    taskkill /F /PID %%A 2>NUL
                )

                exit /b 0
                '''
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

                            bat 'mvn clean package -DskipTests'
                        }
                    }
                }
            }
        }

        stage('Run Follow Service') {

            steps {

                dir('follow-service') {

                    echo 'Starting follow-service in background...'

                    withEnv(['JENKINS_NODE_COOKIE=dontKillMe']) {

                        bat '''
                        @echo off

                        if exist follow.log del follow.log

                        FOR /F "delims=" %%I IN ('dir /b target\\*.jar ^| findstr /v "original"') DO (
                            start /B java -jar "target\\%%I" > follow.log 2>&1
                        )
                        '''
                    }
                }
            }
        }

        stage('Wait for Follow Service') {

            steps {

                powershell '''

                $url = "http://localhost:8085/actuator/health"

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

                        Write-Host "❌ Service not ready yet..."
                    }

                    $count++
                }

                if (-not $success) {

                    throw "Follow Service failed to start"
                }

                '''
            }
        }

        stage('Test Prometheus Endpoint') {

            steps {

                powershell '''

                try {

                    $response = Invoke-WebRequest -Uri "http://localhost:8085/actuator/prometheus" -UseBasicParsing

                    Write-Host "✅ Prometheus endpoint working"

                }
                catch {

                    throw "❌ Prometheus endpoint failed"
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

                    throw "API Test Failed"
                }

                '''
            }
        }

        stage('Open Browser') {

            steps {

                bat '''
                start "" http://localhost:8085/api/follows/1/followers
                '''
            }
        }
    }

    post {

        success {

            echo '✅ Pipeline SUCCESS'
            echo '✅ follow-service is STILL RUNNING'
            echo '✅ Prometheus can now scrape metrics'
            echo '✅ No need to run service manually from VS Code'
        }

        failure {

            echo '❌ Pipeline FAILED'
        }
    }
}