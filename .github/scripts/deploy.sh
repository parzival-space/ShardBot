#!/bin/bash
# This script will run the build.

echo "Installing Maven..."
sudo apt update
sudo apt install maven -y

echo "Downloading Certificate..."
echo -n \
    | openssl s_client -connect $NEXUS_HOST:443 -servername $NEXUS_HOST \
    | openssl x509 > ~/remote.cert

echo "Installing Certificate..."
sudo keytool -import -noprompt -file ~/remote.cert -cacerts -storepass changeit

echo "Preparing Maven..."
mkdir -p ~/.m2
echo "<settings><servers><server><id>$NEXUS_ID</id><username>$NEXUS_USER</username><password>$NEXUS_PASS</password></server></servers></settings>" > ~/.m2/settings.xml

echo "Deploying..."
mvn deploy \
    -Dgit.tag=$VERSION_TAG
    -D"shard.client.token=$DISCORD_TOKEN"
    # -Dmaven.wagon.http.ssl.insecure=true \
    # -Dmaven.wagon.http.ssl.allowall=true \
    # -Dmaven.wagon.http.ssl.ignore.validity.dates=true \