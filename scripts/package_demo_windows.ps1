param(
    [string]$JbrHome = $env:JBR_HOME,
    [string]$AppName = "ArberCharts Demo",
    [string]$AppVersion = "1.0.0",
    [string]$MainJar = "arbercharts-demo-1.0.0.jar",
    [string]$MainClass = "com.arbergashi.charts.Application",
    [string]$InputDir = "arbercharts-demo/target",
    [string]$OutputDir = "dist/windows"
)

if ([string]::IsNullOrWhiteSpace($JbrHome)) {
    throw "Set JBR_HOME to a JetBrains Runtime 25 JDK path."
}

mvn -pl arbercharts-demo -am package

New-Item -ItemType Directory -Force -Path $OutputDir | Out-Null

& "$JbrHome/bin/jpackage.exe" `
  --type msi `
  --name "$AppName" `
  --app-version "$AppVersion" `
  --input "$InputDir" `
  --main-jar "$MainJar" `
  --main-class "$MainClass" `
  --java-options "--enable-native-access=ALL-UNNAMED" `
  --dest "$OutputDir"
