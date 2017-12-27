# es-watcher-plugin
ElasticSearch Document 监控插件

使用ES支持的插件机制向Apache ActiveMQ服务发送文档增删改的通知，兼容**ElasticSearch v2.x**版本。

## 安装和配置

配置elasticsearch.yml

```
# 接受事件通知的ActiveMQ地址
watcher.activemq.uri: 10.0.0.14
watcher.activemq.port: 61616
# 接受事件通知的Queue前缀
watcher.activemq.que.prefix: es:index
# 关闭Java Security
security.manager.enabled: false
```

安装plugin
```$xslt
ES_HOME/bin/plugin install watcher-1.0-SNAPSHOT.zip
```

检查安装
```
ES_HOME/bin/plugin list
```
确认watcher已经被安装。

控制日志级别
在logging.yml中配置Log Level: [DEBUG, INFO, WARN, ERROR]

```$xslt
logger:
  es.plugins.watcher.Plugin: DEBUG
  es.plugins.watcher.Service: DEBUG
  es.plugins.watcher.beans.IndexChanges: DEBUG
```

## 消息通知事件
启动ES服务后，当ES中的索引增加、更改和删除文档时，会生成事件通知到Apache ActiveMQ中。

不同的索引(index)对应不同的Queue，Queue的名字为: es:index:INDEX_NAME，```es:index```是写入到elasticsearch.yml的配置项。

### 消息格式

在消息的message detail中，包含了一个JSON格式的字符串。

#### 文档生成
```
{
  "timestamp": 1505287653765,
  "type": "CREATE",
  "id": "AV56Isko-n4co01PbYIA",
  "version": 1,
  "payload": "\n{\n    \"key\": \"placeholder\"\n}\n",
  "index": "test"
}
```


#### 文档更新
```
{
  "timestamp": 1505290209743,
  "type": "INDEX",
  "id": "AV55-LCkkfgI7BpFPUOX",
  "version": 2,
  "payload": "{\"key\":\"foo\"}",
  "index": "test"
}

```

#### 文档删除

```$xslt
{
  "timestamp": 1505288488221,
  "type": "DELETE",
  "id": "AV55-K9okfgI7BpFPUOU",
  "version": 3,
  "index": "test"
}
```

timestamp: 事件发生时的时间戳

type: 事件类型

id: ES中该文档的ID

payload: 文档的内容，也是一个json的字符串

index: 索引的id

## 测试环境

Apache ActiveMQ
```$xslt
uri: 10.0.0.14
port: 61616
```

管理控制面板
[http://10.0.0.14:8161/admin/queues.jsp](http://10.0.0.14:8161/admin/queues.jsp)

用户名: admin 密码: admin

## Apache ActiveMQ 客户端SDK
http://activemq.apache.org/cross-language-clients.html

