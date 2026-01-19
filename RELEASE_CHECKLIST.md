# ArberCharts Core Release Checklist

Use this list to validate a final release. All items are required unless marked optional.

## API and Documentation

- [x] Public API map reviewed and updated (`PUBLIC_API.md`)
- [x] Quickstart and usage guide updated (`USER_GUIDE.md`)
- [x] Renderer catalog regenerated (`RENDERER_CATALOG.md`)
- [x] Javadoc pass complete for public API

## Correctness and Stability

- [x] `mvn -pl arbercharts-core test` passes
- [x] i18n fallback and locale override tests pass
- [x] Data range and zero-range axis tests pass

## Quality Gates (Report-Only Baselines)

- [x] `scripts/update_quality_report.sh` run and `QUALITY_REPORT.md` updated
- [x] Visual hashes reviewed for drift
- [x] Performance baselines reviewed for drift

## Optional Strict Gates (CI)

- [ ] `scripts/compare_quality_baseline.sh` passes (strict compare)

## Packaging

- [x] `mvn -q -DskipTests package` passes at repo root
- [x] `mvn -pl arbercharts-core -Pguidelines-check verify` passes
