pipeline {
    agent any
    
    tools {
        maven 'mymaven'
        jdk 'JDK21'
    }
    
    stage('Diagnostic') {
    steps {
        sh '''
            echo "=== STRUCTURE ANALYSIS ==="
            pwd
            ls -la
            find . -name "pom.xml" -type f
        '''
    }
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
        stage('display message') {
            steps {
                echo 'Hello from github'
            }
        }
        
        stage('Deploy to Tomcat') {
            steps {
                sh '''
            curl --upload-file target/countryservice.war \
                 "http://tomcat-user:tomcat-password@tomcat-server:8080/manager/text/deploy?path=/countryservice&update=true"
        '''
            }
        }
    }
    
    post {
        always {
            echo "Build ${currentBuild.currentResult} - ${env.JOB_NAME} #${env.BUILD_NUMBER}"
        }
    }
}
