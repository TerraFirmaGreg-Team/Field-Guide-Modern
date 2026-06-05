#!/usr/bin/env bash
# Write export locale list from Language enum, then read it as one string for CI / JVM.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

chmod +x gradlew

lang_file="$ROOT/build/export-languages.txt"

# Gradle logs go to the terminal; the locale list is written to a file only.
./gradlew writeExportLanguagesFile --no-daemon -q --console=plain >/dev/null

if [[ ! -s "$lang_file" ]]; then
  echo "::error::Missing $lang_file after writeExportLanguagesFile" >&2
  exit 1
fi

# Single line, no parsing — fed whole to -Dfieldguide.exportLanguages=...
csv="$(tr -d '\n\r' < "$lang_file")"

if [[ -n "${GITHUB_OUTPUT:-}" ]]; then
  {
    echo "export_languages<<EOF"
    echo "$csv"
    echo "EOF"
  } >> "$GITHUB_OUTPUT"
fi

echo "Export languages (Language enum): ${csv}"
