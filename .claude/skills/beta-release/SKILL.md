---
name: beta-release
description: Build a jClipCorn beta jar (`make betaJar`) for the users who follow HEAD — bumps the 4th version digit in Main.java, sets BETA=true, builds `_mybuilds/jClipCorn X.Y.Z.N.jar` and copies it to the Kreios share. Use when asked for a beta, a beta jar/build, "make betaJar", or a new HEAD build. Not for a real/tagged release (that is the `release` skill).
---

# Beta jar (`make betaJar`)

A beta is just a jar of the current working tree with a fresh build number. No tag, no CHANGELOG, no push.

## How the version works

`Main.VERSION` (`src/main/de/jClipCorn/Main.java`, between the `gradle_version_marker` comments) is `X.Y.Z.N`:

- `X.Y.Z` = the last release; betas count from it (release `1.10.10` → betas `1.10.10.1–8` → release `1.10.11`). Commits meanwhile use the prefix of the *next* release (`1.10.11 …`), so `Main.VERSION` and the commit prefix normally differ by one patch.
- `N` = beta build number since then. `./gradlew betaJar` increments it and sets `BETA = true`.
- A release (see the `release` skill) leaves `X.Y.Z.0` and `BETA = false`; the first beta after it is `X.Y.Z.1`. No manual bump needed.

## Steps

1. **Read the state**: `Main.VERSION`, `BETA`, `git status --short`.

2. **Build**: `make betaJar` (runs `_setup/betajar.sh`). Use a long timeout (~10 min). It:
   - bumps `N`, sets `BETA = true`
   - builds `_mybuilds/jClipCorn X.Y.Z.N.jar`
   - copies the jar to `~/mounts/Melkor_NFS/Kreios/ClipCorn/` (or the `Melkor_WG` fallback) if mounted.

   Check `BUILD SUCCESSFUL` and that the jar exists. On a compile error, fix nothing on your own: report the error. Main.java has already been bumped at that point; leave it bumped (re-running will bump again, a skipped number is harmless).

3. **Copy**: inside the ccc container `~/mounts` does not exist, so the script prints "Skip copy" and the manual `cp` commands. Pass those on to the user (they run on the host). Do not try to mount anything.

4. **Main.java change**: the version bump is meant to be committed together with the change the beta was built for (every feature commit in the history carries its own bump).
   - User asked to commit as well → include `Main.java` in that commit, message format per the commit-msg hook (`X.Y.Z Feature: …`, prefix as used by recent commits).
   - Otherwise leave `Main.java` modified and tell the user it rides along with the next commit. Never revert it: the next beta would reuse the same number.

## Report

Version (`old → new`), jar path, whether it was copied or the `cp` command to run.
