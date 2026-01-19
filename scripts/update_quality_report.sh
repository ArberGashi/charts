#!/usr/bin/env bash
set -euo pipefail

root_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$root_dir"

tmp_log="$(mktemp)"
trap 'rm -f "$tmp_log"' EXIT

mvn -pl arbercharts-core test | tee "$tmp_log"

report_date="$(date +"%Y-%m-%d")"

perf_lines=$(rg "^PERF_BASELINE" "$tmp_log" | awk '{print "- " $2 ": " $3 " " $4}')
visual_lines=$(rg "^VISUAL_HASH" "$tmp_log" | awk '{print "- " $2 ": " $3 " (" $4 ")"}')

if [[ -z "${perf_lines}" || -z "${visual_lines}" ]]; then
  echo "Quality report update failed: missing PERF_BASELINE or VISUAL_HASH output." >&2
  exit 1
fi

cat <<EOF_REPORT > QUALITY_REPORT.md
# ArberCharts Core Quality Report

Generated from \
\`mvn -pl arbercharts-core test\` on ${report_date}.

## Performance Baseline (Report-Only)

Render time is the average per frame over 30 iterations (after warmup). These are reference values for regression tracking.

${perf_lines}

## Visual Hash Baseline (Report-Only)

SHA-256 hashes of rendered images (900x600, light theme). Use these as reference values for visual regression checks.

${visual_lines}
EOF_REPORT

echo "Updated QUALITY_REPORT.md"
