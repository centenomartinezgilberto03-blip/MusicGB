@echo off
echo Ejecutando Gradle Wrapper...
if exist "gradle\wrapper\gradle-wrapper.jar" (
    java -jar gradle\wrapper\gradle-wrapper.jar %*
) else (
    echo Error: gradle-wrapper.jar no encontrado
    echo Descargando Gradle wrapper...
    gradle wrapper --gradle-version 8.2
    java -jar gradle\wrapper\gradle-wrapper.jar %*
)
