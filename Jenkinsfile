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
        
        stage('Package WAR') {
            steps {
                sh 'mvn package -DskipTests'
                sh 'ls -la target/*.war'
            }
        }
        
        stage('Deploy to Tomcat') {
            steps {
                script {
                    // Vérifier que le WAR existe
                    def warFiles = findFiles(glob: 'target/*.war')
                    if (warFiles.length > 0) {
                        def warFile = warFiles[0]
                        echo "🎯 Fichier WAR trouvé: ${warFile.path}"
                        
                        // Déployer sur Tomcat
                        sh """
                            echo "📦 Déploiement sur Tomcat..."
                            curl -v -u $TOMCAT_USER:$TOMCAT_PASSWORD \
                                 --upload-file ${warFile.path} \
                                 "$TOMCAT_URL/manager/text/deploy?path=/countryservice&update=true"
                        """
                    } else {
                        error "❌ Aucun fichier WAR trouvé!"
                    }
                }
            }
        }
    }
    
    post {
        always {
            echo "🏁 Build ${currentBuild.currentResult} - ${env.JOB_NAME} #${env.BUILD_NUMBER}"
        }
        success {
            echo "✅ Déploiement réussi! Vérifiez: $TOMCAT_URL/countryservice"
        }
        failure {
            echo "❌ Le déploiement a échoué"
        }
    }
}
