# es-watcher-plugin
Get Elasticsearch Events and peer to Redis


## Configure elasticsearch.yml
```$xslt
# Watcher configurations
watcher.activemq.uri: 10.0.0.14
watcher.activemq.port: 61616
watcher.activemq.que: es:events
security.manager.enabled: false
```

## Install
```
elasticsearch_home/bin/plugin install watcher-1.0-SNAPSHOT.zip
```

## Events
Publish events as config ```queue://es:events```
Message Detail: JSON String

### on Create Document

```$xslt
{"timestamp":1505218408103,"type":"CREATE","id":"AV52Ai4fPWpYlu0II-aG","version":1,"payload":"\n{\n    \"key\": \"placeholder\"\n}\n","index":"test"}
```

### on Delete Document
```

```