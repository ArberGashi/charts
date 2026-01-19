# ArberCharts Core Final Release Strategy (Usability + Quality)

Goal: Make `arbercharts-core` a public, stable framework for external developers who only use the fluent API. Core must be reliable, predictable, well-documented, and competitive with JFreeChart/XChart on DX and correctness, while matching modern UX expectations (Bloomberg/SciChart-level polish).

Status: Completed for 1.0.0. Core is frozen; any changes require explicit approval.

See `RELEASE_CHECKLIST.md` for the final gate results.

---

## Guiding Principles

1. Public API only. Internal/demo classes are not part of the contract.
2. Zero-allocation in hot render paths stays non-negotiable.
3. Defaults must "just work" with clean output, sensible labels, and minimal setup.
4. Release gates are automated and repeatable (tests + benchmarks), not manual checks.

---

## Release Gates (Blocking)

1. Public API surface is complete and documented (Javadoc in US English).
2. Data range correctness for all `ChartModel` implementations (no backing array leakage).
3. Rendering math robustness (no NaN/Inf; zero-range axes are stable).
4. i18n and locale behavior is deterministic and tested.
5. Export pipeline is stable (PNG required; SVG/PDF optional with dependency checks).
6. Performance baselines are published and meet targets (FPS/GC/memory).
7. Visual regression coverage for core renderer classes.

---

## Phase 1: Public API Hardening (DX/Usability)

### A. Fluent API is the only supported entry point
- Ensure `ArberChartBuilder` can configure:
  - axis behavior (tick count, format, unit suffix, autoscale, locale)
  - grid visibility and density
  - legend placement and visibility
  - tooltip format and locale
  - export options (without UI dialogs)
- If any functionality requires `ChartPanel`, move it to `ArberChartPanel` or deprecate.

### B. De-dup UI components
- Decide on a single public chart panel class.
- If `ChartPanel` is legacy, deprecate and document migration to `ArberChartPanel`.

### C. i18n and locale
- Load bundles from `arbercharts-core/src/main/resources/i18n`.
- Provide explicit API for locale override (global or per-panel).
- Avoid hard-coded UI strings in core; route through i18n.

---

## Phase 2: Correctness & Stability

### A. `ChartModel` data range correctness
- Ensure `getDataRange()` uses `getPointCount()` and not full backing arrays.
- Add tests for:
  - backing array > point count
  - NaN/Inf handling
  - empty/1-point datasets

### B. Axis and coordinate math robustness
- Guard all divisions by zero in `ChartPanel` and `ArberChartPanel`.
- Ensure min == max produces stable output (range clamp).

### C. Threading policy
- Define contract: enforce EDT-only model updates or provide internal safe snapshotting.
- Add tests for model updates during repaint (basic race safety).

---

## Phase 3: Quality & Polish

### A. Axis formatting and unit integration
- `AxisConfig` must drive tick formatting and locale.
- `FormatUtils` should respect locale without penalizing hot paths.

### B. Legend UX and overlays
- Ensure legend is readable at compact sizes, with reliable visibility toggles.
- Provide consistent row naming and default values across renderers.

### C. Export API consistency
- Provide non-UI export methods only in core.
- If a chooser is needed, it belongs in the demo or an optional UI module.

---

## Phase 4: Competitive Proof (Market Readiness)

### A. Performance benchmarks
- Publish FPS/GC/memory baselines against:
  - JFreeChart (baseline feature parity)
  - XChart (lightweight charts)
  - SciChart-style UX expectations (smoothness, grid clarity)
- Document hardware and dataset sizes for reproducibility.

### B. Visual regression
- Golden-image renders for representative renderer set.
- Fail on pixel drift beyond defined tolerance.

### C. API stability policy
- Define semantic versioning rules.
- Document deprecation policy and compatibility window.

---

## Tests to Add (Minimum)

- i18n bundle load + fallback behavior.
- Locale formatting for axis labels and tooltips.
- `ChartModel#getDataRange()` on oversized backing arrays.
- Zero-range axis stability (min == max).
- Fluent API coverage tests for new config paths.
- Export smoke tests (PNG required, SVG/PDF optional).
- Renderer visual regression smoke tests (core set).
- Performance baseline test (non-failing, reports metrics).

---

## Deliverables

1. Public API map: supported vs deprecated, with migration notes.
2. Quickstart: fluent API examples for common chart types.
3. Renderer catalog: IDs, categories, descriptions, icons.
4. Release checklist with explicit pass/fail gates.
5. Performance + quality report (benchmarks + visual regression summary).

---

## Endspurt Checklist (Market-Ready)

1. Finish all missing public Javadoc and API comments.
2. Lock down core API surface and write migration notes.
3. Add/verify correctness tests (data range + zero-range axes + i18n).
4. Add visual regression baseline for priority renderers.
5. Publish benchmark harness and baseline results.
6. Confirm export pipeline reliability and error messages.
7. Final documentation pass (Quickstart + API map + renderer catalog).
8. Run full gates and sign-off.
