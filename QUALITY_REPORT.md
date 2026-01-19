# ArberCharts Core Quality Report

Generated from `mvn -pl arbercharts-core test` on 2026-01-19.

## Performance Baseline (Report-Only)

Render time is the average per frame over 30 iterations (after warmup). These are reference values for regression tracking.

- line: avg_ms=3.241 frames=30
- scatter: avg_ms=3.900 frames=30
- histogram: avg_ms=0.509 frames=30
- candlestick: avg_ms=5.594 frames=30

## Visual Hash Baseline (Report-Only)

SHA-256 hashes of rendered images (900x600, light theme). Use these as reference values for visual regression checks.

- line: ca60365aabe713a698e8f108eeb49e67bbe23f30acaa7e5c468083b2573b1c0c (900x600)
- area: d301edaa30d645a9b134ad8e7114e7360c1f852b1023afa7af7fbd0dc9e68e6c (900x600)
- bar: 894d796d5b3b3c5b1aade2f3015bf898a7f7e118d218badb57c5c3ab9e12feff (900x600)
- scatter: 1c32147c6ebde490d2272effe8f684e815b0a511be4ef643508783182b100ba5 (900x600)
- candlestick: c03b34a683be6d22d125624a525c379272c9c47e5aec20055b33f7516a7e3d4d (900x600)
- histogram: dfd9cbb89a16a2591f3ad4da4e5b11a67fa71a208c484c9f05a8a1d4690bdc64 (900x600)
- pie: e65dbafd46ad11b4b4bbba46b935c434f141b4548add020b6b001b1462150c72 (900x600)
- heatmap: 8d38d06944c654764f37b64e7159c241f3fb2fc1300430cad93b1dd9c570dd15 (900x600)
