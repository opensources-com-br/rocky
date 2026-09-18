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
