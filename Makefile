
.PHONY: build betaJar anyReleaseJar changelog run-tests jformdesigner jfd \
        run run-prodcopy run-fresh run-debug run-prod-ro run-prod-rw

build:
	./gradlew build

run: run-prodcopy

run-local:
	./gradlew run -PrunWorkingDir="$(shell pwd)/_local" --args="-prev-cover-cache"

run-prodcopy:
	./gradlew run -PrunWorkingDir="$(HOME)/temp/jcc-prodcopy"

run-fresh:
	rm -rf   "$(HOME)/temp/jcc_freshcopy"
	mkdir -p "$(HOME)/temp/jcc_freshcopy"
	./gradlew run -PrunWorkingDir="$(HOME)/temp/jcc_freshcopy"

run-debug:
	./gradlew run -PrunWorkingDir="$(HOME)/temp/jcc_debug"

run-prod-ro:
	./gradlew run -PrunWorkingDir="$(HOME)/mounts/Melkor_NFS/Kreios/ClipCorn" --args="-ro"

run-prod-rw:
	./gradlew run -PrunWorkingDir="$(HOME)/mounts/Melkor_NFS/Kreios/ClipCorn"

validate-local-quick:
	./gradlew run -PrunWorkingDir="$(shell pwd)/_local" --args="--validate-db quick"


# create beta-release
# sets beta flag in Main.java and increases version number
# afterwards, copy the created jar to the Kreios NFS share (only if build succeeded, share is mounted and target dir exists)
betaJar:
	./gradlew betaJar
	@echo ""; \
	dest1="/home/mike/mounts/Melkor_NFS/Kreios/ClipCorn/"; \
	dest2="/home/mike/mounts/Melkor_WG/Kreios/ClipCorn/"; \
	mnt1="/home/mike/mounts/Melkor_NFS/Kreios"; \
	mnt2="/home/mike/mounts/Melkor_WG/Kreios"; \
	jar=$$(ls -t _mybuilds/*.jar 2>/dev/null | head -n1); \
	if mountpoint -q "$$mnt1" && [ -d "$$dest1" ]; then \
		echo "Copying '$$jar' to file://$$dest1"; \
		cp "$$jar" "$$dest1"; \
	elif mountpoint -q "$$mnt2" && [ -d "$$dest2" ]; then \
		echo "Copying '$$jar' to file://$$dest2"; \
		cp "$$jar" "$$dest2"; \
	else \
		echo "Skip copy: '$$mnt1'/'$$mnt2' not mounted or '$$dest1'/'$$dest2' does not exist"; \
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




