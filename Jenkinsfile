pipeline {
    agent any

    environment {
        SERVICES = 'eureka-server user-service post-service like-service comment-service follow-service search-service api-gateway'
        FOLLOW_URL = 'http://localhost:8085/api/follows/1/following'
    }

    stages {

        stage('Clean Workspace') {
            steps {
                echo '=== Cleaning Workspace ==='
                cleanWs()
            }
        }

        stage('Checkout Code') {
            steps {
                echo '=== Checkout Code ==='
                checkout scm
            }
        }

        stage('Build All Services') {
            steps {
                echo '=== Building Microservices ==='
                script {
                    def serviceList = env.SERVICES.split(' ')
                    for (service in serviceList) {
                        dir(service) {
                            bat 'mvn clean package -DskipTests'
                        }
                    }
                }
            }
        }

        stage('Start Services') {
            steps {
                echo '=== Starting Services ==='

                bat '''
                    echo Starting Eureka Server...
                    start /B java -jar eureka-server\\target\\*.jar
                    timeout /t 25

                    echo Starting Microservices...
                    start /B java -jar user-service\\target\\*.jar
                    start /B java -jar post-service\\target\\*.jar
                    start /B java -jar like-service\\target\\*.jar
                    start /B java -jar comment-service\\target\\*.jar
                    start /B java -jar follow-service\\target\\*.jar
                    start /B java -jar search-service\\target\\*.jar

                    timeout /t 20

                    echo Starting API Gateway...
                    start /B java -jar api-gateway\\target\\*.jar
                '''
            }
        }

        stage('Wait for Startup') {
            steps {
                echo '=== Waiting for services to register in Eureka ==='
                bat 'timeout /t 60'
            }
        }

        stage('Test Follow API via Gateway') {
            steps {
                echo '=== Testing API ==='

                bat '''
                    echo Calling Follow API...

                    curl %FOLLOW_URL%

                    if %errorlevel% neq 0 (
                        echo API CALL FAILED
                        exit /b 1
                    )
                '''
            }
        }

        stage('Stop Services') {
            steps {
                echo '=== Stopping Java Services ==='
                bat 'taskkill /F /IM java.exe /T || exit /B 0'
            }
        }
    }

    post {
        success {
            echo '====================================='
            echo 'PIPELINE SUCCESS'
            echo "API WORKED: http://localhost:8085/api/follows/1/following"
            echo '====================================='
        }

        failure {
            echo '====================================='
            echo 'PIPELINE FAILED'
            echo 'CHECK LOGS'
            echo '====================================='
        }
    }
}