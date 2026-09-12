#!/bin/bash
# Build with ROCKY_MAC_SIGN=true and an installed Developer ID identity first.
set -euo pipefail
: "${ROCKY_NOTARY_PROFILE:?Set a notarytool keychain profile containing your Apple credentials}"
rocky_root="$(cd "$(dirname "$0")/.." && pwd)"
rocky_app="$rocky_root/apps/desktop/build/compose/binaries/main/app/Rocky.app"
codesign --verify --deep --strict "$rocky_app"
# An ad-hoc signature is not a distributable Developer ID signature.
codesign -dv "$rocky_app" 2>&1 | grep -q 'Authority=Developer ID Application:'
shopt -s nullglob
rocky_packages=("$rocky_root"/apps/desktop/build/compose/binaries/main/dmg/*.dmg)
[ "${#rocky_packages[@]}" -eq 1 ] || { echo 'Expected exactly one candidate DMG.' >&2; exit 1; }
for rocky_package in "${rocky_packages[@]}"; do
    xcrun notarytool submit "$rocky_package" --keychain-profile "$ROCKY_NOTARY_PROFILE" --wait --timeout 15m
    xcrun stapler staple "$rocky_package"
    xcrun stapler validate "$rocky_package"
done
python3 "$rocky_root/scripts/release_metadata.py"
