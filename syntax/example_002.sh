#!/bin/sh
factorial=1;
for var in $(seq 1 10);do
  echo "var is $var";
  factorial=$(expr $factorial \* $var);
done;
echo "10!=$factorial"
