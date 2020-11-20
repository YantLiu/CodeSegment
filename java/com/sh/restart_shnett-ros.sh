#!/bin/sh
#查询jar包的端口
app="shnett-ros"
jarName=$app".jar"
pidlist=`ps -ef|grep ${jarName} |grep -v "grep"|awk '{print $2}'`

#创建kill进程的方法
function stop(){
if ["$pidlist" == ""]
then
echo "----jar 已经关闭----"

else
echo "tomcat进程号 :$pidlist"
kill -9 $pidlist
echo "KILL $pidlist:"
fi
}

#执行方法
stop
#启动jar包
nohup nohup java -jar -server -Xms512M -Xmx512M -Xss256k -XX:NewSize=128M -XX:MaxNewSize=128M  $jarName --spring.profiles.active=$1 > $app".out" &