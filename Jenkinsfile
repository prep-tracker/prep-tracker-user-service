@Library('prep-tracker-shared-library') _
pipeline {
    agent any
    environment {
        SERVICE_NAME = "user-service"
        REGISTRY = "xxxxxxxx.dkr.ecr.us-east-1.amazonaws.com"
        IMAGE_TAG = "${GIT_COMMIT}"
        AWS_REGION = "ap-south-1"
    }
    stages {
        stage('CI') {
            steps {
                script {
                    if(env.BRANCH_NAME.startsWith('feature/')){
                        featureCi(
                            serviceName: 'user-service-test'
                        )
                    }else if(env.CHANGE_ID){
                        prValidation(
                            serviceName: 'user-service'
                        )
                    }else if(env.BRANCH_NAME == 'main'){
                        mainBuild(
                            serviceName: 'user-service'
                        )
                    }
                }
            }
        }
    }
}