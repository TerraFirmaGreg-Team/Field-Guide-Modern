#!/usr/bin/env bash
# Emit comma-separated locale codes from Field-Guide-Modern :core Language enum.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

chmod +x gradlew
csv="$(./gradlew :core:printExportLanguages --no-daemon -q)"

if [[ -z "$csv" ]]; then
  echo "::error:::core:printExportLanguages returned empty list" >&2
  exit 1
fi

if [[ -n "${GITHUB_OUTPUT:-}" ]]; then
  echo "export_languages=${csv}" >> "$GITHUB_OUTPUT"
fi

echo "Export languages (Language enum): ${csv}"
