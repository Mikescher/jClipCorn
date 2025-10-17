
build:
	./gradlew build


# create release, first set the version and beta-flag in Main.java
anyReleaseJar:
	./gradlew anyReleaseJar


# create beta-release (sets beta flag in Main.java and increases version number)
betaJar:
	./gradlew betaJar


# create changelog
changelog:
	./gradlew changelog


run-tests:
	./gradlew run-tests

