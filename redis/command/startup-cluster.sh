#!/bin/bash

ps -ef|grep redis|awk '{print $2}'|xargs kill -9

rm -rf /home/wulin/Software/redis-3.2.3/dbs/6373/*
rm -rf /home/wulin/Software/redis-3.2.3/dbs/6374/*
rm -rf /home/wulin/Software/redis-3.2.3/dbs/6375/*
rm -rf /home/wulin/Software/redis-3.2.3/dbs/6376/*
rm -rf /home/wulin/Software/redis-3.2.3/dbs/6377/*
rm -rf /home/wulin/Software/redis-3.2.3/dbs/6378/*

rm -rf /home/wulin/Software/redis-3.2.3/cluster/conf/*

redis-server /home/wulin/Software/redis-3.2.3/cluster/6373/redis.conf
redis-server /home/wulin/Software/redis-3.2.3/cluster/6374/redis.conf
redis-server /home/wulin/Software/redis-3.2.3/cluster/6375/redis.conf
redis-server /home/wulin/Software/redis-3.2.3/cluster/6376/redis.conf
redis-server /home/wulin/Software/redis-3.2.3/cluster/6377/redis.conf
redis-server /home/wulin/Software/redis-3.2.3/cluster/6378/redis.conf

echo yes | /home/wulin/Software/redis-3.2.3/src/redis-trib.rb create --replicas 1 192.168.0.1:6373 192.168.0.1:6374 192.168.0.1:6375 192.168.0.1:6376 192.168.0.1:6377 192.168.0.1:6378
