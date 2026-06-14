#!/bin/bash

# Script para exportar usuarios usando la API
# Uso: ./scripts/export-users.sh

echo "📊 Exportando usuarios..."

API_URL="${API_URL:-http://localhost:8080}"
OUTPUT_DIR="./exports"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
OUTPUT_FILE="$OUTPUT_DIR/usuarios_export_$TIMESTAMP.xlsx"

# Crear directorio de exports
mkdir -p $OUTPUT_DIR

# Verificar que el backend esté corriendo
echo "🔍 Verificando conexión con el backend..."
curl -s -o /dev/null -w "%{http_code}" $API_URL/test | grep -q "200"
if [ $? -ne 0 ]; then
    echo "❌ Error: No se puede conectar al backend en $API_URL"
    echo "Asegúrate de que el backend esté corriendo"
    exit 1
fi

# Exportar usuarios
echo "📥 Descargando archivo de usuarios..."
curl -s -o $OUTPUT_FILE "$API_URL/api/exportar/usuarios"

if [ -f "$OUTPUT_FILE" ] && [ $(stat -c%s "$OUTPUT_FILE") -gt 1000 ]; then
    echo "✅ Usuarios exportados exitosamente"
    echo "📁 Archivo guardado en: $OUTPUT_FILE"
    
    # Mostrar información del archivo
    echo ""
    echo "📊 Información del archivo:"
    ls -lh $OUTPUT_FILE
else
    echo "❌ Error al exportar usuarios"
    rm -f $OUTPUT_FILE
    exit 1
fi