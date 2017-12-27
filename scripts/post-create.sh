#! /bin/bash 
###########################################
#
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)
baseUrl=http://localhost:9200/faq_test/external?pretty
# functions

# main
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
cd $baseDir
curl -POST $baseUrl -d '
{
    "key": "placeholder"
}
'