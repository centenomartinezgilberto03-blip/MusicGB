#!/bin/sh
# Gradle start up script for POSIX

# Intenta encontrar gradle
if [ -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    echo "Gradle wrapper encontrado"
else
    echo "Descargando Gradle wrapper..."
    # Usar gradle del sistema o descargar
    if command -v gradle &> /dev/null; then
        gradle wrapper --gradle-version 8.2
    else
        echo "Error: Gradle no encontrado"
        exit 1
    fi
fi

# Ejecutar gradle wrapper
./gradlew "$@"
