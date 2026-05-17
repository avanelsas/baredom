#!/usr/bin/env bash
set -euo pipefail

# Bundle size budget check (gzipped bytes).
#
# BASE_BUDGET covers base.js — the shared runtime promoted by shadow-cljs
# whenever code is reachable from more than one module entry.
#
# COMPONENT_BUDGET is the default ceiling for an individual x-*.js
# module. It is calibrated for a single UI component.
#
# PER_MODULE_BUDGETS overrides the default for specific modules that
# legitimately need more headroom — currently only x-trace-history,
# the dev-only debugger dock. It ships an SVG timeline, scrubber,
# detail pane, sessions strip, import/export UI, the PR 17 causality
# DAG tree view, and the live-element highlight overlay, so 15 KB is
# the wrong ceiling for it. The dock is opt-in via ?baredom-trace-
# history and never runs in production builds where the flag stays off.
BASE_BUDGET=57344       # 56 KB for base.js
COMPONENT_BUDGET=15360  # 15 KB default for any x-*.js component

# Per-module overrides. Looked up in module_budget() below — case
# statement rather than an associative array so the script stays
# compatible with macOS's bash 3.2.
module_budget() {
  case "$1" in
    base.js)             echo "$BASE_BUDGET" ;;
    x-trace-history.js)  echo 24576 ;;  # 24 KB for the dev-tool dock
    *)                   echo "$COMPONENT_BUDGET" ;;
  esac
}

DIST_DIR="dist"
FAILED=0

if [ ! -d "$DIST_DIR" ]; then
  echo "ERROR: $DIST_DIR directory not found. Run 'npm run build' first."
  exit 1
fi

printf "\n%-40s %10s %10s %s\n" "Module" "Raw" "Gzipped" "Status"
printf "%-40s %10s %10s %s\n" "------" "---" "-------" "------"

for file in "$DIST_DIR"/*.js; do
  name=$(basename "$file")
  raw=$(wc -c < "$file" | tr -d ' ')
  gz=$(gzip -c "$file" | wc -c | tr -d ' ')

  budget=$(module_budget "$name")

  if [ "$gz" -gt "$budget" ]; then
    status="OVER BUDGET (max $(( budget / 1024 ))KB)"
    FAILED=1
  else
    status="ok"
  fi

  printf "%-40s %8s B %8s B %s\n" "$name" "$raw" "$gz" "$status"
done

echo ""
if [ "$FAILED" -eq 1 ]; then
  echo "FAIL: One or more modules exceed their size budget."
  exit 1
else
  echo "All modules within budget."
fi
