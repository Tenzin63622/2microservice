// pipeline {
//     agent any

//     environment {
//         DOCKER_HUB_CREDENTIALS = credentials('docker-hub-credentials')
//         DOCKER_HUB_REPO = 'yourdockerhubusername'
//         SERVICES = 'eureka-server user-service post-service like-service comment-service follow-service search-service notification-service api-gateway'
//     }

//     stages {
//         stage('Checkout') {
//             steps {
//                 echo '=== Checking out source code ==='
//                 checkout scm
//             }
//         }

//         stage('Build All Services') {
//             steps {
//                 echo '=== Building all microservices with Maven ==='
//                 script {
//                     def serviceList = env.SERVICES.split(' ')
//                     for (service in serviceList) {
//                         echo "Building ${service}..."
//                         dir(service) {
//                             sh 'mvn clean package -DskipTests'
//                         }
//                     }
//                 }
//             }
//         }

//         stage('Run Tests') {
//             steps {
//                 echo '=== Running unit tests ==='
//                 script {
//                     def serviceList = env.SERVICES.split(' ')
//                     for (service in serviceList) {
//                         dir(service) {
//                             sh 'mvn test || true'
//                         }
//                     }
//                 }
//             }
//             post {
//                 always {
//                     junit '**/target/surefire-reports/*.xml'
//                 }
//             }
//         }

//         stage('Docker Build & Push') {
//             steps {
//                 echo '=== Building Docker images and pushing to DockerHub ==='
//                 script {
//                     sh "echo ${DOCKER_HUB_CREDENTIALS_PSW} | docker login -u ${DOCKER_HUB_CREDENTIALS_USR} --password-stdin"
//                     def serviceList = env.SERVICES.split(' ')
//                     for (service in serviceList) {
//                         dir(service) {
//                             def imageName = "${DOCKER_HUB_REPO}/insta-${service}:${BUILD_NUMBER}"
//                             def latestImage = "${DOCKER_HUB_REPO}/insta-${service}:latest"
//                             sh "docker build -t ${imageName} -t ${latestImage} ."
//                             sh "docker push ${imageName}"
//                             sh "docker push ${latestImage}"
//                         }
//                     }
//                 }
//             }
//         }

//         stage('Deploy with Docker Compose') {
//             steps {
//                 echo '=== Deploying all services with Docker Compose ==='
//                 sh '''
//                     docker-compose down --remove-orphans || true
//                     docker-compose pull
//                     docker-compose up -d
//                     echo "Waiting for services to start..."
//                     sleep 60
//                     docker-compose ps
//                 '''
//             }
//         }

//         stage('Health Check') {
//             steps {
//                 echo '=== Running health checks ==='
//                 sh '''
//                     echo "Checking Eureka Server..."
//                     curl -f http://localhost:8761/actuator/health || echo "Eureka not ready yet"
//                     echo "Checking API Gateway..."
//                     curl -f http://localhost:8080/actuator/health || echo "Gateway not ready yet"
//                     docker-compose ps
//                 '''
//             }
//         }
//     }

//     post {
//         success {
//             echo '=== Pipeline completed successfully! ==='
//             echo 'Application is running at http://localhost:8080'
//             echo 'Eureka Dashboard: http://localhost:8761'
//         }
//         failure {
//             echo '=== Pipeline failed! Checking logs... ==='
//             sh 'docker-compose logs --tail=50 || true'
//         }
//         always {
//             sh 'docker logout || true'
//         }
//     }
// }
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
                        dir(service) {
                            bat 'mvn clean package -DskipTests'
                        }
                    }
                }
            }
        }

        stage('Stop Only Required Ports') {
            steps {
                echo '=== Stopping services on known ports only ==='
                bat '''
                    for /f "tokens=5" %a in ('netstat -ano ^| findstr :8761') do taskkill /F /PID %a || exit /B 0
                    for /f "tokens=5" %a in ('netstat -ano ^| findstr :8081') do taskkill /F /PID %a || exit /B 0
                    for /f "tokens=5" %a in ('netstat -ano ^| findstr :8082') do taskkill /F /PID %a || exit /B 0
                    for /f "tokens=5" %a in ('netstat -ano ^| findstr :8083') do taskkill /F /PID %a || exit /B 0
                    for /f "tokens=5" %a in ('netstat -ano ^| findstr :8084') do taskkill /F /PID %a || exit /B 0
                    for /f "tokens=5" %a in ('netstat -ano ^| findstr :8085') do taskkill /F /PID %a || exit /B 0
                    for /f "tokens=5" %a in ('netstat -ano ^| findstr :8086') do taskkill /F /PID %a || exit /B 0
                    for /f "tokens=5" %a in ('netstat -ano ^| findstr :9090') do taskkill /F /PID %a || exit /B 0
                '''
            }
        }

        stage('Deploy Services') {
            steps {
                echo '=== Starting Microservices ==='

                bat '''
                    echo Starting Eureka Server...
                    start /B java -jar eureka-server\\target\\*.jar
                    timeout /t 20

                    echo Starting core services...
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

        stage('Wait for Eureka + Follow Service') {
            steps {
                echo '=== Waiting for services to be READY ==='

                bat '''
                set /A COUNT=0

                :check
                curl -f http://localhost:8761/actuator/health >nul 2>&1
                if %errorlevel%==0 (
                    echo Eureka is UP
                ) else (
                    echo Waiting for Eureka...
                    timeout /t 5 >nul
                    goto check
                )

                set /A COUNT=0

                :checkFollow
                curl -f http://localhost:8085/actuator/health >nul 2>&1
                if %errorlevel%==0 (
                    echo Follow-Service is UP
                    goto done
                )

                set /A COUNT+=1
                if %COUNT% GEQ 20 (
                    echo Follow-Service failed to start
                    exit /B 1
                )

                echo Waiting for Follow-Service...
                timeout /t 5 >nul
                goto checkFollow

                :done
                echo All required services are READY
                '''
            }
        }

        stage('Test Follow-Service API') {
            steps {
                echo '=== Testing Follow Service API ==='

                bat '''
                    echo Calling follow-service GET API...

                    curl -X GET http://localhost:8085/api/follows/1/following ^
                    -H "X-User-Id: 1"

                    echo API Test Completed Successfully
                '''
            }
        }
    }

    post {

        success {
            echo '====================================='
            echo 'DEPLOYMENT SUCCESSFUL'
            echo '====================================='

            echo 'Eureka: http://localhost:8761'
            echo 'API Gateway: http://localhost:9090'
            echo 'Follow API: http://localhost:8085/api/follows/1/following'
        }

        failure {
            echo '====================================='
            echo 'DEPLOYMENT FAILED'
            echo '====================================='
        }
    }
}