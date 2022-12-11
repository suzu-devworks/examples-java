ARG VARIANT="17-jdk-jammy"

FROM eclipse-temurin:${VARIANT} as develop-stage
WORKDIR /workspace

# Configure apt and install packages
RUN set -x \
    && apt update \
    && apt install -y --no-install-recommends apt-utils dialog 2>&1 \
    && apt install -y sudo git wget unzip \
    ;

# Create a non-root user to use if preferred
ARG USERNAME=vscode
ARG USER_UID=2001
ARG USER_GID=$USER_UID
RUN set -x \
    && groupadd --gid $USER_GID $USERNAME \
    && useradd -s /bin/bash --uid $USER_UID --gid $USER_GID -m $USERNAME \
    \
    && echo $USERNAME ALL=\(root\) NOPASSWD:ALL > /etc/sudoers.d/$USERNAME \
    && chmod 0440 /etc/sudoers.d/$USERNAME \
    ;

# Configure Maven
ARG MAVEN_VERSION=3.8.6
ARG MAVEN_BASE_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries
ENV MAVEN_HOME /opt/maven
RUN set -x \
    && mkdir -p ${MAVEN_HOME}/ref \
    && curl -fsSL -o /tmp/apache-maven.tar.gz "${MAVEN_BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz" \
    \
    && tar -xzf /tmp/apache-maven.tar.gz -C "${MAVEN_HOME}" --strip-components=1 \
    && ln -s "${MAVEN_HOME}/bin/mvn" /usr/bin/mvn \
    && rm -f /tmp/apache-maven.tar.gz \
    ;

# Configure Gradle
ARG GRADLE_VERSION=7.6
ARG GRADLE_BASE_URL=https://services.gradle.org/distributions
ENV GRADLE_HOME /opt/gradle
RUN set -x \
    && wget --no-verbose --output-document=/tmp/gradle.zip "${GRADLE_BASE_URL}/gradle-${GRADLE_VERSION}-bin.zip" \
    \
    && unzip /tmp/gradle.zip \
    && mv "gradle-${GRADLE_VERSION}" "${GRADLE_HOME}/" \
    && ln -s "${GRADLE_HOME}/bin/gradle" /usr/bin/gradle \
    && rm /tmp/gradle.zip \
    \
    ;

# Clean up
RUN set -x \
    && apt autoremove -y \
    && apt clean -y \
    && rm -rf /var/lib/apt/lists/* \
    ;
