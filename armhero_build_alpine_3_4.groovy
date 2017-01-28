#!groovy
job('armhero/build.alpine-3.4') {
  label('rpi3')
  logRotator {
    numToKeep(30)
    artifactNumToKeep(1)
    daysToKeep(-1)
    artifactDaysToKeep(-1)
  }
  wrappers {
    preBuildCleanup()
    timestamps()
    colorizeOutput()
    credentialsBinding {
      usernamePassword('DOCKER_USERNAME', 'DOCKER_PASSWORD', '1d448f61-46d6-4af8-a517-9a06866447bb')
    }
  }
  scm {
    git {
      remote {
        url('git@code.dini-mueter.net:armhero/alpine.git')
        branches('*/master')
        credentials('8ffaa0c1-6e5d-4884-b2ee-854685476789')
      }
    }
  }
  triggers {
    scm('H/5 * * * *')
    cron('H 4 * * 0')
  }
  steps {
    shell('sudo ARCH=armhf ./build.sh -r v3.4')
    shell('''
    docker login -u \044{DOCKER_USERNAME} -p \044{DOCKER_PASSWORD}

    docker tag armhero/alpine:latest armhero/alpine:3.4
    docker push armhero/alpine:latest
    docker rmi armhero/alpine:latest

    docker push armhero/alpine:3.4
    docker rmi armhero/alpine:3.4
    ''')
  }
  publishers {
    mailer('me@rootlogin.ch')
    wsCleanup()
  }
}
