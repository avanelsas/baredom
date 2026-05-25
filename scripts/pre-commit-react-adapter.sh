#!/usr/bin/env bash
# Pre-commit hook: regenerate all adapters when component metadata changes.
# Install: ln -sf ../../scripts/pre-commit-react-adapter.sh .git/hooks/pre-commit

set -e

# Only run if relevant files are staged
if git diff --cached --name-only | grep -qE '(components/.*/model\.cljs|exports/x_|generate_(react|angular|vue|svelte|solid)\.bb|codegen_shared\.bb|metadata\.bb|form-control-metadata\.bb|^package\.json$)'; then
  echo "[pre-commit] Regenerating React adapter..."
  bb scripts/generate_react.bb || { echo "[pre-commit] Failed to generate React adapter"; exit 1; }
  git add adapters/react/src/ adapters/react/package.json
  echo "[pre-commit] React adapter updated"

  echo "[pre-commit] Regenerating Angular adapter..."
  bb scripts/generate_angular.bb || { echo "[pre-commit] Failed to generate Angular adapter"; exit 1; }
  git add adapters/angular/src/ adapters/angular/package.json
  echo "[pre-commit] Angular adapter updated"

  echo "[pre-commit] Regenerating Vue adapter..."
  bb scripts/generate_vue.bb || { echo "[pre-commit] Failed to generate Vue adapter"; exit 1; }
  git add adapters/vue/src/ adapters/vue/package.json
  echo "[pre-commit] Vue adapter updated"

  echo "[pre-commit] Regenerating Svelte adapter..."
  bb scripts/generate_svelte.bb || { echo "[pre-commit] Failed to generate Svelte adapter"; exit 1; }
  git add adapters/svelte/src/ adapters/svelte/package.json
  echo "[pre-commit] Svelte adapter updated"

  echo "[pre-commit] Regenerating Solid adapter..."
  bb scripts/generate_solid.bb || { echo "[pre-commit] Failed to generate Solid adapter"; exit 1; }
  git add adapters/solid/src/ adapters/solid/package.json
  echo "[pre-commit] Solid adapter updated"
fi
