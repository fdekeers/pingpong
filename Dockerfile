# Base image: Alpine 3.21
FROM alpine:3.21

# Install packages
RUN apk add --no-cache \
    bash \
    git \
    libpcap \
    openjdk8

# Check Java installation
RUN java -version

# Create PingPong directory
ARG PINGPONG_HOME=/PingPong
RUN mkdir ${PINGPONG_HOME}
WORKDIR ${PINGPONG_HOME}

# Clone PingPong repository
RUN git clone https://github.com/uci-plrg/pingpong.git
WORKDIR ${PINGPONG_HOME}/pingpong/Code/Projects/PacketLevelSignatureExtractor
RUN ./gradlew
