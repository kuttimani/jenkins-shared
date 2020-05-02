def call(String dockerfile, String targetName) {
    node {
        final String registry = 'https://registry.hub.docker.com'
        final String registryCreds = 'dockerhub'
        def image
        stage('Checkout from github') {
            checkout scm
        }
        stage('Build and Push Docker image') {
            final String imageVersion = sh(script: "grep build.version ${dockerfile} | cut -d'=' -f 2", returnStdout: true).trim()
            println "Building image version ${imageVersion}"
            currentBuild.displayName = imageVersion
            docker.withRegistry(registry, registryCreds) {
                image = docker.build(targetName, "- < ${dockerfile}")
                image.push(imageVersion)
                image.push('latest')
            }
        }
        stage('Clean up') {
            sh("docker rmi -f ${image.imageName()}")
        }
    }
}
