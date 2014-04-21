#!/bin/sh
cat /dev/null > /home/nginx/logs/*.163.com_access.log
cat /dev/null > /home/nginx/logs/*.163.com_error.log

cat /dev/null > /home/resin/log/gather.log
rm /home/resin/log/gather.log.*
cat /dev/null > /home/resin/log/mobile_access.log
rm /home/resin/log/mobile_access.log.*
cat /dev/null > /home/resin/log/jvm.log
cat /dev/null > /home/resin/log/stderr.log
cat /dev/null > /home/resin/log/stdout.log
rm /home/workspace/gather/logs/schedule/gather.log.*

cat /dev/null > /var/mail/web
cat /dev/null > /var/log/rsync.log
cat /dev/null > /var/log/inotify_rsync.log
cat /dev/null > /var/log/inotify_monitor.log
