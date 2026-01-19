# ArberCharts Core Public API Map

This document defines the supported public API surface of `arbercharts-core`.
Anything outside this list is considered internal and may change without notice.

See `USER_GUIDE.md` for usage examples and `RENDERER_CATALOG.md` for renderer IDs.

---

## Supported Packages

### com.arbergashi.charts.api
Public interfaces, configuration objects, and builder API.
- `ArberChartBuilder` (fluent entry point)
- `AxisConfig`, `PlotContext`, `ChartTheme`, `ChartThemes`
- `ChartFocus`, `ChartFocusListener`, `TooltipMode`
- `ModelBuilder`, `ChartModelBinder`

### com.arbergashi.charts.model
Data models and point records used by renderers.
- `ChartModel`, `DefaultChartModel`, `FastMedicalModel`, `CircularFastMedicalModel`
- `DefaultMatrixChartModel`, `DefaultMultiVariateChartModel`, `DefaultMultiDimensionalChartModel`
- `ChartPoint`, `ErrorBarPoint`, `HierarchicalDataPoint`, `OHLCBar`
- `FlowChartModel`, `MatrixChartModel`, `MultiVariateChartModel`, `MultiDimensionalChartModel`, `HierarchicalChartModel`, `TernaryChartModel`

### com.arbergashi.charts.render
Renderer types and shared renderer infrastructure.
- `ChartRenderer`, `BaseRenderer`, `AxisRenderer`, `RendererCategory`, `RendererMetadata`
- All renderers under `com.arbergashi.charts.render.*` are public and supported.

### com.arbergashi.charts.ui
Swing UI components and export helpers.
- `ArberChartPanel`, `ChartTooltip`, `ChartExportService`, `ChartExportHandler`, `HighPrecisionCrosshair`

### com.arbergashi.charts.ui.grid
Grid layer implementations.
- `GridLayer`, `DefaultGridLayer`, `MedicalGridLayer`, `FinancialGridLayer`, `AnalysisGridLayer`

### com.arbergashi.charts.ui.legend
Legend UI configuration and components.
- `LegendConfig`, `LegendPlacement`, `LegendPosition`, `LegendDockSide`, `LegendDensity`
- `LegendActionListener`, `LegendChartContext`, `LegendSeriesRow`
- `InteractiveLegendOverlay`, `DockedLegendPanel`, `LayerVisibilityModel`

### com.arbergashi.charts.util
Utilities intended for public use.
- `ChartEngine`, `ChartScale`, `ChartAssets`, `FormatUtils`, `ColorUtils`, `NiceScale`

---

## Explicitly Internal (Not Supported)

- `com.arbergashi.charts.internal.*`
- `com.arbergashi.charts.tools.*`
- Demo-only packages in `arbercharts-demo`

---

## Fluent API Contract

- External applications should use `ArberChartBuilder` to assemble charts.
- Direct renderer usage is supported but not required for common workflows.
- Any missing configuration path in the fluent API is treated as a framework gap.

---

## Migration Notes

- No legacy `ChartPanel` is exposed; `ArberChartPanel` is the supported Swing component.
- Internal helpers (e.g., registry utilities) are not part of the stable API surface.

---

## Stability Policy

- Semantic versioning is used at the module level.
- Deprecated APIs remain for at least one minor version and include migration notes.
