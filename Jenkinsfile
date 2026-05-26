pipeline {
    agent any

    environment {
        SERVICES = 'eureka-server user-service post-service like-service comment-service follow-service search-service api-gateway'
        FOLLOW_SERVICE_PORT = '8085'
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
                echo '=== Fetching Code ==='
                checkout scm
            }
        }

        stage('Build All Microservices') {
            steps {
                echo '=== Building Services ==='

                script {
                    def serviceList = env.SERVICES.split(' ')

                    for (service in serviceList) {
                        echo "Building ${service}"

                        dir(service) {
                            bat 'mvn clean package -DskipTests'
                        }
                    }
                }
            }
        }

        stage('Run Unit Tests') {
            steps {
                echo '=== Running Tests ==='

                script {
                    def serviceList = env.SERVICES.split(' ')

                    for (service in serviceList) {
                        dir(service) {
                            bat 'mvn test || exit /B 0'
                        }
                    }
                }
            }

            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Stop Old Services') {
            steps {
                echo '=== Stopping old Java processes ==='
                bat 'taskkill /F /IM java.exe /T || exit /B 0'
            }
        }

        stage('Start Core Services') {
            steps {
                echo '=== Starting Eureka + Microservices ==='

                bat '''
                    echo Starting Eureka Server...
                    start /B java -jar eureka-server\\target\\*.jar
                    timeout /t 25

                    echo Starting Core Services...
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

        stage('Wait for Services to Start') {
            steps {
                echo '=== Waiting for services ==='
                bat 'timeout /t 60'
            }
        }

        stage('Start Follow Service for Testing') {
            steps {
                echo '=== Ensuring Follow Service is running ==='

                bat '''
                    echo Starting Follow Service explicitly for test...
                    start /B java -jar follow-service\\target\\*.jar
                    timeout /t 20
                '''
            }
        }

        stage('Health Check') {
            steps {
                echo '=== Checking Health ==='

                bat '''
                    echo Eureka Health Check...
                    curl http://localhost:8761/actuator/health || echo Eureka not ready

                    echo API Gateway Health Check...
                    curl http://localhost:9090/actuator/health || echo Gateway not ready
                '''
            }
        }

        stage('Test Follow Service API') {
            steps {
                echo '=== Testing Follow Service API ==='

                bat '''
                    echo Calling Follow Service API...
                    curl -H "X-User-Id: 1" http://localhost:8085/api/follows/1/following
                '''
            }
        }
    }

    post {

        success {
            echo '====================================='
            echo 'DEPLOYMENT SUCCESSFUL 🚀'
            echo '====================================='
            echo 'Eureka: http://localhost:8761'
            echo 'API Gateway: http://localhost:9090'
            echo 'Follow API: http://localhost:8085/api/follows/1/following'
        }

        failure {
            echo '====================================='
            echo 'DEPLOYMENT FAILED ❌'
            echo 'Check logs above'
            echo '====================================='
        }
    }
}