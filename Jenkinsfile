pipeline {
    agent any

    triggers {
        githubPush()
    }

    environment {
        DOCKER_BUILDKIT = '1'
        COMPOSE_PROJECT_NAME = 'inventorymanagement'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Prepare') {
            steps {
                sh 'git config --global --add safe.directory "$WORKSPACE" || true'
                sh 'docker --version'
                sh 'docker compose version'
            }
        }

        stage('Build And Deploy') {
            steps {
                withCredentials([
                    string(credentialsId: 'app-auth-username', variable: 'APP_AUTH_USERNAME'),
                    string(credentialsId: 'app-auth-password', variable: 'APP_AUTH_PASSWORD'),
                    string(credentialsId: 'app-auth-secret', variable: 'APP_AUTH_SECRET')
                ]) {
                    sh '''
                        cat > .env.prod <<EOF
APP_AUTH_USERNAME=${APP_AUTH_USERNAME}
APP_AUTH_PASSWORD=${APP_AUTH_PASSWORD}
APP_AUTH_SECRET=${APP_AUTH_SECRET}
APP_AUTH_EXPIRATION_MS=3600000
EOF

                        docker compose \
                          -f docker-compose.yml \
                          -f docker-compose.prod.yml \
                          --env-file .env.prod \
                          up -d --build --remove-orphans
                    '''
                }
            }
        }

        stage('Health Check') {
            steps {
                withCredentials([
                    string(credentialsId: 'app-auth-username', variable: 'APP_AUTH_USERNAME'),
                    string(credentialsId: 'app-auth-password', variable: 'APP_AUTH_PASSWORD')
                ]) {
                    sh '''
                        sleep 30
                        curl --fail --silent --show-error \
                          -X POST http://localhost:8088/auth/login \
                          -H "Content-Type: application/json" \
                          -d "{\\"username\\":\\"${APP_AUTH_USERNAME}\\",\\"password\\":\\"${APP_AUTH_PASSWORD}\\"}" \
                          > /tmp/inventory-auth-response.json
                    '''
                }
            }
        }
    }

    post {
        always {
            sh 'rm -f .env.prod /tmp/inventory-auth-response.json || true'
        }
    }
}
