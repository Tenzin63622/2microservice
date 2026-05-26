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
        SERVICES = 'eureka-server user-service post-service like-service comment-service follow-service search-service notification-service api-gateway'
    }

    stages {

        stage('Checkout Code') {
            steps {
                echo '=== Fetching latest code from GitHub ==='
                checkout scm
            }
        }

        stage('Build Microservices') {
            steps {

                echo '=== Building all Spring Boot microservices ==='

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

                        dir(service) {

                            bat 'mvn test'
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

                echo '=== Stopping old running services ==='

                bat '''
                    taskkill /F /IM java.exe /T || exit /B 0
                '''
            }
        }

        stage('Deploy Services') {
            steps {

                echo '=== Starting all microservices ==='

                bat '''
                    start /B java -jar eureka-server\\target\\*.jar
                    timeout /t 20

                    start /B java -jar user-service\\target\\*.jar
                    start /B java -jar post-service\\target\\*.jar
                    start /B java -jar like-service\\target\\*.jar
                    start /B java -jar comment-service\\target\\*.jar
                    start /B java -jar follow-service\\target\\*.jar
                    start /B java -jar search-service\\target\\*.jar
                    start /B java -jar api-gateway\\target\\*.jar
                '''
            }
        }

        stage('Health Check') {
            steps {

                echo '=== Performing Health Checks ==='

                bat '''
                    timeout /t 40

                    curl http://localhost:8761/actuator/health
                    curl http://localhost:9090/actuator/health
                '''
            }
        }
    }

    post {

        success {

            echo '====================================='
            echo 'Deployment Successful'
            echo '====================================='

            echo 'Eureka Dashboard: http://localhost:8761'
            echo 'API Gateway: http://localhost:9090'
        }

        failure {

            echo '====================================='
            echo 'Deployment Failed'
            echo '====================================='
        }
    }
}