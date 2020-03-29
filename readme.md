##  节点信息

```html
http://localhost/computer/slave_name/api/json?pretty=true -- 浏览器查看
https://www.cnblogs.com/zjsupermanblog/archive/2017/07/26/7238422.html -- API 比较详细
https://javadoc.jenkins-ci.org/hudson/model/Computer.html#offlineCause -- 官方文档，详细
```

* https://www.jianshu.com/p/c9bc35b26c26

* 只需要将slave_name换成指定的slave名称即可,返回数据是json格式的

* 如果key offline的value为true说该slave掉线了，offlineCauseReason表面原因。

* 可以写个简单地python脚本，把脚本放到jenkins上定时执行进行监控，最好是限定这个任务在master上执行，保证任务顺利执行。shell里可以判断返回不为空时设置为exit 1，使任务失败，对于任务失败配置邮件(构建后任务里)提醒即可。

```python
import urllib2
import json
baseapi = http://localhost/computer/%s/api/json?pretty=true
def request(slaves):
    slaveapis = slaves.split(",")
    offlineapi = ""
    for api in slaveapis:
        url = baseapi % api
        ret = json.load(urllib2.urlopen(url))
        if ret['offline']:
            offlineapi += url
            offlineapi += ","
    return offlineapi
```



#### import一堆

```groovy
import jenkins.model.Jenkins
import hudson.model.User
import hudson.security.Permission
import hudson.EnvVars
import hudson.model.*
import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*
```

.model.*



#### 节点offlineCauseReason

* http://localhost:9000/computer/123/api/json?pretty=true&tree=offlineCauseReason

#### 节点offline

* http://localhost:9000/computer/123/api/json?pretty=true&tree=offline

#### 筛选节点

```groovy
slaves = hudson.model.Hudson.instance.slaves
offlines = []
onlines=[]
for(slave in slaves){
  if(slave.getComputer().isOnline() == true){
    onlines.add(slave.name)
  }
  if(slave.getComputer().getOfflineCause().toString().contains('Disconnected by')){
  	offlines.add(slave.name)
    println 'offline casue is : ' + slave.getComputer().getOfflineCause()
  }
}
println onlines
println offlines

简化
def offs = slaves.findAll{it.getComputer().getOfflineCause().toString().contains('Disconnected by')}
def offs_names = offs.collect{[it.name,it.getComputer().getOfflineCause().toString()]}
println offs
def ons = slaves.findAll{it.getComputer().isOnline() == true}
def ons_names = ons.collect{it.name}
println ons
```



```groovy
《============================================》
只能做筛选，不能用于pipeline中，因为会引起序列化错误。
在pipeline中一定要使用for(slave in slaves){}结构。
《============================================》

def slaves = hudson.model.Hudson.instance.slaves
def offs = slaves.findAll{it.getComputer().getOfflineCause().toString().contains('Disconnected by')}
        println offs
        if(offs.size() > 0){offs.each{println it.name+ ' : '+it.getComputer().getOfflineCause().toString()}}
        else{println 'no nodes to be on lines'}


def slaves = hudson.model.Hudson.instance.slaves
def ons = slaves.findAll{it.getComputer().isOnline() == true}
println ons
if(ons.size() > 0){
    ons.each{
        println it.name
        //该指令的offline cause无效
        it.getComputer().setTemporarilyOffline(true,'from off_line stage') 
    }
}
else{ println "No nodes to be on_line"}

```


#### 节点属性

```groovy
for (aSlave in hudson.model.Hudson.instance.slaves) {
	println('====================');
	println('Name: ' + aSlave.name);
	println('getLabelString: ' + aSlave.getLabelString());
	println('getNumExectutors: ' + aSlave.getNumExecutors());
	println('getRemoteFS: ' + aSlave.getRemoteFS());
	println('getMode: ' + aSlave.getMode());
	println('getRootPath: ' + aSlave.getRootPath());
	println('getDescriptor: ' + aSlave.getDescriptor());
	println('getComputer: ' + aSlave.getComputer());
	println('	computer.isAcceptingTasks: ' + aSlave.getComputer().isAcceptingTasks());
	println('	computer.isLaunchSupported: ' + aSlave.getComputer().isLaunchSupported());
	println('	computer.getConnectTime: ' + aSlave.getComputer().getConnectTime());
	println('	computer.getDemandStartMilliseconds: ' + 	aSlave.getComputer().getDemandStartMilliseconds());
	println('	computer.isOffline: ' + aSlave.getComputer().isOffline());
	println('	computer.offlineCause: ' + aSlave.getComputer().getOfflineCause());
	println('	computer.countBusy: ' + aSlave.getComputer().countBusy());
	if (aSlave.name == 'NAME OF NODE TO DELETE') {
	  println('Shutting down node!!!!');
	  aSlave.getComputer().setTemporarilyOffline(true,null);
	  aSlave.getComputer().doDoDelete();
	}
	println('	computer.getLog: ' + aSlave.getComputer().getLog());
	println('	computer.getBuilds: ' + aSlave.getComputer().getBuilds());
	}
```



#### jenkins Python api

http://www.voidcn.com/article/p-mkxeljmv-nh.html



#### 节点ip

通过master，远程控制slave

```groovy
import hudson.util.RemotingDiagnostics;

print_ip = 'println InetAddress.localHost.hostAddress';
print_hostname = 'println InetAddress.localHost.canonicalHostName';

// here it is - the shell command, uname as example 
uname = 'def proc = "uname -a".execute(); proc.waitFor(); println proc.in.text';

for (slave in hudson.model.Hudson.instance.slaves) {
    println slave.name;
    println RemotingDiagnostics.executeGroovy(print_ip, slave.getChannel());
    println RemotingDiagnostics.executeGroovy(print_hostname, slave.getChannel());
    println RemotingDiagnostics.executeGroovy(uname, slave.getChannel());
}
```



#### 节点上线

```groovy
it.getComputer().cliOnline()
```



#### 节点下线

```groovy
it.getComputer().doDoDisconnect('temp offline') // 这个是下线，不是临时下线，offlineCause没有显示
it.getComputer().disconnect()
it.getComputer().setTemporarilyOffline(true,'from off_line stage') //offlineCause没有显示,还会出错，原因没找到，只能用 null代替
```



#### 节点配置

```groovy
echo ？？？？ > secret-file
java -jar agent.jar -jnlpUrl http://localhost:9000/computer/456/slave-agent.jnlp -secret @secret-file -workDir "E:\？？？\？？？？"
```

