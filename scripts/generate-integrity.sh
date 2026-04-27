#!/usr/bin/env bash
# Generate dist/integrity.json with SHA-384 SRI hashes for all JS files in dist/
set -euo pipefail

DIST_DIR="dist"
OUT="$DIST_DIR/integrity.json"
ALGO="sha384"
VERSION=$(node -p "require('./package.json').version")
TIMESTAMP=$(date -u +"%Y-%m-%dT%H:%M:%SZ")

if [ ! -d "$DIST_DIR" ]; then
  echo "Error: $DIST_DIR not found. Run 'npm run build' first." >&2
  exit 1
fi

# Start JSON
printf '{\n  "version": "%s",\n  "algorithm": "%s",\n  "generated-at": "%s",\n  "files": {\n' \
  "$VERSION" "$ALGO" "$TIMESTAMP" > "$OUT"

# Collect JS files, sorted
FILES=($(find "$DIST_DIR" -maxdepth 1 -name '*.js' -type f | sort))
TOTAL=${#FILES[@]}
COUNT=0

for FILE in "${FILES[@]}"; do
  BASENAME=$(basename "$FILE")
  HASH=$(openssl dgst -sha384 -binary "$FILE" | openssl base64 -A)
  COUNT=$((COUNT + 1))
  if [ "$COUNT" -lt "$TOTAL" ]; then
    printf '    "%s": "%s-%s",\n' "$BASENAME" "$ALGO" "$HASH" >> "$OUT"
  else
    printf '    "%s": "%s-%s"\n' "$BASENAME" "$ALGO" "$HASH" >> "$OUT"
  fi
done

printf '  }\n}\n' >> "$OUT"

echo "Generated $OUT ($TOTAL files, version $VERSION)"
