#!/bin/sh
JAVA_HOME=/home/jdk
PATH=$PATH:${JAVA_HOME}/bin
LANG=GBK
LC_ALL=zh_CN.GBK
PROJECT_HOME=/home/workspace/gather/current-release/target/webapp
LIB_HOME=${PROJECT_HOME}/WEB-INF/lib
for i in ${LIB_HOME}/*.jar ; do
  CLASSPATH=${CLASSPATH}:${i}
  #echo $i
done
RESIN_HOME=/home/resin
for i in ${RESIN_HOME}/lib/*.jar ; do
  CLASSPATH=${CLASSPATH}:${i}
  #echo $i
done

CLASSPATH=${PROJECT_HOME}/WEB-INF/classes:${CLASSPATH}

export PATH LANG LC_ALL CLASSPATH

java -Dlog4j.configuration=log4j.properties.schedule com.netease.gather.clawerpic.execute.ClawerPicExecutor $@

