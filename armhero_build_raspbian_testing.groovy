#!groovy
job('armhero/build.raspbian-testing') {
  disabled()
  label('pr-armv6')
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
    cron('H 5 * * *')
  }
  steps {
    shell('sudo  ./build.sh -a armhf -r testing -t testing')
    shell('''
    sudo docker login -u \044{DOCKER_USERNAME} -p \044{DOCKER_PASSWORD}

    sudo docker push armhero/raspbian:testing
    sudo docker rmi armhero/raspbian:testing

    curl -X POST https://hooks.microbadger.com/images/armhero/raspbian/lHKoGhLH0mK2Jw5wYQ36XbQMj5Q=
    ''')
  }
  publishers {
    mailer('me@rootlogin.ch')
    wsCleanup()
  }
}
