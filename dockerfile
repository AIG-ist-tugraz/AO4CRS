FROM eclipse-temurin:20-jdk

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        maven \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY pom.xml ./

COPY src ./src
COPY data ./data
COPY lib ./lib

RUN mkdir results

RUN mvn install:install-file -Dfile=./lib/configurator-1.0.1-alpha-8.jar -DgroupId=at.tugraz.ist.ase.hiconfit -DartifactId=configurator -Dversion=1.0.1-alpha-8 -Dpackaging=jar -f pom.xml
RUN mvn package

RUN mv ./target/ao4crs-jar-with-dependencies.jar app.jar

RUN java -jar  app.jar -cfg ./data/conf/app.cfg