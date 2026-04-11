#!/usr/bin/env bash
set -euo pipefail

# Bundle size budget check (gzipped bytes)
BASE_BUDGET=51200    # 50 KB for base.js
COMPONENT_BUDGET=15360  # 15 KB for any x-*.js component

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

  if [ "$name" = "base.js" ]; then
    budget=$BASE_BUDGET
  else
    budget=$COMPONENT_BUDGET
  fi

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
