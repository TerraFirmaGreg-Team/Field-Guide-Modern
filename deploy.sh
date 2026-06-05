#!/usr/bin/env bash
#
# Build static site from guide-export/ via :site (requires prior game export).
#
# Env:
#   EXPORT_GUIDE   guide-export root (default: export/guide-export)
#   SITE_OUTPUT    output dir (default: ./output)
#   SKIP_BUILD=1   reuse site/build/libs/field-guide-site-*.jar
#
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT"

EXPORT_GUIDE="${EXPORT_GUIDE:-export/guide-export}"
SITE_OUTPUT="${SITE_OUTPUT:-output}"

if [[ ! -f "$EXPORT_GUIDE/manifest.json" ]]; then
  echo "❌ Missing $EXPORT_GUIDE/manifest.json — run export first (see scripts/ci-launch-mc-export.sh)"
  exit 1
fi

find_site_jar() {
  local -a jars=()
  shopt -s nullglob
  jars=( site/build/libs/field-guide-site-*.jar )
  shopt -u nullglob
  if ((${#jars[@]} == 0)); then
    return 1
  fi
  ls -t "${jars[@]}" | head -1
}

SITE_JAR=""
if [[ "${SKIP_BUILD:-0}" == "1" ]]; then
  SITE_JAR="$(find_site_jar || true)"
  if [[ -z "$SITE_JAR" ]]; then
    echo "❌ SKIP_BUILD=1 but no site jar under site/build/libs/ (run: ./gradlew :site:jar)"
    exit 1
  fi
  echo "SKIP_BUILD=1 — using $SITE_JAR"
else
  ./gradlew :site:jar || {
    echo "❌ Gradle :site:jar failed"
    exit 1
  }
  SITE_JAR="$(find_site_jar || true)"
  if [[ -z "$SITE_JAR" ]]; then
    echo "❌ Site jar missing after build (expected site/build/libs/field-guide-site-*.jar)"
    exit 1
  fi
fi

rm -rf "$SITE_OUTPUT"
site_args=(-e "$EXPORT_GUIDE" -o "$SITE_OUTPUT")
if [[ -d "${EXPORT_ROOT:-export}/emi" ]]; then
  site_args+=(--emi-dir "${EXPORT_ROOT:-export}/emi")
fi
java -jar "$SITE_JAR" "${site_args[@]}"

if [[ -d "${EXPORT_ROOT:-export}/emi" ]]; then
  rm -rf "${SITE_OUTPUT}/emi"
  cp -a "${EXPORT_ROOT:-export}/emi" "${SITE_OUTPUT}/emi"
fi

echo "✅ Site built at $SITE_OUTPUT"
