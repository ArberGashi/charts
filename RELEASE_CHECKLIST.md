# Release Checklist (GitHub)

Use this for the final publication of ArberCharts.

## Repository
- Repo visibility set to **Private**
- `origin` remote points to the correct GitHub repo
- Tag created: `v1.0.0`
- Clean working tree (`git status` is clean)

## Documentation (Minimum)
- `USER_GUIDE.md` up to date
- `LICENSING.md` and `EULA_DRAFT.md` present
- `PRICING.md` present
- `PUBLIC_API.md` available

## Artifacts
- Core JAR: `arbercharts-core/target/arbercharts-core-1.0.0.jar`
- Demo JAR: `dist/release/arbercharts-demo-1.0.0.jar`
- macOS DMG: `dist/macos/ArberCharts Demo-1.0.0.dmg`
- (Optional) Windows MSI / Linux DEB built on respective OS

## Push & Release
- `git push -u origin main`
- `git push origin v1.0.0`
- Create GitHub Release `v1.0.0`
- Upload artifacts listed above
- Add release notes (marketing version)

## Post-Release
- Verify downloads and checksums
- Confirm demo runs with `--enable-native-access=ALL-UNNAMED`
- Confirm contact links (email + website)
