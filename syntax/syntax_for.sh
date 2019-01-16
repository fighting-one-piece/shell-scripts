#!/bin/sh
for i in `seq 1 10`;do
  echo "i: $i";
done;
for((j=1;j<10;j++));do
  echo "j: $j";
done;
for k in $(seq 1 10);do
  echo "k: $k";
done;
