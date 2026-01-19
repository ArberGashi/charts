# Repository Guidelines

## Project Structure & Module Organization
- `arbercharts-core/` holds the core Swing charting framework (renderers, models, utilities) under `src/main/java` and JUnit tests under `src/test/java`.
- `arbercharts-demo/` is the showcase application with renderer panels, actions, and UI wiring in `src/main/java` plus assets in `src/main/resources/icons`.
- Root docs such as `ARCHITECTURE.md`, `TECH.md`, and `RENDERER_GUIDELINES.md` capture design constraints and renderer rules.
- Localization bundles live in `arbercharts-core/src/main/resources/i18n`.

## Build, Test, and Development Commands
- `mvn -q -DskipTests package` builds all modules from the root.
- `mvn -pl arbercharts-core test` runs core unit tests (JUnit Jupiter); perf-tagged tests are excluded by default.
- `mvn -pl arbercharts-core -Pperf test` runs performance-tagged tests when you need perf gates.
- `mvn -pl arbercharts-core -Pguidelines-check verify` enforces renderer guidelines via `RendererGuidelinesChecker`.
- `mvn -pl arbercharts-demo package` builds the demo app; run the shaded jar from `arbercharts-demo/target/` (example: `java --enable-native-access=ALL-UNNAMED -jar arbercharts-demo/target/arbercharts-demo-1.0.0.jar`).

## Coding Style & Naming Conventions
- Java sources follow standard 4-space indentation with package naming under `com.arbergashi.charts`.
- Renderer implementations live in `com.arbergashi.charts.render.*` with descriptive `*Renderer` names.
- Prefer immutable, allocation-free logic in render hot paths; see `RENDERER_GUIDELINES.md` and `TECH.md` for zero-GC expectations.

## Testing Guidelines
- Tests use JUnit Jupiter (`junit-jupiter`), named `*Test` or `*ContractTest`, and live beside their package in `src/test/java`.
- Performance tests are tagged `perf`; keep them isolated and deterministic.

## Commit & Pull Request Guidelines
- No formal commit message convention is declared in this repo; use concise, imperative summaries (example: `Add renderer registry cache guard`).
- PRs should include: a short rationale, impacted modules, test commands run, and screenshots or GIFs for demo UI changes.

## References & Architecture Notes
- Read `ARCHITECTURE.md` for component boundaries and `RENDERER_GUIDELINES.md` before adding or modifying renderers.
- `TECH.md` documents performance gates, renderer counts, and grid layer contracts.
