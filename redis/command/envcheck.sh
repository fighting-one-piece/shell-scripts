#!/bin/bash

echo "redis directory check"
CHECK_DIRS=("/home/wulin/Software/redis-3.2.3/pids" "/home/wulin/Software/redis-3.2.3/logs" "/home/wulin/Software/redis-3.2.3/dbs/6373" "/home/wulin/Software/redis-3.2.3/dbs/6374" "/home/wulin/Software/redis-3.2.3/dbs/6375" "/home/wulin/Software/redis-3.2.3/dbs/6376" "/home/wulin/Software/redis-3.2.3/dbs/6377" "/home/wulin/Software/redis-3.2.3/dbs/6378")

for DIR in ${CHECK_DIRS[*]}
do
    if [ ! -d $DIR ]
    then
        mkdir -p $DIR
    else
        echo "$DIR has existed! "
    fi
done 
