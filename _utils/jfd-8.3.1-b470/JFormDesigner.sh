#!/bin/sh
#
# JFormDesigner startup script for Unix/Linux
#

JFD_MAX_MEM_SIZE=512m
JFD_VM_PARAMS=

JFD_JAVA_HOME=""
JFD_JAVA_HOME_VAR=""
if [ -n "$JFORMDESIGNER_JAVA_HOME" ]; then
	JFD_JAVA_HOME="$JFORMDESIGNER_JAVA_HOME"
	JFD_JAVA_HOME_VAR=JFORMDESIGNER_JAVA_HOME
elif [ -d "jre" ]; then
	JFD_JAVA_HOME=`readlink -f jre`
elif [ -n "$JAVA_HOME" ]; then
	JFD_JAVA_HOME="$JAVA_HOME"
	JFD_JAVA_HOME_VAR=JAVA_HOME
elif [ -n "$JDK_HOME" ]; then
	JFD_JAVA_HOME="$JDK_HOME"
	JFD_JAVA_HOME_VAR=JDK_HOME
fi

if [ -z "$JFD_JAVA_HOME" ]; then
	JFD_JAVA_EXEC=`which java`
else
	JFD_JAVA_EXEC="$JFD_JAVA_HOME/bin/java"
fi

if [ -n "$JFD_JAVA_EXEC" ] && [ ! -x "$JFD_JAVA_EXEC" ]; then
	echo "$JFD_JAVA_EXEC does not exist or is not executable."
	echo
	JFD_JAVA_EXEC=""
fi

if [ -z "$JFD_JAVA_EXEC" ]; then
	if [ -n "$JFD_JAVA_HOME_VAR" ]; then
		echo "Please validate that environment variable $JFD_JAVA_HOME_VAR points"
		echo "to a valid JRE installation folder (e.g. /home/charly/jre-11)."
		echo "Current value of $JFD_JAVA_HOME_VAR is \"$JFD_JAVA_HOME\"."
	else
		echo "No Java Virtual Machine (JVM) could be found on your system."
		echo
		echo "Please install Java JDK or JRE."
		echo
		echo "If necessary, you can define JFORMDESIGNER_JAVA_HOME, JAVA_HOME or JDK_HOME"
		echo "environment variables to point to a valid JRE installation folder"
		echo "(e.g. JFORMDESIGNER_JAVA_HOME=/home/charly/jre-11)."
	fi
	echo
	echo "Press Enter to continue."
	read IGNORE
	exit 1
fi

JFD_SCRIPT=$0
if [ -L "$JFD_SCRIPT" ]; then
	JFD_SCRIPT=`readlink -f "$JFD_SCRIPT"`
fi

JFD_HOME=`dirname "$JFD_SCRIPT"`

"$JFD_JAVA_EXEC" -Xmx$JFD_MAX_MEM_SIZE --add-opens java.desktop/javax.swing=ALL-UNNAMED --enable-native-access=ALL-UNNAMED $JFD_VM_PARAMS -jar "$JFD_HOME/lib/JFormDesigner.jar" "$@"
