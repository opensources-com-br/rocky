#!/bin/bash
set -euo pipefail

dmg_path="$(find apps/desktop/build/compose/binaries/main/dmg -name '*.dmg' -print -quit)"
mount_path="$(mktemp -d "${TMPDIR:-/tmp}/Rocky instalação.XXXXXX")"
cleanup() {
  hdiutil detach "$mount_path" -quiet >/dev/null 2>&1 || true
}
trap cleanup EXIT

hdiutil attach "$dmg_path" -mountpoint "$mount_path" -nobrowse -readonly -quiet
test -x "$mount_path/Rocky.app/Contents/MacOS/Rocky"
test -f "$mount_path/Rocky.app/Contents/runtime/Contents/Home/release"
test "$(plutil -extract LSUIElement raw -o - "$mount_path/Rocky.app/Contents/Info.plist")" = "true"
