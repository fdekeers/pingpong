# Base image: Alpine 3.21
FROM alpine:3.21

WORKDIR /

# Install packages
RUN apk add --no-cache \
    bash \
    git \
    libpcap \
    openjdk8

# Check Java installation
RUN java -version

# Clone PingPong repository
RUN git clone https://github.com/uci-plrg/pingpong.git
WORKDIR /pingpong/Code/Projects/PacketLevelSignatureExtractor
RUN ./gradlew
