#!/bin/bash
set -euo pipefail

: "${ROCKY_MAC_CERTIFICATE_BASE64:?Set the base64-encoded Developer ID certificate}"
: "${ROCKY_MAC_CERTIFICATE_PASSWORD:?Set the Developer ID certificate password}"
: "${ROCKY_MAC_KEYCHAIN:?Set the temporary signing keychain path}"
: "${ROCKY_MAC_KEYCHAIN_PASSWORD:?Set the temporary signing keychain password}"

rocky_certificate="$(mktemp "${TMPDIR:-/tmp}/rocky-developer-id.XXXXXX.p12")"
trap 'rm -f "$rocky_certificate"' EXIT

printf '%s' "$ROCKY_MAC_CERTIFICATE_BASE64" | base64 --decode > "$rocky_certificate"
security create-keychain -p "$ROCKY_MAC_KEYCHAIN_PASSWORD" "$ROCKY_MAC_KEYCHAIN"
security set-keychain-settings -lut 21600 "$ROCKY_MAC_KEYCHAIN"
security unlock-keychain -p "$ROCKY_MAC_KEYCHAIN_PASSWORD" "$ROCKY_MAC_KEYCHAIN"
security import "$rocky_certificate" -k "$ROCKY_MAC_KEYCHAIN" -P "$ROCKY_MAC_CERTIFICATE_PASSWORD" -A -t cert -f pkcs12
security set-key-partition-list -S apple-tool:,apple: -s -k "$ROCKY_MAC_KEYCHAIN_PASSWORD" "$ROCKY_MAC_KEYCHAIN"
security list-keychains -d user -s "$ROCKY_MAC_KEYCHAIN"
security find-identity -v -p codesigning "$ROCKY_MAC_KEYCHAIN" | grep -q 'Developer ID Application:'
