---
name: release
description: Make a real jClipCorn release (`make manualReleaseJar`) — tag vX.Y.Z, set Main.VERSION to X.Y.Z.0 with BETA=false, build the release jar, regenerate CHANGELOG.txt, create the `X.Y.Z RELEASE` commit and prepare the GitHub release notes. Use when asked to "make a v1.11.2 release", "release 1.12.0", cut/publish/tag a release or run manualReleaseJar. Not for beta/HEAD builds (that is the `beta-release` skill).
---

# Release (`make manualReleaseJar`)

Input: the release version `V = X.Y.Z` from the prompt (strip a leading `v`). No version given → ask.

## What a release consists of (from the history, e.g. `10c8479f 1.10.11 RELEASE`)

- One commit, message exactly `V RELEASE`, changing only `Main.java` (`VERSION = "V.0"`, `BETA = false`) and `CHANGELOG.txt`.
- `CHANGELOG.txt` contains a `#### <date>  V  RELEASE ####` header above the entries since the previous release. The header is generated from the RELEASE commit itself, so the changelog is regenerated after committing and amended in.
- Lightweight tag `vV` on that commit.
- Jar `_mybuilds/jClipCorn V.0.jar`, uploaded to a GitHub release `jClipCorn vV` (GitHub turns the spaces into dots: `jClipCorn.V.0.jar`).

The RELEASE commit message gets **no body and no trailers** (no Co-Authored-By etc.): `createChangelog` copies every line of a release commit's message into CHANGELOG.txt.

## 1. Preconditions — stop and report if any fails

```bash
git branch --show-current            # master
git status --short                   # clean
git tag -l "vV"; git ls-remote --tags origin "refs/tags/vV"   # both empty
git log -1 --format=%s --grep='^[0-9]\+\.[0-9]\+\.[0-9]\+ RELEASE$'   # previous release, e.g. "1.11.0 RELEASE"
```

- Dirty tree: if the only change is a pending beta bump of the version marker in `Main.java`, that is fine (it gets overwritten); anything else → ask whether to commit or stash it first.
- `V` must be greater than the previous release.
- The expected `V` is the prefix of the commits since the previous release: commits always carry the version of the *upcoming* release (commits `1.10.11 …` → `1.10.11 RELEASE`). Get it with `git log --format=%s <prev-release>..HEAD | awk '{print $1}' | sort | uniq -c`. Read obvious typos as what they mean (`10.11.1` = `1.11.1`, `10.10.12` = `1.10.12`).
  - `V` equals that prefix → the normal case.
  - `V` is a minor/major jump above it (commits `1.10.12`, released as `1.11.0`) → fine, that happened before.
  - anything else (e.g. commits `1.11.1`, asked `1.11.2`) → ask once which version is meant, showing the prefix and the previous release.
- `Main.VERSION` (`X.Y.Z.N`) counts betas from the previous release (`1.10.10.8` → release `1.10.11`), so it lags one patch behind `V`. `V` must be strictly greater than `X.Y.Z`: the jar `V.0` has to sort after every beta jar already shipped. If not (betas `1.11.1.1–14` with commits `1.11.1`), stop and propose `V = X.Y.(Z+1)` with the commit prefixes rewritten to `V` (filter-branch `--msg-filter`, keeps dates; needs a force push), and let the user decide.

## 2. Tests

`make run-tests` (Docker, takes a few minutes; timeout 600000). Failing tests → stop and report. Docker unavailable → ask whether to release without tests.

## 3. Build

```bash
git tag "vV"                 # manualReleaseJar reads the version via `git describe --tags`
make manualReleaseJar
```

Check the output: `Version (Tag):  V.0`, `BUILD SUCCESSFUL`. Then verify:
- `Main.java`: version marker `"V.0"`, `BETA = false`
- `_mybuilds/jClipCorn V.0.jar` exists and is fresh
- "Running Launch4j" produces no `.exe` on Linux; that is expected (recent releases ship only the jar).

If the build fails: `git tag -d "vV"`, `git checkout -- src/main/de/jClipCorn/Main.java CHANGELOG.txt`, report.

## 4. Commit, changelog, tag

The commit-msg hook regex is case-sensitive and rejects `V RELEASE`; the user has to approve `--no-verify` for these two commits.

```bash
git add src/main/de/jClipCorn/Main.java CHANGELOG.txt
git commit --no-verify -m "V RELEASE"
git show HEAD~1:CHANGELOG.txt > /tmp/cl_old.txt
make changelog                       # now includes the RELEASE header
```

`make changelog` regenerates the whole file and also rewrites the headers of older releases (dates/widths differ from the committed file). Old parts of the changelog must stay untouched: keep only the new `V RELEASE` section (from the top down to the line before the previous release's `####` block) and append `/tmp/cl_old.txt` unchanged. Check with `git diff HEAD~1 -- CHANGELOG.txt`: insertions only, no `-` lines.

```bash
git add CHANGELOG.txt
git commit --no-verify --amend --no-edit
git tag -f "vV"                      # move the tag from the pre-release commit to the RELEASE commit
```

Check: `head -5 CHANGELOG.txt` shows the `V RELEASE` header; `git show --stat HEAD` touches only those two files and CHANGELOG.txt has insertions only; `git tag --points-at HEAD` lists `vV`.

Look at the entries of this release (header down to the next `####` block). Their prefixes should equal `V` (or the pre-jump version on a minor/major jump, like the `1.10.12 …` lines in 1.11.0); point out others, e.g. typos like `10.11.1` for `1.11.1`. Only report them, do not rewrite history.

## 4b. Copy to the Kreios share

`manualReleaseJar` does not copy the jar (only `_setup/betajar.sh` does). Copy `_mybuilds/jClipCorn V.0.jar` like that script: to `~/mounts/Melkor_NFS/Kreios/ClipCorn/` if `Melkor_NFS/Kreios` is a mountpoint, else to `~/mounts/Melkor_WG/Kreios/ClipCorn/`. If neither is mounted, give the user the `cp` command.

## 5. Release notes

Write `_mybuilds/release-notes-vV.md` (`_mybuilds/` is gitignored), same format as earlier GitHub releases:

```
# Changelog
~~~
<every line of this release's section in CHANGELOG.txt, without the #### header>
~~~
```

## 6. Publish — the user does this

The container has no push credentials and no `gh`, so do not push. Give the user:

```bash
git push origin master "vV"
```

and the GitHub release to create at https://github.com/Mikescher/jClipCorn/releases/new: tag `vV`, title `jClipCorn vV`, body = the notes file, asset = `_mybuilds/jClipCorn V.0.jar`.

## After the release

The next beta (skill `beta-release`) turns `V.0` into `V.1`. Commits after the release use the prefix `X.Y.(Z+1)` (the next release). Never bump the betas to `X.Y.(Z+1).N` by hand.

## Report

Release version, commit hash, tag, jar path, notes file path, suspicious changelog prefixes, and the publish commands from step 6.
