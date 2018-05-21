FROM ruby:2.4.4-alpine3.7 as ruby-deps

COPY Dashboard/Gemfile Dashboard/Gemfile.lock /

# throw errors if Gemfile has been modified since Gemfile.lock
RUN set -ex ; \
    apk add --no-cache git build-base libffi-dev && \
    bundle config --global frozen 1 && \
    bundle install

FROM ruby:2.4.4-alpine3.7

ARG CLOUD_SDK_VERSION=198.0.0
ENV LEIN_ROOT=1
ENV PATH="/google-cloud-sdk/bin:${PATH}"
ENV CLOUDSDK_PYTHON_SITEPACKAGES=1
ENV BUNDLE_GEMFILE=/app/src/Dashboard/Gemfile

COPY --from=ruby-deps /usr/local/bundle /usr/local/bundle

RUN set -ex ; \
    apk add --no-cache \
    bash=4.4.19-r1 \
    curl=7.60.0-r0 \
    git=2.15.0-r1 \
    nodejs=8.9.3-r1 \
    openjdk8=8.151.12-r0 \
    openssh-client=7.5_p1-r8 \
    python2=2.7.14-r2 \
    py-crcmod=1.7-r0 \
    py-openssl=17.2.0-r0 \
    maven=3.5.2-r0 \
    libc6-compat=1.1.18-r3 \
    su-exec=0.2-r0 \
    shadow=4.5-r0 \
    zip=3.0-r4 && \
    curl -O "https://dl.google.com/dl/cloudsdk/channels/rapid/downloads/google-cloud-sdk-${CLOUD_SDK_VERSION}-linux-x86_64.tar.gz" && \
    tar xzf "google-cloud-sdk-${CLOUD_SDK_VERSION}-linux-x86_64.tar.gz" && \
    rm "google-cloud-sdk-${CLOUD_SDK_VERSION}-linux-x86_64.tar.gz" && \
    ln -s /lib /lib64 && \
    gcloud config set core/disable_usage_reporting true && \
    gcloud config set component_manager/disable_update_check true && \
    gcloud components install app-engine-java && \
    rm -rf /google-cloud-sdk/.install/.backup && \
    rm -rf /google-cloud-sdk/.install/.download && \
    curl -L -o /usr/local/bin/lein https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein && \
    chmod a+x /usr/local/bin/lein && \
    lein && \
    adduser -D -h /home/akvo -s /bin/bash akvo akvo

WORKDIR /app/src
