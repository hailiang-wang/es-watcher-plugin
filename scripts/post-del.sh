#! /bin/bash 
###########################################
#
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)
baseUrl=http://localhost:9200/test/external/AV55-K9okfgI7BpFPUOU/?pretty
# functions

# main
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
cd $baseDir
curl -XDELETE $baseUrl
