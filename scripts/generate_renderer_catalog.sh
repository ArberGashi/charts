#!/usr/bin/env bash
set -euo pipefail

root_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$root_dir"

out_file="RENDERER_CATALOG.md"
tmp_file="$(mktemp)"
trap 'rm -f "$tmp_file"' EXIT

rg -n 'new RendererDescriptor' arbercharts-core/src/main/java/com/arbergashi/charts/render | \
  while IFS=: read -r file _line content; do
    if [[ "$content" =~ ^[[:space:]]*// ]]; then
      continue
    fi

    parts=$(printf '%s' "$content" | rg -o '"[^"]+"' | tr -d '"' | tr '\n' ' ')
    if [[ -z "$parts" ]]; then
      continue
    fi
    read -r id name_key icon_path _rest <<< "$parts"
    if [[ -z "${id:-}" || -z "${name_key:-}" || -z "${icon_path:-}" ]]; then
      continue
    fi

    case "$file" in
      */render/standard/*) category="STANDARD";;
      */render/financial/*) category="FINANCIAL";;
      */render/medical/*) category="MEDICAL";;
      */render/analysis/*) category="ANALYSIS";;
      */render/statistical/*) category="STATISTICAL";;
      */render/circular/*) category="CIRCULAR";;
      */render/specialized/*) category="SPECIALIZED";;
      *) category="OTHER";;
    esac

    printf '%s|%s|%s|%s\n' "$category" "$id" "$name_key" "$icon_path"
  done | sort -u > "$tmp_file"

{
  echo "# ArberCharts Renderer Catalog"
  echo
  echo "Generated from core renderer registrations."
  echo
  echo "Fields: id, nameKey (i18n key), iconPath."
  echo

  for category in STANDARD FINANCIAL MEDICAL STATISTICAL ANALYSIS CIRCULAR SPECIALIZED OTHER; do
    echo "## ${category}"
    echo
    echo "| id | nameKey | iconPath |"
    echo "|---|---|---|"
    found=0
    while IFS='|' read -r cat id name_key icon_path; do
      if [[ "$cat" == "$category" ]]; then
        printf '| `%s` | `%s` | `%s` |\n' "$id" "$name_key" "$icon_path"
        found=1
      fi
    done < "$tmp_file"
    if [[ $found -eq 0 ]]; then
      echo "| (none) |  |  |"
    fi
    echo
  done
} > "$out_file"

echo "Updated $out_file"
