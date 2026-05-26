pipeline {
    agent any

    tools {
        jdk 'JDK21'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build All Services') {
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
                            bat "mvn clean package -DskipTests"
                        }
                    }
                }
            }
        }

        stage('Start Services') {
            steps {
                bat """
                    for /f %%i in ('dir /b eureka-server\\target\\*.jar') do start java -jar eureka-server\\target\\%%i
                    timeout /t 20 >nul

                    for /f %%i in ('dir /b user-service\\target\\*.jar') do start java -jar user-service\\target\\%%i
                    for /f %%i in ('dir /b post-service\\target\\*.jar') do start java -jar post-service\\target\\%%i
                    for /f %%i in ('dir /b like-service\\target\\*.jar') do start java -jar like-service\\target\\%%i
                    for /f %%i in ('dir /b comment-service\\target\\*.jar') do start java -jar comment-service\\target\\%%i
                    for /f %%i in ('dir /b follow-service\\target\\*.jar') do start java -jar follow-service\\target\\%%i
                    for /f %%i in ('dir /b search-service\\target\\*.jar') do start java -jar search-service\\target\\%%i

                    timeout /t 15 >nul

                    for /f %%i in ('dir /b api-gateway\\target\\*.jar') do start java -jar api-gateway\\target\\%%i
                """
            }
        }

        stage('Health Check') {
            steps {
                bat "curl http://localhost:8761 || echo Eureka not ready"
            }
        }
    }
}