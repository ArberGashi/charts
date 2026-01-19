# ArberCharts Architecture Overview (Core 1.0.0)

ArberCharts is a high-performance Swing charting framework built for zero-allocation rendering. This document describes the module boundaries and architectural contracts.

---

## 1) Package Structure

- `com.arbergashi.charts.api`
  - Public API: builder, theme, axis config, plot context.
- `com.arbergashi.charts.model`
  - Data models and point records.
- `com.arbergashi.charts.render`
  - Renderers, renderer registry, shared renderer helpers.
- `com.arbergashi.charts.ui`
  - Swing components, tooltips, export services, crosshair.
- `com.arbergashi.charts.ui.grid`
  - Grid layer implementations.
- `com.arbergashi.charts.ui.legend`
  - Legend configuration and UI components.
- `com.arbergashi.charts.util`
  - Scaling, formatting, theming helpers.

See `PUBLIC_API.md` for the full stable surface area.

---

## 2) Module Boundaries

- Core (`arbercharts-core`) is the framework runtime.
- Demo (`arbercharts-demo`) is a showcase application and is not part of the API contract.
- Internal helpers (`com.arbergashi.charts.internal`) are not public and may change without notice.
- Tools (`com.arbergashi.charts.tools`) are for QA/checks and are not public API.

---

## 3) Rendering Architecture

- `ArberChartPanel` coordinates rendering, overlays, tooltips, and legends.
- Renderers extend `BaseRenderer` and implement `drawData(...)`.
- `PlotContext` provides coordinate transforms and theme access.
- Grid rendering is separated via `GridLayer`.

---

## 4) Performance Principles

- No allocations in `drawData` (enforced by `RendererGuidelinesChecker`).
- Primitive arrays for data flow.
- Reusable shapes and cached fonts/strokes.
- Thread-local buffers for coordinate mapping.

---

## 5) Theming and i18n

- Themes are provided via `ChartTheme` and `ChartThemes`.
- `ChartI18N` provides locale-aware strings with a default locale override.
- UIManager can supply defaults when using FlatLaf.

---

## 6) Export

`ChartExportService` provides PNG by default. SVG/PDF are optional and resolved via reflection if the dependencies are present.

---

## 7) Quality Gates

- Renderer guidelines (`RendererGuidelinesChecker`)
- Renderer registry uniqueness tests
- Data contract tests for renderers
- Visual and perf baselines (`QUALITY_REPORT.md`)
