#!/bin/sh
echo "please input number 1 to 10"
read number
case $number in
1|2|3)
  echo "the number you input is 1-3";;
4|5|6)
  echo "the number you input is 4-6";;
7|8|9|10)
  echo "the number you input is 7-10";;
*)
  echo "error! the number you input is not 1-10";;
esac;
