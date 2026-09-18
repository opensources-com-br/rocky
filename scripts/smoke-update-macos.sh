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
