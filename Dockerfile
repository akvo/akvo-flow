FROM google/cloud-sdk:196.0.0-slim as gcloud

RUN set -ex; \
    apt-get update && \
    apt-get install -y --no-install-recommends \
    google-cloud-sdk-app-engine-java=196.0.0-0 && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

FROM ruby:2.4.4 as ruby-deps

COPY Dashboard/Gemfile Dashboard/Gemfile.lock /

# throw errors if Gemfile has been modified since Gemfile.lock
RUN bundle config --global frozen 1 && bundle install

FROM ruby:2.4.4-slim-jessie

ARG MAVEN_VERSION=3.5.3
ARG USER_HOME_DIR="/root"
ARG SHA=b52956373fab1dd4277926507ab189fb797b3bc51a2a267a193c931fffad8408
ARG BASE_URL="https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries"

ENV LEIN_ROOT=1
ENV MAVEN_HOME=/usr/share/maven

COPY --from=gcloud /usr/lib/google-cloud-sdk /usr/lib/google-cloud-sdk

COPY --from=ruby-deps /usr/local/bundle /usr/local/bundle

RUN set -ex; \
    echo "deb http://ftp.debian.org/debian jessie-backports main" >> /etc/apt/sources.list && \
    apt-get update && \
    apt-get -t jessie-backports install -y -q --no-install-recommends \
    curl=7.38.0-4+deb8u10 \
    git=1:2.11.0-3~bpo8+1 \
    openjdk-8-jdk=8u162-b12-1~bpo8+1 \
    openssh-client=1:6.7p1-5+deb8u3 \
    python-dev=2.7.9-1 \
    python-setuptools=33.1.1-1~bpo8+1 \
    unzip=6.0-16+deb8u2 \
    xz-utils=5.1.1alpha+20120614-2+b3 && \
    easy_install -U pip && \
    pip install crcmod==1.7 pyopenssl==17.5.0 && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    update-java-alternatives -s java-1.8.0-openjdk-amd64 && \
    curl -L https://nodejs.org/dist/v8.11.1/node-v8.11.1-linux-x64.tar.xz | tar -xJf - --strip-components=1 -C /usr && \
    mkdir -p /usr/share/maven /usr/share/maven/ref && \
    curl -fsSL -o /tmp/apache-maven.tar.gz "${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz" && \
    echo "${SHA}  /tmp/apache-maven.tar.gz" | sha256sum -c - && \
    tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 && \
    rm -rf /tmp/apache-maven.tar.gz && \
    ln -s /usr/share/maven/bin/mvn /usr/bin/mvn && \
    ln -s /usr/lib/google-cloud-sdk/bin/gcloud /usr/bin/gcloud && \
    curl -L -o /usr/local/bin/lein https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein && \
    chmod a+x /usr/local/bin/lein && \
    lein && \
    mkdir /akvo-flow

WORKDIR /akvo-flow
