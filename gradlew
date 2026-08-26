#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"

# Allows `./gradlew <task>` from the repository root on Windows Git Bash.
export JAVA_HOME="C:/Program Files/Microsoft/jdk-21.0.12.101-hotspot"
export PATH="$JAVA_HOME/bin:$PATH"

cd "$ROOT_DIR/backend"
exec bash ./gradlew "$@"
