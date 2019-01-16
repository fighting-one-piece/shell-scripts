#!/bin/bash

cd /usr/local/nginx-1.11
NGINX_DIR="/usr/local/nginx"
NGINX_LOG_DIR="/usr/local/nginx/logs"
TIME=`date -d "-1 day" +%Y%m%d`

for LOG_FILE in `ls -l $NGINX_LOG_DIR|grep -v "^d"|awk '{print $9}'|grep ".log$"|grep -v "[_-]\{1\}[0-9]\{8\}.log"`;
do
    echo $LOG_FILE
    mv $NGINX_LOG_DIR/$LOG_FILE $NGINX_LOG_DIR/${LOG_FILE%.*}-$TIME.log
done;

$NGINX_DIR/sbin/nginx -s reload

crontab config
0 0 * * * sh /usr/local/nginx/scripts/logsplit.sh > /usr/local/nginx/scripts/logsplit.log