#!/usr/bin/env bash
# Launch modpack under xvfb; minecraft-web-export CI driver writes guide-export/ + emi/.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
MP="${MODPACK_DIR:-$ROOT/Modpack-Modern}"
EXPORT_ROOT="${EXPORT_ROOT:?EXPORT_ROOT required}"
HMC_VER="${HMC_VERSION:?HMC_VERSION required}"
launcher="headlessmc-launcher-${HMC_VER}.jar"

mkdir -p "$MP/config" "$MP/saves" "$EXPORT_ROOT"
cp -f "$ROOT/ci/config/export-fml.toml" "$MP/config/fml.toml"
cp -f "$ROOT/ci/config/export-forge-client.toml" "$MP/config/forge-client.toml"
cat > "$MP/options.txt" <<EOF
onboardAccessibility:false
pauseOnLostFocus:false
EOF

cd "$ROOT"
xvfb-run --server-args="-screen 0 1280x720x24" -a java \
  -Dhmc.check.xvfb=true \
  -jar "$launcher" \
  --command "launch .*forge.* -regex --jvm \"${MWE_JVM_FLAGS:?MWE_JVM_FLAGS required}\""

bash "$ROOT/scripts/ci-verify-guide-export.sh"
