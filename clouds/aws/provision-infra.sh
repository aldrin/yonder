#!/bin/bash

# Install Docker
curl -fsSL get.docker.com -o get-docker.sh
chmod +x get-docker.sh
./get-docker.sh
usermod -aG docker ubuntu

# Install Compose
apt install python-pip emacs-nox -y
pip install docker-compose

# Start Docker and wait for it to be up
service docker start
while ! docker info; do sleep 10; done

# Start the infrastructure services
docker-compose up -d
