#!/usr/bin/env bash
set -euo pipefail

JBR_HOME="${JBR_HOME:?Set JBR_HOME to a JetBrains Runtime 25 JDK path}"
APP_NAME="${APP_NAME:-ArberCharts Demo}"
APP_VERSION="${APP_VERSION:-1.0.0}"
MAIN_JAR="${MAIN_JAR:-arbercharts-demo-1.0.0.jar}"
MAIN_CLASS="${MAIN_CLASS:-com.arbergashi.charts.Application}"
INPUT_DIR="${INPUT_DIR:-arbercharts-demo/target}"
OUTPUT_DIR="${OUTPUT_DIR:-dist/linux}"

mvn -pl arbercharts-demo -am package

mkdir -p "${OUTPUT_DIR}"

"${JBR_HOME}/bin/jpackage" \
  --type deb \
  --name "${APP_NAME}" \
  --app-version "${APP_VERSION}" \
  --input "${INPUT_DIR}" \
  --main-jar "${MAIN_JAR}" \
  --main-class "${MAIN_CLASS}" \
  --java-options "--enable-native-access=ALL-UNNAMED" \
  --dest "${OUTPUT_DIR}"
