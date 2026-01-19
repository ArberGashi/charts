# Demo Packaging (JBR 25, Bundled Runtime)

This project ships the demo app with a bundled JetBrains Runtime 25.
Packaging uses `jpackage` and produces native installers per OS.

Requirements:
- JBR 25 JDK installed per target OS
- `JBR_HOME` set to the JBR JDK root
- Maven available on PATH

---

## macOS (DMG)

```bash
export JBR_HOME="/path/to/jbr-25"
./scripts/package_demo_macos.sh
```

Output: `dist/macos/`

---

## Windows (MSI)

```powershell
$env:JBR_HOME="C:\path\to\jbr-25"
.\scripts\package_demo_windows.ps1
```

Output: `dist/windows/`

---

## Linux (DEB)

```bash
export JBR_HOME="/path/to/jbr-25"
./scripts/package_demo_linux.sh
```

Output: `dist/linux/`

---

## Notes

- The demo uses `--enable-native-access=ALL-UNNAMED` to avoid JBR warnings.
- Icon files can be added later via `--icon` once platform-specific assets are ready.
