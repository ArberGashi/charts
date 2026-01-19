#!/usr/bin/env bash
set -euo pipefail

root_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$root_dir"

if [[ ! -f QUALITY_REPORT.md ]]; then
  echo "Missing QUALITY_REPORT.md baseline file." >&2
  exit 1
fi

tmp_log="$(mktemp)"
trap 'rm -f "$tmp_log"' EXIT

: "${MVN_CMD:=mvn -pl arbercharts-core test}"
: "${PERF_MAX_PCT:=20}"
: "${PERF_MAX_MS:=1.0}"

$MVN_CMD | tee "$tmp_log"

# Parse baselines from QUALITY_REPORT.md
mapfile -t perf_base_lines < <(awk '
  /^## Performance Baseline/ {state=1; next}
  /^## / {if(state==1) state=0}
  state==1 && /^- / {print}
' QUALITY_REPORT.md)

mapfile -t hash_base_lines < <(awk '
  /^## Visual Hash Baseline/ {state=2; next}
  /^## / {if(state==2) state=0}
  state==2 && /^- / {print}
' QUALITY_REPORT.md)

declare -A perf_base
for line in "${perf_base_lines[@]}"; do
  name=$(printf '%s' "$line" | awk -F': ' '{gsub(/^- /, "", $1); print $1}')
  value=$(printf '%s' "$line" | awk -F': ' '{print $2}' | awk '{print $1}')
  if [[ -n "$name" && -n "$value" ]]; then
    perf_base[$name]="$value"
  fi
done

declare -A hash_base
for line in "${hash_base_lines[@]}"; do
  name=$(printf '%s' "$line" | awk -F': ' '{gsub(/^- /, "", $1); print $1}')
  value=$(printf '%s' "$line" | awk -F': ' '{print $2}')
  if [[ -n "$name" && -n "$value" ]]; then
    hash_base[$name]="$value"
  fi
done

# Parse current run output
mapfile -t perf_curr_lines < <(rg "^PERF_BASELINE" "$tmp_log")
mapfile -t hash_curr_lines < <(rg "^VISUAL_HASH" "$tmp_log")

declare -A perf_curr
for line in "${perf_curr_lines[@]}"; do
  name=$(printf '%s' "$line" | awk '{print $2}')
  value=$(printf '%s' "$line" | awk '{for(i=1;i<=NF;i++){if($i ~ /^avg_ms=/){sub("avg_ms=","",$i); print $i}}}')
  if [[ -n "$name" && -n "$value" ]]; then
    perf_curr[$name]="$value"
  fi
done

declare -A hash_curr
for line in "${hash_curr_lines[@]}"; do
  name=$(printf '%s' "$line" | awk '{print $2}')
  value=$(printf '%s' "$line" | awk '{print $3}')
  if [[ -n "$name" && -n "$value" ]]; then
    hash_curr[$name]="$value"
  fi
done

fail=0

# Compare perf baselines
for name in "${!perf_base[@]}"; do
  base=${perf_base[$name]}
  curr=${perf_curr[$name]-}
  if [[ -z "$curr" ]]; then
    echo "PERF_MISSING_CURRENT $name" >&2
    fail=1
    continue
  fi
  if awk -v b="$base" 'BEGIN{exit (b==0.0 ? 0 : 1)}'; then
    delta=$(awk -v c="$curr" -v b="$base" 'BEGIN{print (c-b)}')
    if awk -v d="$delta" -v max="$PERF_MAX_MS" 'BEGIN{exit (d>max ? 1 : 0)}'; then
      echo "PERF_REGRESSION $name base=${base}ms curr=${curr}ms (+${delta}ms > ${PERF_MAX_MS}ms)" >&2
      fail=1
    fi
  else
    pct=$(awk -v c="$curr" -v b="$base" 'BEGIN{print ((c-b)/b)*100.0}')
    if awk -v p="$pct" -v max="$PERF_MAX_PCT" 'BEGIN{exit (p>max ? 1 : 0)}'; then
      printf "PERF_REGRESSION %s base=%sms curr=%sms (+%.2f%% > %s%%)\n" "$name" "$base" "$curr" "$pct" "$PERF_MAX_PCT" >&2
      fail=1
    fi
  fi
done

# Compare visual hashes
for name in "${!hash_base[@]}"; do
  base=${hash_base[$name]}
  curr=${hash_curr[$name]-}
  if [[ -z "$curr" ]]; then
    echo "HASH_MISSING_CURRENT $name" >&2
    fail=1
    continue
  fi
  if [[ "$curr" != "$base" ]]; then
    echo "HASH_MISMATCH $name base=${base} curr=${curr}" >&2
    fail=1
  fi
done

if [[ $fail -ne 0 ]]; then
  echo "Quality baseline comparison failed." >&2
  exit 2
fi

echo "Quality baseline comparison passed."
