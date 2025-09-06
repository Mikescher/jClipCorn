
build:
	./gradlew build


# create release/beta, depending on config in Main.java
anyReleaseJar:
	./gradlew anyReleaseJar


# create changelog
changelog:
	./gradlew changelog


run-tests:
	./gradlew run-tests

