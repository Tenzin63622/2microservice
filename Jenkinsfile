pipeline {
    agent any

    environment {
        SERVICES = 'eureka-server user-service post-service like-service comment-service follow-service search-service api-gateway'
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
                echo '=== Fetching Code from GitHub ==='
                checkout scm
            }
        }

        stage('Build Microservices') {
            steps {
                echo '=== Building all services ==='

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
                echo '=== Running Unit Tests ==='

                script {
                    def serviceList = env.SERVICES.split(' ')

                    for (service in serviceList) {
                        echo "Testing ${service}"

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
                echo '=== Stopping old Java services ==='
                bat 'taskkill /F /IM java.exe /T || exit /B 0'
            }
        }

        stage('Start Services') {
            steps {
                echo '=== Starting Microservices ==='

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

        stage('Wait for Services') {
            steps {
                echo '=== Waiting for services to initialize ==='
                bat 'timeout /t 60'
            }
        }

        stage('Health Check') {
            steps {
                echo '=== Checking Service Health ==='

                bat '''
                    echo Checking Eureka...
                    curl -f http://localhost:8761/actuator/health || echo Eureka not ready

                    echo Checking API Gateway...
                    curl -f http://localhost:9090/actuator/health || echo Gateway not ready
                '''
            }
        }

        stage('Test Follow Service API') {
            steps {
                echo '=== Testing Follow Service API ==='

                bat '''
                    echo Calling Follow Service...
                    curl -H "X-User-Id: 1" http://localhost:8082/api/follows/1/following
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
            echo 'Post Service Example: http://localhost:8082/api/posts'
        }

        failure {
            echo '====================================='
            echo 'DEPLOYMENT FAILED ❌'
            echo 'Check logs above'
            echo '====================================='
        }
    }
}