pipeline {
    agent any
    
    tools {
        maven 'mymaven'
        jdk 'JDK21'
    }
    
    environment {
        NEXUS_URL = 'http://your-nexus-server:8081'
        NEXUS_CREDENTIALS = credentials('nexus-credentials')
        TOMCAT_URL = 'http://your-tomcat-server:8080'
        TOMCAT_CREDENTIALS = credentials('tomcat-credentials')
        SONARQUBE_SCANNER = 'sonar-scanner'
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
        
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonar-server') {
                    sh 'mvn sonar:sonar -Dsonar.projectKey=countryservice'
                }
            }
        }
        
        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }
        
        stage('Upload to Nexus') {
            steps {
                script {
                    // Trouver le fichier WAR généré
                    def warFile = findFiles(glob: '**/target/*.war')[0]
                    
                    // Téléverser vers Nexus
                    sh """
                        curl -u $NEXUS_CREDENTIALS \
                             -X POST \
                             -F "maven2.asset1=@${warFile.path}" \
                             -F "maven2.asset1.extension=war" \
                             -F "maven2.asset1.groupId=com.countryservice" \
                             -F "maven2.asset1.version=${env.BUILD_NUMBER}" \
                             "$NEXUS_URL/service/rest/v1/components?repository=maven-releases"
                    """
                }
            }
        }
        
        stage('Deploy to Tomcat') {
            steps {
                script {
                    def warFile = findFiles(glob: '**/target/*.war')[0]
                    
                    // Déployer sur Tomcat via le manager
                    sh """
                        curl -u $TOMCAT_CREDENTIALS \
                             --upload-file ${warFile.path} \
                             "$TOMCAT_URL/manager/text/deploy?path=/countryservice&update=true"
                    """
                }
            }
        }
    }
    
    post {
        always {
            echo "Build ${currentBuild.currentResult} - ${env.JOB_NAME} #${env.BUILD_NUMBER}"
        }
        success {
            emailext (
                subject: "SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: "Le déploiement de countryservice a réussi!\n${env.BUILD_URL}",
                to: "dev-team@company.com"
            )
        }
        failure {
            emailext (
                subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: "Le déploiement de countryservice a échoué.\n${env.BUILD_URL}",
                to: "dev-team@company.com"
            )
        }
    }
}
