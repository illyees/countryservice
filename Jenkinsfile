pipeline {
    agent any
    
    tools {
        maven 'mymaven'
        jdk 'JDK21'
    }
    
    environment {
        TOMCAT_URL = 'http://localhost:8080'
        TOMCAT_USER = 'deployer'
        TOMCAT_PASSWORD = 'deployer123'
        DOCKER_IMAGE = 'illyees/countryservice'
        DOCKER_REGISTRY = 'docker.io'
    }
    
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/illyees/countryservice.git'
            }
        }
        
        stage('Build & Test') {
            steps {
                sh 'mvn clean compile'
            }
        }
        
        stage('Unit Tests') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Package JAR') {
            steps {
                sh 'mvn package -DskipTests'
                sh 'ls -la target/*.jar'
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    // Construire l'image Docker
                    sh """
                        docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} .
                        docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${DOCKER_IMAGE}:latest
                    """
                }
            }
        }
        
        stage('Push to Docker Hub') {
            steps {
                script {
                    withCredentials([string(credentialsId: 'dockerhub-pwd', variable: 'DOCKER_PASSWORD')]) {
                        sh """
                            docker login -u illyees -p ${DOCKER_PASSWORD}
                            docker push ${DOCKER_IMAGE}:${BUILD_NUMBER}
                            docker push ${DOCKER_IMAGE}:latest
                        """
                    }
                }
            }
        }
        
        stage('Deploy to Tomcat') {
            steps {
                script {
                    def warFiles = findFiles(glob: 'target/*.war')
                    if (warFiles.length > 0) {
                        def warFile = warFiles[0]
                        echo "🎯 Fichier WAR trouvé: ${warFile.path}"
                        
                        sh """
                            echo "📦 Déploiement sur Tomcat..."
                            curl -v -u $TOMCAT_USER:$TOMCAT_PASSWORD \
                                 --upload-file ${warFile.path} \
                                 "$TOMCAT_URL/manager/text/deploy?path=/countryservice&update=true"
                        """
                    } else {
                        echo "ℹ️ Aucun fichier WAR trouvé (probablement un JAR)"
                    }
                }
            }
        }
        
        stage('Deploy with Docker') {
            steps {
                script {
                    // Arrêter l'ancien conteneur si existe
                    sh 'docker rm -f countryservice-app || true'
                    
                    // Lancer le nouveau conteneur
                    sh """
                        docker run -d \
                            --name countryservice-app \
                            -p 8081:8080 \
                            ${DOCKER_IMAGE}:${BUILD_NUMBER}
                    """
                }
            }
        }
    }
    
    post {
        always {
            echo "🏁 Build ${currentBuild.currentResult} - ${env.JOB_NAME} #${env.BUILD_NUMBER}"
            
            // Nettoyage Docker
            sh '''
                docker system prune -f || true
            '''
        }
        success {
            echo "✅ Déploiement réussi!"
            echo "🌐 Application Docker: http://localhost:8081"
            echo "🐳 Image Docker: ${DOCKER_IMAGE}:${BUILD_NUMBER}"
        }
        failure {
            echo "❌ Le déploiement a échoué"
        }
    }
}
