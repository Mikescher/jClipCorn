
build:
	./gradlew build


# create beta-release
# sets beta flag in Main.java and increases version number
betaJar:
	./gradlew betaJar


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




