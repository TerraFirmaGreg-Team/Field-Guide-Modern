#!/usr/bin/env bash
# Post-export contract for guide-export/ (CLI input).
set -euo pipefail

GUIDE="${EXPORT_GUIDE:?EXPORT_GUIDE required}"

for f in manifest.json meta.json; do
  if [[ ! -f "$GUIDE/$f" ]]; then
    echo "::error::Missing $GUIDE/$f"
    exit 1
  fi
done

for d in assets data; do
  if [[ ! -d "$GUIDE/$d" ]]; then
    echo "::error::Missing directory $GUIDE/$d"
    exit 1
  fi
done

echo "guide-export OK: $GUIDE"
du -sh "$GUIDE" "$GUIDE/assets" "$GUIDE/data" 2>/dev/null || true

if [[ -d "${EXPORT_ROOT:?}/emi" ]]; then
  echo "emi bundle present: ${EXPORT_ROOT}/emi"
  du -sh "${EXPORT_ROOT}/emi" 2>/dev/null || true
fi
