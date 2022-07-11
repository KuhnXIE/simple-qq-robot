#!/bin/bash
ps aux |grep shr25-qq-robot.jar |grep -v "grep" >/dev/null
if [ $? -eq 0 ];then
  echo "qq-robot is running"
  shrzhrpid=$(ps aux |grep shr25-qq-robot.jar |grep -v "grep"|awk '{print $2}')
  if [ x"$shrzhrpid" != x ]; then
	kill -9 $shrzhrpid
  fi
  echo "shr25-qq-robot already stoped"
else
  echo "shr25-qq-robot is stoped"
fi

nohup java -Dloader.path=lib,plugins -jar shr25-qq-robot.jar > qq.log 2>&1  &

tail -f qq.log