# Technical Specification: ArberCharts Core 1.0.0

Version: 1.0.0  
Target Platform: Java 25+  
Module: `arbercharts-core`

---

## Overview

ArberCharts Core is a high-performance Swing charting framework designed for zero-allocation rendering in hot paths. It provides a fluent API for assembling charts, a large renderer suite, theme-aware visuals, and export utilities for PNG/SVG/PDF (optional dependencies).

---

## Core Capabilities

- Fluent builder API (`ArberChartBuilder`)
- Multi-layer rendering with per-layer renderers
- Theme-driven styling (`ChartTheme`, `ChartThemes`)
- Domain-specific grid layers
- Interactive legend and high-precision crosshair
- Export services (PNG built-in; SVG/PDF optional)

---

## Renderer Suite

Renderer IDs, name keys, and icons are listed in `RENDERER_CATALOG.md`.
Registration is centralized in `RendererRegistry`.

---

## Performance Policy

- Zero-allocation `drawData` paths (enforced by `RendererGuidelinesChecker`)
- Primitive arrays for data flow
- Cached fonts/strokes and reusable shapes
- `ThreadLocal` buffers for coordinate mapping

---

## Testing & Quality Gates

- JUnit tests cover model contracts, renderers, UI behavior, and edge cases
- Renderer registry and data contract tests enforce stability
- Visual and performance baselines are recorded in `QUALITY_REPORT.md`

---

## Release Artifacts

- `PUBLIC_API.md` defines the stable API surface
- `RELEASE_CHECKLIST.md` captures final-release gates
- `QUALITY_REPORT.md` stores baseline hashes and timings
