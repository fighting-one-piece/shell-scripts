#!/bin/bash

export BUILD_ID=dontKillMe
export JAVA_HOME=/usr/local/jdk1.8.0_102
source /etc/profile

SERVICE_ZOOKEEPER_COUNT=`/usr/local/jdk1.8.0_102/bin/jps|grep QuorumPeerMain|wc -l`
if [ $SERVICE_ZOOKEEPER_COUNT -eq 0 ];then
    echo `date "+%Y-%m-%d %H:%M:%S"` 'startup zookeeper service' >> /home/wulin/Project/monitor/monitorservice.log
    cd /home/wulin/Software/zookeeper-3.4.10
    /home/wulin/Software/zookeeper-3.4.10/bin/zkServer.sh start
    echo `date "+%Y-%m-%d %H:%M:%S"` 'startup zookeeper service finish ' >> /home/wulin/Project/monitor/monitorservice.log
else
    echo `date "+%Y-%m-%d %H:%M:%S"` $SERVICE_ZOOKEEPER_COUNT 'zookeeper service normal running' >> /home/wulin/Project/monitor/monitorservice.log
fi

SERVICE_KAFKA_COUNT=`/usr/local/jdk1.8.0_102/bin/jps|grep Kafka|wc -l`
if [ $SERVICE_KAFKA_COUNT -eq 0 ];then
    sleep 10s
    echo `date "+%Y-%m-%d %H:%M:%S"` 'startup kafka service' >> /home/wulin/Project/monitor/monitorservice.log
    cd /home/wulin/Software/kafka_2.12-0.10.2.0
    /home/wulin/Software/kafka_2.12-0.10.2.0/bin/kafka-server-start.sh -daemon config/server.properties > /dev/null 2>&1 &
    echo `date "+%Y-%m-%d %H:%M:%S"` 'startup kafka service finish ' >> /home/wulin/Project/monitor/monitorservice.log
else
    echo `date "+%Y-%m-%d %H:%M:%S"` $SERVICE_KAFKA_COUNT 'kafka service normal running' >> /home/wulin/Project/monitor/monitorservice.log
fi

SERVICE_ES_COUNT=`ps -ef|grep elastic-241-pg.jar|grep -v "grep"|wc -l`
if [ $SERVICE_ES_COUNT -eq 0 ];then
    echo `date "+%Y-%m-%d %H:%M:%S"` 'startup elasticsearch 2.4.1 service' >> /home/wulin/Project/monitor/monitorservice.log
    cd /home/wulin/Project/elasticsearch/2.4.1/server1
    $JAVA_HOME/bin/java -jar -Dspring.profiles.active=production -Dserver.port=10021 /home/wulin/Project/elasticsearch/2.4.1/server1/elastic-241-pg.jar > /dev/null 2>&1 &
    cd /home/wulin/Project/elasticsearch/2.4.1/server2
    $JAVA_HOME/bin/java -jar -Dspring.profiles.active=production -Dserver.port=10022 /home/wulin/Project/elasticsearch/2.4.1/server2/elastic-241-pg.jar > /dev/null 2>&1 &
    echo `date "+%Y-%m-%d %H:%M:%S"` 'startup elasticsearch 2.4.1 service finish ' >> /home/wulin/Project/monitor/monitorservice.log
else
    echo `date "+%Y-%m-%d %H:%M:%S"` $SERVICE_ES_COUNT 'elasticsearch 2.4.1 service normal running' >> /home/wulin/Project/monitor/monitorservice.log
fi

SERVICE_APIGATEWAY_COUNT=`ps -ef|grep api-gateway.jar|grep -v "grep"|wc -l`
if [ $SERVICE_APIGATEWAY_COUNT -eq 0 ];then
    echo `date "+%Y-%m-%d %H:%M:%S"` 'startup api gateway service' >> /home/wulin/Project/monitor/monitorservice.log
    cd /home/wulin/Project/api-gateway/server1
    $JAVA_HOME/bin/java -jar -Dserver.port=10011 /home/wulin/Project/api-gateway/server1/api-gateway.jar > /dev/null 2>&1 &
    cd /home/wulin/Project/api-gateway/server2
    $JAVA_HOME/bin/java -jar -Dserver.port=10012 /home/wulin/Project/api-gateway/server2/api-gateway.jar > /dev/null 2>&1 &
    echo `date "+%Y-%m-%d %H:%M:%S"` 'startup api gateway service finish ' >> /home/wulin/Project/monitor/monitorservice.log
else
    echo `date "+%Y-%m-%d %H:%M:%S"` $SERVICE_APIGATEWAY_COUNT 'api gateway service normal running' >> /home/wulin/Project/monitor/monitorservice.log
fi

SERVICE_EUREKA_COUNT=`ps -ef|grep eureka-server.jar|grep -v "grep"|wc -l`
if [ $SERVICE_EUREKA_COUNT -eq 0 ];then
    echo `date "+%Y-%m-%d %H:%M:%S"` 'startup eureka server service' >> /home/wulin/Project/monitor/monitorservice.log
    cd /home/wulin/Project/eureka-server/server1
    $JAVA_HOME/bin/java -jar -Dspring.profiles.active=server1 /home/wulin/Project/eureka-server/server1/eureka-server.jar > /dev/null 2>&1 &
    cd /home/wulin/Project/eureka-server/server2
    $JAVA_HOME/bin/java -jar -Dspring.profiles.active=server2 /home/wulin/Project/eureka-server/server2/eureka-server.jar > /dev/null 2>&1 &
    echo `date "+%Y-%m-%d %H:%M:%S"` 'startup eureka server service finish ' >> /home/wulin/Project/monitor/monitorservice.log
else
    echo `date "+%Y-%m-%d %H:%M:%S"` $SERVICE_EUREKA_COUNT 'eureka server service normal running' >> /home/wulin/Project/monitor/monitorservice.log
fi


crontab config
*/30 * * * * sh /home/wulin/Project/monitor/monitorservice.sh



#!/bin/bash

ps -ef|grep api-gateway.jar|awk '{print $2}'|xargs kill -9

cd /home/wulin/Project/api-gateway/server1
nohup java -jar -Dserver.port=10011 /home/wulin/Project/api-gateway/server1/api-gateway.jar > /dev/null 2>&1 &

cd /home/wulin/Project/api-gateway/server2
nohup java -jar -Dserver.port=10012 /home/wulin/Project/api-gateway/server2/api-gateway.jar > /dev/null 2>&1 &