#!/usr/local/bin/env bash

## Installs and restarts on my server in a hacky way

set -e 

version=0.0.8

for dir in /opt/ce-*; do
	app=$(basename "$dir")
	file="/home/tom/repository/com/runpaste/$app/$version/$app-$version.jar"
	if [ -e "$file" ]; then
		rm -f "/opt/$app/app.jar"
		ln -s "$file" "/opt/$app/app.jar"
		echo "Created /opt/$app/app.jar"
		systemctl daemon-reload
		for f in /etc/systemd/system/*-ce-client.service; do
			svc=$(basename "$f" | sed 's/\.service$//')
			>&2 echo "Restarting $svc"
			systemctl restart "$svc"
		done
	else
		f=$(basename "$file")
		>&2 echo "Skipping $f"
	fi
done

