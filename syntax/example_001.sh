#!/bin/sh
t=0
j=0
d=0
PS="please choose one of three result or quit"
select choice in Yes No Reset Quit;do
case $choice in
Yes)
  t=$t+1
  if (($t==5));then
    echo "Yes"
    break
  fi
;;
No)
  j=$j+1
  if (($j==5));then
    echo "No"
    break;
  fi
;;
Quit)
  exit 0;;
*)
  echo "$REPLY is invalide choice" 1>&2
  echo "Try agai!"
;;
esac
done
