pipeline {
    agent any
    
    tools {
        maven 'maven-3.9'
        jdk 'jdk17'
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
        
        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }
        
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('MySonarQubeServer') {
                    sh 'mvn sonar:sonar -Dsonar.projectKey=country-service'
                }
            }
        }
        
        stage('Deploy to Tomcat') {
            steps {
                sh 'cp target/*.war /var/lib/tomcat9/webapps/countryservice.war'
            }
        }
    }
    
    post {
        always {
            emailext (
                subject: "Build ${currentBuild.currentResult}: ${env.JOB_NAME}",
                body: "Build: ${env.BUILD_URL}\\nResult: ${currentBuild.currentResult}",
                to: "ilyes.sellami@ensi-uma.tn"
            )
        }
    }
}
