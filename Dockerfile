FROM ruby:2.4.1

RUN echo "deb http://ftp.debian.org/debian jessie-backports main" >> /etc/apt/sources.list && \
    apt-get update && \
    apt-get -t jessie-backports install -y -q --no-install-recommends openjdk-8-jdk git unzip gnupg2 && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    update-java-alternatives -s java-1.8.0-openjdk-amd64 && \
    curl -L https://nodejs.org/dist/v8.2.1/node-v8.2.1-linux-x64.tar.xz | tar -xJf - --strip-components=1 -C /usr


ARG MAVEN_VERSION=3.5.3
ARG USER_HOME_DIR="/root"
ARG SHA=b52956373fab1dd4277926507ab189fb797b3bc51a2a267a193c931fffad8408
ARG BASE_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries

RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
  && curl -fsSL -o /tmp/apache-maven.tar.gz ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
  && echo "${SHA}  /tmp/apache-maven.tar.gz" | sha256sum -c - \
  && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
  && rm -f /tmp/apache-maven.tar.gz \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven

RUN mkdir /akvo-flow

WORKDIR /akvo-flow

COPY Dashboard/Gemfile Dashboard/Gemfile.lock Dashboard/app/cljs/project.clj ./

# throw errors if Gemfile has been modified since Gemfile.lock
RUN bundle config --global frozen 1

RUN bundle install

# Leinigen
ENV LEIN_ROOT=1

RUN curl -L -O https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein && \
    mv lein /usr/local/bin/ && \
    chmod a+x /usr/local/bin/lein && \
    lein

ENTRYPOINT ["/app/src/docker/startup.sh"]

CMD ["/app/src/docker/build.sh"]
