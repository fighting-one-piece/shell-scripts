#!/bin/sh

url=$1
minsize=$2
bakname=$3      
filename=$4 
checkerror="is"$5

expression="500 Servlet Exception"

/usr/bin/wget ${url} -O ${bakname}
bsize=`ls -l ${bakname} | awk '{if ($5 > '$minsize') print "true"; else print "false" }'`

if (test $checkerror = "istrue")
then
  count=`/bin/grep -P -c "${expression}" ${bakname}`
  if (test $count != "0")
  then

    bsize="false"
  fi
fi


if (test $bsize = "true")
then
   /bin/cp ${bakname} ${filename}
fi
