#!/usr/bin/env bash
# Optional: minecraft-web-export for scoped EMI (Phase 3). Same pattern as TFG-Recipe-Viewer.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
MP="${MODPACK_DIR:-$ROOT/Modpack-Modern}"
MWE_TAG="${MWE_TAG:-${MWE_VERSION:?MWE_VERSION or MWE_TAG required}}"
ver="${MWE_TAG#v}"
jar_name="minecraft-web-export-${ver}.jar"

cd "$ROOT"
rm -f minecraft-web-export-*.jar
gh release download "$MWE_TAG" \
  --repo jmecn/minecraft-web-export \
  --pattern "$jar_name" \
  --clobber

mkdir -p "$MP/mods"
find "$MP/mods" -maxdepth 1 -name 'minecraft-web-export*.jar' -delete

jar=$(ls minecraft-web-export-*.jar | head -1)
cp -v "$jar" "$MP/mods/"
