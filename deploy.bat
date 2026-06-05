@echo off
setlocal EnableDelayedExpansion

:: Build static site from guide-export/ via :site (requires prior game export).
:: Env: EXPORT_GUIDE (default export\guide-export), SITE_OUTPUT (default output), SKIP_BUILD=1

set "EXPORT_GUIDE=%EXPORT_GUIDE%"
if not defined EXPORT_GUIDE set "EXPORT_GUIDE=export\guide-export"
set "SITE_OUTPUT=%SITE_OUTPUT%"
if not defined SITE_OUTPUT set "SITE_OUTPUT=output"

if not exist "%EXPORT_GUIDE%\manifest.json" (
    echo ❌ Missing %EXPORT_GUIDE%\manifest.json — run export first
    exit /b 1
)

if not "%SKIP_BUILD%"=="1" (
    call gradlew :site:jar
    if %errorlevel% neq 0 (
        echo ❌ Gradle :site:jar failed
        exit /b 1
    )
) else (
    echo SKIP_BUILD=1 — looking for existing site jar
)

set "SITE_JAR="
for %%i in (site\build\libs\field-guide-site-*.jar) do set "SITE_JAR=%%i"
if not defined SITE_JAR (
    echo ❌ No site jar under site\build\libs\ — run: gradlew :site:jar
    exit /b 1
)
if "%SKIP_BUILD%"=="1" echo Using !SITE_JAR!

if exist "%SITE_OUTPUT%" rmdir /s /q "%SITE_OUTPUT%"

java -jar "!SITE_JAR!" -e "%EXPORT_GUIDE%" -o "%SITE_OUTPUT%"
if %errorlevel% neq 0 (
    echo ❌ Site build failed
    exit /b 1
)

if exist "export\emi" (
    if exist "%SITE_OUTPUT%\emi" rmdir /s /q "%SITE_OUTPUT%\emi"
    xcopy /E /I /Y "export\emi" "%SITE_OUTPUT%\emi" >nul
)

echo ✅ Site built at %SITE_OUTPUT%
