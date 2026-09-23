#!/bin/bash

echo ""

dest1="/home/mike/mounts/Melkor_NFS/Kreios/ClipCorn/"
dest2="/home/mike/mounts/Melkor_WG/Kreios/ClipCorn/"
mnt1="/home/mike/mounts/Melkor_NFS/Kreios"
mnt2="/home/mike/mounts/Melkor_WG/Kreios"

jar=$(ls -t _mybuilds/*.jar 2>/dev/null | head -n1);

if mountpoint -q "$mnt1" && [ -d "$dest1" ]; then
	echo "Copying '$jar' to file://$dest1"
	cp "$jar" "$dest1"
elif mount "$dest2" && mountpoint -q "$mnt2" && [ -d "$dest2" ]; then
	echo "Copying '$jar' to file://$dest2"
	cp "$jar" "$dest2"
else
	echo "Skip copy: '$mnt1'/'$mnt2' not mounted or '$dest1'/'$dest2' does not exist"
	echo "Run manually:"
	echo " - [A] mount \"$dest1\" && cp \"$jar\" \"$dest1\""
	echo " - [B] mount \"$dest2\" && cp \"$jar\" \"$dest2\""
fi