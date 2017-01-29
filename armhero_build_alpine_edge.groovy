#!groovy
job('armhero/build.alpine-edge') {
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
        url('git@github.com:armhero/alpine.git')
        branches('*/master')
        credentials('8ffaa0c1-6e5d-4884-b2ee-854685476789')
      }
    }
  }
  triggers {
    scm('H/5 * * * *')
    cron('H 4 * * *')
  }
  steps {
    shell('sudo ARCH=armhf ./build.sh -r edge -t edge')
    shell('''
    sudo docker login -u \044{DOCKER_USERNAME} -p \044{DOCKER_PASSWORD}

    sudo docker push armhero/alpine:edge
    sudo docker rmi armhero/alpine:edge

    # Access Microbadger Github
    curl -X POST https://hooks.microbadger.com/images/armhero/alpine/ufmYLRNfn7Uj_sXNIW2SGuEg6Qo=
    ''')
  }
  publishers {
    mailer('me@rootlogin.ch')
    wsCleanup()
  }
}
