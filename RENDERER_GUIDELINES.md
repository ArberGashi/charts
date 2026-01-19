# Renderer Development Guidelines (Core 1.0.0)

These guidelines apply to renderers in `arbercharts-core`. Public API references are listed in `PUBLIC_API.md`.

## ⚡️ Performance Rules (STRICT)

Every renderer in `com.arbergashi.charts.render` must adhere to these rules. Violations will cause CI build failures.

### 1. Zero-Allocation Policy

- **FORBIDDEN**: `new Point2D(...)`, `new Rectangle2D(...)`, `new BasicStroke(...)`, `new Font(...)` inside `drawData`.
- **REQUIRED**: Use cached instances (fields) or pools.
    - Use `getPathCache()` from `BaseRenderer` for `Path2D`.
    - Use `pBuffer()` (`double[2]`) or `pBuffer4()` (`double[4]`) for coordinate mapping. Prefer the allocation-free
      `PlotContext.mapToPixel(x,y,dest)` API.
    - Cache `Stroke` and `Font` in member variables and update only on change.
    - For string/number formatting, avoid `String.format` or creating new `NumberFormat` instances in loops. Use the
      caching mechanisms provided in `AxisConfig` or `ChartUtils`.
    - Use `setupQualityHints(g2)` from `BaseRenderer` to apply consistent rendering hints (anti-aliasing, stroke
      control).

Note: A lightweight source scanner `RendererGuidelinesChecker` has been added to `arbercharts-core` to help detect
forbidden allocations in `drawData` methods. You can run it manually after building, or use the Maven profile included
for CI: `mvn verify -Pguidelines-check` (the profile runs the checker during the verify phase).

### 2. Data Access

- **FORBIDDEN**: Iterating via `Iterator` or `for(ChartPoint p : points)`.
- **REQUIRED**: Use indexed loops over primitive arrays.
  ```java
  double[] xData = model.getXData();
  int count = model.getPointCount();
  for (int i = 0; i < count; i++) { ... }
  ```

### 3. Coordinate Mapping

- **FORBIDDEN**: `context.mapToPixel(x, y)` returning a `Point2D`.
- **REQUIRED**: `context.mapToPixel(x, y, buffer)` writing to a `double[]`.

### 4. Algorithmic Complexity

- **GOAL**: O(N) for rendering.
- **AVOID**: Nested loops O(N^2) unless strictly limited (e.g., small window size).
- **OPTIMIZE**: Use sliding window algorithms for moving averages or envelopes.

---

## 🎨 Theme Integration (MANDATORY)

All renderers must be theme-aware and derive their styling from the active `ChartTheme`.

### 1. Color Access

- **FORBIDDEN**: Hardcoded colors like `new Color(255, 0, 0)` or `Color.RED` in render paths.
- **REQUIRED**: Use theme methods to obtain colors dynamically.
  ```java
  // In drawData or helper methods:
  ChartTheme theme = context.theme();
  Color lineColor = theme.getSeriesColor(seriesIndex);
  Color gridColor = theme.getGridColor();
  Color labelColor = theme.getAxisLabelColor();
  ```

### 2. Theme Contract

Every renderer must handle the case where `context.theme()` returns `null` gracefully:
```java
ChartTheme theme = context.theme();
if (theme == null) {
    theme = ChartThemes.defaultDark(); // Fallback
}
```

### 3. Dynamic Theme Switching

Renderers must not cache theme-derived colors across frames. Always re-read from `context.theme()` in each render cycle
to support live theme switching (Dark ↔ Light).

---

## 📐 Grid Layer Guidelines

Grid rendering is separated from data rendering. Renderers should NOT draw their own grids.

### 1. Grid Separation

- **FORBIDDEN**: Drawing grid lines inside renderer `drawData` methods.
- **REQUIRED**: Use the appropriate `GridLayer` via `ArberChartBuilder.withGridLayer(...)`.

### 2. Available Grid Layers

| Grid Layer | Use Case | Key Features |
|------------|----------|--------------|
| `DefaultGridLayer` | Standard charts | Adaptive spacing, major/minor lines |
| `MedicalGridLayer` | ECG, EEG, IBP | 1mm/5mm clinical grid, baseline emphasis |
| `FinancialGridLayer` | Candlestick, OHLC | Time-based verticals, price horizontals |
| `AnalysisGridLayer` | FFT, Autocorrelation | Scientific precision, frequency markers |

### 3. Grid Layer Selection

The Demo or application code selects the grid layer:
```java
ArberChartPanel panel = ArberChartBuilder.create()
    .addLayer(model, new ECGRenderer())
    .withGridLayer(new MedicalGridLayer())
    .build();
```

---

## 🏥 Medical Renderer Guidelines

Medical renderers have additional requirements for clinical-grade visualization.

### 1. Real-Time Performance

- **TARGET**: 60 FPS minimum for sweep-erase displays.
- **REQUIRED**: Use `CircularFastMedicalModel` for streaming data.
- **OPTIMIZE**: Implement gap detection for sweep-erase effect without full redraws.

### 2. Sweep-Erase Pattern

Medical sweep renderers should extend `AbstractMedicalSweepRenderer` which provides:
- Optimized circular buffer rendering
- Automatic gap calculation
- Configurable line color, stroke width, and gap size

### 3. Clinical Accuracy

- Use appropriate units (mV for ECG, mmHg for IBP, % for SpO2).
- Respect standard clinical color conventions where applicable.
- Ensure waveforms remain readable at standard paper speeds (25mm/s, 50mm/s for ECG).

---

## 🛠 Implementation Checklist

- [ ] Does the renderer extend `BaseRenderer`?
- [ ] Are all temporary objects (Shapes, Buffers) declared as `private final` fields?
- [ ] Is `drawData` free of `new` keywords? (Enforced by `RendererGuidelinesChecker`)
- [ ] Are heavy objects like `RadialGradientPaint` cached and only re-allocated in helper methods?
- [ ] Are `BasicStroke` and `Font` objects cached?
- [ ] Is string parsing (e.g., from properties) cached?
- [ ] Does it handle `count < 2` or empty data gracefully?
- [ ] Is the visual style configurable via `ChartAssets`?
- [ ] Are all colors derived from `ChartTheme`, not hardcoded?
- [ ] Does the renderer support both Dark and Light themes?
- [ ] Is grid rendering delegated to the appropriate `GridLayer`?

---

## 🧪 Testing

- Create a Microbenchmark test case.
- Verify no GC activity during a 10-second render loop.
- Test with both `ChartThemes.defaultDark()` and `ChartThemes.defaultLight()`.
- Verify graceful handling of edge cases (empty data, NaN values, single point).

---

## 🏛 Architectural Integrity

### 1. Separation of Concerns (Core vs. Demo)

- **STRICT**: The `arbercharts-core` module must not be modified to fix presentation issues in the `arbercharts-demo`
  module.
- **PRINCIPLE**: The Core provides high-performance infrastructure and flexible APIs. The Demo demonstrates how to use
  these APIs correctly.
- **GOAL**: Presentation quality ("Wow-Effect") in the Demo should be achieved through:
    - High-quality, realistic data models in `DemoPanelFactory`.
    - Correct configuration of existing public Core APIs (Themes, Grids, Tooltips).
    - Proper utilization of the Multi-Layer and Overlay system.
- **INTEGRITY**: Avoid "leaky abstractions" where demo-specific logic creeps into core renderers or UI components. If a
  feature is missing in the Core, it must be added as a generic, well-designed API, not as a hack for a single demo
  panel.

### 2. API Stability

- **PUBLIC API**: Classes in `com.arbergashi.charts.api` are stable and must maintain backward compatibility.
- **INTERNAL API**: Classes in `com.arbergashi.charts.internal` are not public and may change without notice.
- **RENDERER API**: New renderers must register via `RendererRegistry` with a unique ID.

### 3. Dependency Direction

```
Demo → Core (allowed)
Core → Demo (FORBIDDEN)
```

The Core must never import or reference Demo classes.

---

*ArberCharts Core Team — 2026*
