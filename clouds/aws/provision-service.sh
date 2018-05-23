#!/bin/bash

# Install Java 8
apt-get update -y
apt-get install openjdk-8-jre -y

# Make the jar executable
chmod +x backend.jar

# Point the service to the deployment infrastructure node
echo "RUN_ARGS=\"--spring.data.mongodb.uri=mongodb://$1/test --spring.rabbitmq.host=$1\"" > backend.conf

# Install as service
ln -s ~ubuntu/backend.jar /etc/init.d/backend

# Start the service
/etc/init.d/backend start
