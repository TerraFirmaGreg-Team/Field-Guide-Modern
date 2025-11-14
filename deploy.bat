@echo off
setlocal

:: Update Repo
git pull
if %errorlevel% neq 0 (
    echo ❌ Git pull failed
    exit /b 1
)

git submodule sync
if %errorlevel% neq 0 (
    echo ❌ Git submodule sync failed
    exit /b 1
)

git submodule update --force --init --depth=1 Modpack-Modern
if %errorlevel% neq 0 (
    echo ❌ Git submodule update failed
    exit /b 1
)

:: Build Project
call gradlew clean build
if %errorlevel% neq 0 (
    echo ❌ Gradle Build Failed
    exit /b 1
)

:: Fetch Mods
cd Modpack-Modern
if %errorlevel% neq 0 (
    echo ❌ Cannot enter Modpack-Modern directory
    exit /b 1
)

java -jar pakku.jar fetch
if %errorlevel% neq 0 (
    echo ❌ pakku.jar Fetch failed
    exit /b 1
)

:: Clean Output
cd ..
if exist output rmdir /s /q output

:: Build Field Guide TFG
for %%i in (build\libs\field-guide-tfg*.jar) do (
    java -jar "%%i" -i Modpack-Modern -o output
    goto :build_done
)

:build_done
if %errorlevel% neq 0 (
    echo ❌ Field Guide build failed
    exit /b 1
)

:: Congratulation
echo ✅ Build Success