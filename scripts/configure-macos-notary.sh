#!/bin/bash
set -euo pipefail

: "${ROCKY_APPLE_ID:?Set the Apple ID used for notarization}"
: "${ROCKY_APPLE_TEAM_ID:?Set the Apple Developer Team ID}"
: "${ROCKY_APPLE_APP_PASSWORD:?Set an app-specific Apple ID password}"
: "${ROCKY_MAC_KEYCHAIN:?Set the temporary signing keychain path}"
: "${ROCKY_MAC_KEYCHAIN_PASSWORD:?Set the temporary signing keychain password}"
: "${ROCKY_NOTARY_PROFILE:?Set the temporary notarytool profile name}"

security unlock-keychain -p "$ROCKY_MAC_KEYCHAIN_PASSWORD" "$ROCKY_MAC_KEYCHAIN"
xcrun notarytool store-credentials "$ROCKY_NOTARY_PROFILE" \
  --apple-id "$ROCKY_APPLE_ID" \
  --team-id "$ROCKY_APPLE_TEAM_ID" \
  --password "$ROCKY_APPLE_APP_PASSWORD" \
  --keychain "$ROCKY_MAC_KEYCHAIN" \
  --validate
