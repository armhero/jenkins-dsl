#!groovy
job('armhero/build.raspbian-jessie') {
  label('armhf')
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
        url('git@github.com:armhero/raspbian.git')
        branches('*/master')
        credentials('8ffaa0c1-6e5d-4884-b2ee-854685476789')
      }
    }
  }
  triggers {
    scm('H/5 * * * *')
    cron('H 5 * * 0')
  }
  steps {
    shell('sudo ./build.sh -a armhf -r jessie')
    shell('''
    sudo docker login -u \044{DOCKER_USERNAME} -p \044{DOCKER_PASSWORD}

    sudo docker tag armhero/raspbian:latest armhero/raspbian:jessie
    sudo docker push armhero/raspbian:latest
    sudo docker rmi armhero/raspbian:latest

    sudo docker push armhero/raspbian:jessie
    sudo docker rmi armhero/raspbian:jessie
    ''')
  }
  publishers {
    mailer('me@rootlogin.ch')
    wsCleanup()
  }
}
