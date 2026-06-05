
.PHONY: build betaJar anyReleaseJar changelog run-tests jformdesigner jfd

build:
	./gradlew build


# create beta-release
# sets beta flag in Main.java and increases version number
# afterwards, copy the created jar to the Kreios NFS share (only if build succeeded, share is mounted and target dir exists)
betaJar:
	./gradlew betaJar
	@dest="/home/mike/mounts/Melkor_NFS/Kreios/ClipCorn/"; \
	mnt="/home/mike/mounts/Melkor_NFS/Kreios"; \
	jar=$$(ls -t _mybuilds/*.jar 2>/dev/null | head -n1); \
	if mountpoint -q "$$mnt" && [ -d "$$dest" ]; then \
		echo "Copying '$$jar' to file://$$dest"; \
		cp "$$jar" "$$dest"; \
	else \
		echo "Skip copy: '$$mnt' not mounted or '$$dest' does not exist"; \
	fi


# create release, first set the version and beta-flag in Main.java
# does not inc version number, build the currently specified version
anyReleaseJar:
	./gradlew anyReleaseJar


# create changelog
changelog:
	./gradlew changelog


run-tests:
	./gradlew run-tests


# generate java source from jfd files
# https://www.formdev.com/jformdesigner/doc/command-line
jformdesigner:
	./gradlew :compileJava
	@echo ''
	@echo ''
	@java -classpath "./_utils/jfd-8.3.1-b470/lib/JFormDesigner.jar" \
	     "com.jformdesigner.application.CommandLineMain"             \
		 --generate --recursive --verbose                            \
	     "./jClipCorn.jfdproj"                                       \
		 "./src/main/de/jClipCorn/gui/frames/"

jfd: jformdesigner




