#!/usr/bin/env bash
set -euo pipefail

SOURCE_SVG="${SOURCE_SVG:-arbercharts-demo/src/main/resources/icons/appicon.svg}"
OUTPUT_DIR="${OUTPUT_DIR:-docs/packaging/icons}"
TMP_DIR="${TMP_DIR:-/tmp/arbercharts-iconset}"

if ! command -v rsvg-convert >/dev/null 2>&1; then
  echo "rsvg-convert is required (e.g., brew install librsvg)." >&2
  exit 1
fi

mkdir -p "${OUTPUT_DIR}"
rm -rf "${TMP_DIR}"
mkdir -p "${TMP_DIR}"

sizes=(16 32 64 128 256 512 1024)
for size in "${sizes[@]}"; do
  rsvg-convert -w "${size}" -h "${size}" "${SOURCE_SVG}" -o "${TMP_DIR}/icon_${size}x${size}.png"
done

# macOS .icns
iconset="${TMP_DIR}/AppIcon.iconset"
mkdir -p "${iconset}"
cp "${TMP_DIR}/icon_16x16.png" "${iconset}/icon_16x16.png"
cp "${TMP_DIR}/icon_32x32.png" "${iconset}/icon_16x16@2x.png"
cp "${TMP_DIR}/icon_32x32.png" "${iconset}/icon_32x32.png"
cp "${TMP_DIR}/icon_64x64.png" "${iconset}/icon_32x32@2x.png"
cp "${TMP_DIR}/icon_128x128.png" "${iconset}/icon_128x128.png"
cp "${TMP_DIR}/icon_256x256.png" "${iconset}/icon_128x128@2x.png"
cp "${TMP_DIR}/icon_256x256.png" "${iconset}/icon_256x256.png"
cp "${TMP_DIR}/icon_512x512.png" "${iconset}/icon_256x256@2x.png"
cp "${TMP_DIR}/icon_512x512.png" "${iconset}/icon_512x512.png"
cp "${TMP_DIR}/icon_1024x1024.png" "${iconset}/icon_512x512@2x.png"

if command -v iconutil >/dev/null 2>&1; then
  if ! iconutil --convert icns --output "${OUTPUT_DIR}/appicon.icns" "${iconset}"; then
    echo "iconutil failed to generate .icns; leaving iconset in ${iconset}" >&2
  fi
else
  echo "iconutil not found; skipped macOS .icns generation." >&2
fi

# Linux PNG (512x512)
cp "${TMP_DIR}/icon_512x512.png" "${OUTPUT_DIR}/appicon.png"

# Windows ICO (optional)
if command -v png2ico >/dev/null 2>&1; then
  png2ico "${OUTPUT_DIR}/appicon.ico" \
    "${TMP_DIR}/icon_16x16.png" \
    "${TMP_DIR}/icon_32x32.png" \
    "${TMP_DIR}/icon_64x64.png" \
    "${TMP_DIR}/icon_128x128.png"
else
  echo "png2ico not found; skipped Windows .ico generation." >&2
fi

echo "Icons written to ${OUTPUT_DIR}"
