FROM docker.io/eclipse-temurin:17.0.12_7-jdk-jammy@sha256:d41eff8f20494968aaa1f5bbea4547303076b915d38f7d642441bd16538b45e3 AS builder

ARG ANDROID_SDK_DIST=commandlinetools-linux-11076708_latest.zip
ARG ANDROID_SDK_SHA256=2d2d50857e4eb553af5a6dc3ad507a17adf43d115264b1afc116f95c92e5e258

ENV ANDROID_HOME=/opt/android-sdk-linux

RUN apt-get update && apt-get install -y unzip git

RUN mkdir -p "${ANDROID_HOME}"

RUN curl -o sdk.zip "https://dl.google.com/android/repository/${ANDROID_SDK_DIST}"
RUN echo "${ANDROID_SDK_SHA256}" sdk.zip | sha256sum -c -
RUN unzip -q -d "${ANDROID_HOME}/cmdline-tools/" sdk.zip && \
    mv "${ANDROID_HOME}/cmdline-tools/cmdline-tools" "${ANDROID_HOME}/cmdline-tools/latest" && \
    rm sdk.zip

ENV PATH="${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin"

RUN mkdir /root/.android && touch /root/.android/repositories.cfg
RUN yes | sdkmanager --licenses

RUN sdkmanager "platform-tools"

ARG NDK_VERSION=27.0.12077973
ARG COMPILE_SDK_VERSION=34
ARG BUILD_TOOLS_VERSION=34.0.0

RUN sdkmanager "ndk;${NDK_VERSION}"
RUN sdkmanager "platforms;android-${COMPILE_SDK_VERSION}"
RUN sdkmanager "build-tools;${BUILD_TOOLS_VERSION}"

COPY gradlew /molly/
COPY gradle /molly/gradle/
RUN /molly/gradlew --version

ENV GRADLE_RO_DEP_CACHE=/.gradle-ro-cache

COPY . /molly/
WORKDIR /molly
RUN git clean -df

ENTRYPOINT ["./gradlew", "-PCI=true"]
