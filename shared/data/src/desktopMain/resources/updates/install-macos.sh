#!/bin/bash
set -euo pipefail
umask 077

package="$1"
target="$2"
parent_pid="$3"
job="$4"
expected_hash="$5"
native_version="$6"
release_version="$7"
mount_path=""
work=""
stopped=0
finished=0

status() {
  printf '%s\n' "$1" > "$job/status.tmp"
  /bin/mv -f "$job/status.tmp" "$job/status"
}

assert_exclusive_application() {
  local others
  others="$(/bin/ps -axww -o pid=,comm= | /usr/bin/awk \
    -v executable="$target/Contents/MacOS/Rocky" -v parent="$parent_pid" '
      { pid = $1; sub(/^[[:space:]]*[0-9]+[[:space:]]+/, "") }
      $0 == executable && pid != parent { print pid }
    ')"
  if [ -n "$others" ]; then
    echo 'Close other instances of this Rocky installation before updating.' >&2
    return 1
  fi
}

cleanup() {
  result=$?
  trap - EXIT
  if [ "$finished" -eq 0 ]; then
    if [ -n "$work" ] && [ -d "$work/Previous.app" ]; then
      /bin/rm -rf "$target"
      /bin/mv "$work/Previous.app" "$target" || true
    fi
    if [ -f "$job/cancel" ]; then status cancelled; else status failed; fi
    if [ "$stopped" -eq 1 ]; then /usr/bin/open "$target" || true; fi
  fi
  if [ -n "$mount_path" ]; then /usr/bin/hdiutil detach "$mount_path" -quiet || true; fi
  if [ -n "$work" ] && [ ! -d "$work/Previous.app" ]; then /bin/rm -rf "$work"; fi
  if [ "$finished" -eq 1 ] && [ -n "$work" ]; then /bin/rm -rf "$work"; fi
  exit "$result"
}
trap cleanup EXIT
trap 'exit 1' INT TERM
trap 'printf "Updater failed at line %s\n" "$LINENO" >&2' ERR
printf '%s\n' "$$" > "$job/helper.pid"

case "$target" in
  /Applications/Rocky.app|"$HOME/Applications/Rocky.app") ;;
  *) echo 'Rocky must be installed in Applications.' >&2; exit 1 ;;
esac
[[ "$parent_pid" =~ ^[0-9]+$ ]] && [ "$parent_pid" -gt 1 ]
[[ "$native_version" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]
[[ "$expected_hash" =~ ^[a-f0-9]{64}$ ]]
[ ! -L "$target" ] && [ ! -L "$package" ] && [ -f "$package" ]
[ -w "$(/usr/bin/dirname "$target")" ]
[ "$(/usr/bin/shasum -a 256 "$package" | /usr/bin/awk '{print $1}')" = "$expected_hash" ]
[ "$(/usr/bin/plutil -extract CFBundleIdentifier raw -o - "$target/Contents/Info.plist")" = 'dev.rocky.app' ]

mount_path="$(/usr/bin/mktemp -d "$job/mounted.XXXXXX")"
/usr/bin/hdiutil attach "$package" -mountpoint "$mount_path" -nobrowse -readonly -quiet
source_app="$mount_path/Rocky.app"
[ ! -L "$source_app" ] && [ -x "$source_app/Contents/MacOS/Rocky" ]
[ "$(/usr/bin/plutil -extract CFBundleIdentifier raw -o - "$source_app/Contents/Info.plist")" = 'dev.rocky.app' ]
[ "$(/usr/bin/plutil -extract CFBundleShortVersionString raw -o - "$source_app/Contents/Info.plist")" = "$native_version" ]
/usr/bin/grep -Fx -- "java-options=-Drocky.version=$release_version" "$source_app/Contents/app/Rocky.cfg"

old_team="$(/usr/bin/codesign -dv --verbose=4 "$target" 2>&1 | /usr/bin/awk -F= '/^TeamIdentifier=/{print $2}' || true)"
if [ -n "$old_team" ] && [ "$old_team" != 'not set' ]; then
  /usr/bin/codesign --verify --deep --strict "$source_app"
  new_team="$(/usr/bin/codesign -dv --verbose=4 "$source_app" 2>&1 | /usr/bin/awk -F= '/^TeamIdentifier=/{print $2}')"
  [ "$old_team" = "$new_team" ]
fi

work="$(/usr/bin/mktemp -d "$(/usr/bin/dirname "$target")/.Rocky-update.XXXXXX")"
/usr/bin/ditto "$source_app" "$work/Rocky.app"
if [ -n "$old_team" ] && [ "$old_team" != 'not set' ]; then
  /usr/bin/codesign --verify --deep --strict "$work/Rocky.app"
fi
/usr/bin/hdiutil detach "$mount_path" -quiet
/bin/rmdir "$mount_path"
mount_path=""
[ ! -f "$job/cancel" ]
assert_exclusive_application
status ready
/usr/bin/touch "$job/ready"

deadline=$((SECONDS + 120))
while /bin/kill -0 "$parent_pid" 2>/dev/null; do
  [ ! -f "$job/cancel" ]
  [ "$SECONDS" -lt "$deadline" ]
  /bin/sleep 0.2
done
[ ! -f "$job/cancel" ]
stopped=1
status installing
/bin/mv "$target" "$work/Previous.app"
/bin/mv "$work/Rocky.app" "$target"
status installed
/usr/bin/open "$target"
finished=1
