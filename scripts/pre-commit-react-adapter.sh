#!/usr/bin/env bash
# Pre-commit hook: regenerate React adapter when component metadata changes.
# Install: ln -sf ../../scripts/pre-commit-react-adapter.sh .git/hooks/pre-commit

set -e

# Only run if relevant files are staged
if git diff --cached --name-only | grep -qE '(components/.*/model\.cljs|exports/x_|generate_react\.bb|metadata\.bb|^package\.json$)'; then
  echo "[pre-commit] Regenerating React adapter..."
  bb scripts/generate_react.bb
  git add adapters/react/src/ adapters/react/package.json
fi
