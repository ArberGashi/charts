# ArberCharts Roadmap

This roadmap summarizes what shipped in 1.0.0 and the intended direction for follow-up releases.

---

## 1.0.0 (Final Release - Completed)

- Fluent API (`ArberChartBuilder`) as the primary entry point
- Stable public API map (`PUBLIC_API.md`)
- Theme-aware rendering with dark/light defaults
- Legend overlay and docked legend support
- High-precision crosshair
- Domain grid layers (default/medical/financial/analysis)
- Export service (PNG built-in; SVG/PDF optional)
- Quality gates: renderer guidelines, contract tests, baselines

---

## 1.1.x (Planned)

- Renderer name localization coverage (i18n keys for catalog entries)
- Expanded quickstart examples and recipe library
- Optional strict visual regression gate (hash compare)
- Additional performance baselines for large datasets

---

## Longer-Term (Exploration)

- Dedicated headless rendering helpers for server-side usage
- Additional chart-specific data model helpers
- More optional export targets (vector formats)

---

For current status and release gates, see `RELEASE_CHECKLIST.md`.
