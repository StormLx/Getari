#!/bin/bash
function echo_bold_green {
    echo -e "\033[32;1m"$1"\033[0m";
}
DESTDIR=/var/www/html/getari/
COLOR="\033[31;1;7m"
SERVER=147.100.20.95
echo -e "Are you sure to deploy GETARI website on $COLOR$SERVER\033[0m? (y/N)"
read -p "" -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Deployment is cancelled!";
        exit 0;
fi
echo_bold_green "Cleaning public/..."
rm -fr public/
echo_bold_green "Building site..."
hugo
echo_bold_green "Launching Hugo server..."
hugo server &
cp -r php/* public/
sleep 1
echo_bold_green "Checking links..."
linkchecker http://localhost:1313/getari/ &&
	echo "Sync..." &&
	rsync -avz --delete --copy-links public/ $SERVER:$DESTDIR
echo_bold_green "Killing hugo..."
pkill hugo
