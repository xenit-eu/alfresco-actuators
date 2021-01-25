pipeline {
    agent any

    stages {
        stage("Integration test") {
            steps {
                sh "./gradlew integrationTest"
            }
        }
    }

    post {
        always {
            sh "./gradlew composeDownForced"
            script {
                junit '**/build/**/TEST-*.xml'
            }
        }
    }
}
