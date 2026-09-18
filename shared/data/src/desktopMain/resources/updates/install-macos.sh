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
