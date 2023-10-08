#readme: https://github.com/vegardit/docker-graalvm-maven

#      vegardit/graalvm-maven:dev-java20 \


#On MacOS, may need to disable Rosetta before running
docker run --rm \
      --platform=linux/amd64 \
      -it \
      -v /Users/fantasy/.m2/graalmapped:/root/.m2/repository:rw \
      -v $PWD:/mnt/playground:rw \
      -w /mnt/playground \
      -e MAVEN_OPTS='-Xmx8G -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -Dfile.encoding=UTF-8 -Djdk.lang.Process.launchMechanism=vfork' \
      vegardit/graalvm-maven:dev-java21 \
      mvn clean package -DskipTests=true -P native -Dnet.bytebuddy.experimental=true
