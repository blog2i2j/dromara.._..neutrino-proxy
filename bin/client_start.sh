#!/bin/sh
# 中微子代理客户端启动脚本，基础参数请自行修改

JAVA_OPS="-server -Xms256m -Xmx512m -XX:PermSize=128M -XX:MaxPermSize=256M -XX:+HeapDumpOnOutOfMemoryError  -XX:HeapDumpPath=/work/$NAME/heapError/"
export JAVA_HOME=/work/programs/jdk/jdk1.8.0_171
export PATH=:$PATH:$JAVA_HOME/bin
export CLASSPATH=.:$JAVA_HOME/jre/lib/rt.jar:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
mkdir -p /work/$NAME/heapError/

NAME=neutrino-proxy-client
WORK=/work/projects
OUT=$WORK/$NAME/$NAME.out
JAR_PATH=$WORK/$NAME

function start(){
PID=`jps -l | grep $NAME.jar | cut -d' ' -f 1`
if [ -n "$PID" ]; then
echo "kill  pid : $PID"
kill $PID
fi
sleep 3
if [ -n "$PID" ]; then
echo "kill fail & kill -9  pid : $PID"
kill -9  $PID
fi
if [ -f "$OUT" ]; then
time=$(date "+%Y%m%d-%H%M%S")
cp $OUT $JAR_PATH/logs/back_$time.out
fi
rm -f $OUT
cd $JAR_PATH
nohup java $JAVA_OPS -jar $NAME.jar > $OUT 2>&1 &
echo "sleep 15s wating service start"
sleep 15
tail -200 $OUT
echo "start ${JAR_PATH} success ;log path:$OUT"
}

start