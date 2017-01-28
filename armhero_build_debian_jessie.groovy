#!groovy
job('armhero/build.debian-jessie') {
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
        url('git@github.com:armhero/debian.git')
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
    shell('ARCH=armhf ./build.sh -r jessie')
    shell('''
    docker login -u \044{DOCKER_USERNAME} -p \044{DOCKER_PASSWORD}

    docker tag armhero/debian:latest armhero/debian:jessie
    docker push armhero/debian:latest
    docker rmi armhero/debian:latest

    docker push armhero/debian:jessie
    docker rmi armhero/debian:jessie
    ''')
  }
  publishers {
    mailer('me@rootlogin.ch')
    wsCleanup()
  }
}
