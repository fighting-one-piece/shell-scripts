#!/bin/sh
echo "select one"
select entry in "linux" "centos" "redhat";do
    break;
done
echo "you have selected $entry"

