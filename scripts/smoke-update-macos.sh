#!/bin/bash
# Run only on an isolated GitHub runner; never replace a developer's installation.
set -euo pipefail
[ "${GITHUB_ACTIONS:-}" = true ] && [ "${RUNNER_OS:-}" = macOS ]
fixture="$(mktemp -d "$RUNNER_TEMP/Rocky update smoke.XXXXXX")"
target="$HOME/Applications/Rocky.app"
[ ! -e "$target" ]
mkdir -p "$fixture/input" "$fixture/job" "$HOME/Applications"
result="$fixture/started.txt"
parent_pid=""
helper_pid=""
cleanup() {
  code=$?
  trap - EXIT
  touch "$fixture/job/cancel"
  if [ -n "$parent_pid" ]; then kill "$parent_pid" 2>/dev/null || true; fi
  if [ -n "$helper_pid" ]; then wait "$helper_pid" || true; fi
  if [ -f "$fixture/job/installation.log" ]; then tail -40 "$fixture/job/installation.log"; fi
  rm -rf "$target" "$fixture"
  exit "$code"
}
trap cleanup EXIT
cat > "$fixture/Main.java" <<'JAVA'
import java.nio.file.*;
public class Main {
    public static void main(String[] args) throws Exception {
        Files.writeString(Path.of(System.getProperty("rocky.smoke.result")), System.getProperty("rocky.version"));
    }
}
JAVA
"$JAVA_HOME/bin/javac" -d "$fixture/input" "$fixture/Main.java"
"$JAVA_HOME/bin/jar" --create --file "$fixture/input/fixture.jar" -C "$fixture/input" Main.class
for version in 1.0.1 1.0.2; do
  format=app-image
  if [ "$version" = 1.0.2 ]; then format=dmg; fi
  "$JAVA_HOME/bin/jpackage" --type "$format" --name Rocky --app-version "$version" \
    --input "$fixture/input" --main-jar fixture.jar --main-class Main --dest "$fixture/$version" \
    --mac-package-identifier dev.rocky.app --java-options "-Drocky.version=$version-alpha.1" \
    --java-options "'-Drocky.smoke.result=$result'" --add-modules java.base
done
ditto "$fixture/1.0.1/Rocky.app" "$target"
package="$fixture/1.0.2/Rocky-1.0.2.dmg"
hash="$(shasum -a 256 "$package" | awk '{print $1}')"
sleep 120 &
parent_pid=$!
bash shared/data/src/desktopMain/resources/updates/install-macos.sh "$package" "$target" "$parent_pid" \
  "$fixture/job" "$hash" 1.0.2 1.0.2-alpha.1 > "$fixture/job/installation.log" 2>&1 &
helper_pid=$!
deadline=$((SECONDS + 60))
until [ -f "$fixture/job/ready" ]; do
  kill -0 "$helper_pid"
  [ "$SECONDS" -lt "$deadline" ]
  sleep 0.2
done
grep -Fx 'java-options=-Drocky.version=1.0.1-alpha.1' "$target/Contents/app/Rocky.cfg"
kill "$parent_pid"
wait "$parent_pid" || true
parent_pid=""
wait "$helper_pid"
helper_pid=""
deadline=$((SECONDS + 30))
until [ -f "$result" ]; do [ "$SECONDS" -lt "$deadline" ]; sleep 0.2; done
[ "$(cat "$result")" = 1.0.2-alpha.1 ]
[ "$(cat "$fixture/job/status")" = installed ]
echo 'macOS native upgrade and automatic restart passed.'
