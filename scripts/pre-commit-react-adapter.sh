#!/usr/bin/env bash
# Pre-commit hook: regenerate React adapter when component metadata changes.
# Install: ln -sf ../../scripts/pre-commit-react-adapter.sh .git/hooks/pre-commit

set -e

# Only run if relevant files are staged
if git diff --cached --name-only | grep -qE '(components/.*/model\.cljs|exports/x_|generate_react\.bb|metadata\.bb|^package\.json$)'; then
  echo "[pre-commit] Regenerating React adapter..."
  bb scripts/generate_react.bb || { echo "[pre-commit] Failed to generate React adapter"; exit 1; }
  git add adapters/react/src/ adapters/react/package.json
  echo "[pre-commit] React adapter updated"

  echo "[pre-commit] Regenerating Angular adapter..."
  bb scripts/generate_angular.bb || { echo "[pre-commit] Failed to generate Angular adapter"; exit 1; }
  git add adapters/angular/src/ adapters/angular/package.json
  echo "[pre-commit] Angular adapter updated"
fi
