#! /bin/bash 
###########################################
#
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)
APP_HOME=~/git/elasticsearch-docker/
PLUGIN_PLACE=$APP_HOME/elasticsearch/plugins/watcher

# functions

# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
cd $baseDir/..
set -x
mvn clean compile package -DskipTests

if [ $? -eq 1 ]; then
    exit 1
fi

cd $APP_HOME
docker-compose down
if [ -e $PLUGIN_PLACE ]; then
    rm -rf $PLUGIN_PLACE
fi
unzip $baseDir/../target/releases/watcher-1.0-SNAPSHOT.zip -d $PLUGIN_PLACE
docker-compose up elasticsearch
