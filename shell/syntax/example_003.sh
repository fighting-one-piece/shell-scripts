#!/bin/sh
function iterateFile(){
  for tmp in $1/*; do
    echo "current is $tmp"
    if [[ -d "$tmp" ]]; then
      echo "$tmp is directory";
      iterateFile $tmp
    elif [[ -f "$tmp" ]]; then
      echo "$tmp is file";
    else
      echo "$tmp error";
    fi;
  done;
}
iterateFile /tmp
