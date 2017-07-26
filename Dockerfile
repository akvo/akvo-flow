FROM ruby:2.4.1

RUN echo "deb http://ftp.debian.org/debian jessie-backports main" >> /etc/apt/sources.list && \
    apt-get update && \
    apt-get -t jessie-backports install -y -q --no-install-recommends openjdk-8-jdk git maven ant && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    update-java-alternatives -s java-1.8.0-openjdk-amd64 && \
    curl -L https://nodejs.org/dist/v8.2.1/node-v8.2.1-linux-x64.tar.xz | tar -xJf - --strip-components=1 -C /usr

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

COPY docker/startup.sh docker/build.sh /usr/local/bin/

RUN chmod a+x /usr/local/bin/*.sh

ENTRYPOINT ["/usr/local/bin/startup.sh"]

CMD ["/usr/local/bin/build.sh"]
