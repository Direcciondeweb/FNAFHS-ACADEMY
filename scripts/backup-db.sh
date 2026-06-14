#!/bin/bash

# Script para hacer backup de la base de datos
# Uso: ./scripts/backup-db.sh

echo "💾 Iniciando backup de la base de datos..."

# Configuración
BACKUP_DIR="./database/backups"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="$BACKUP_DIR/fnafhs_db_backup_$TIMESTAMP.sql"

# Crear directorio de backups si no existe
mkdir -p $BACKUP_DIR

# Verificar si hay variables de entorno o usar valores por defecto
if [ -f ".env" ]; then
    source .env
fi

# Configuración de la base de datos (cambiar según tu entorno)
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-3306}"
DB_NAME="${DB_NAME:-fnafhs_db}"
DB_USER="${DB_USER:-root}"
DB_PASSWORD="${DB_PASSWORD:-}"

echo "📁 Backup guardado en: $BACKUP_FILE"

# Crear backup
if [ -z "$DB_PASSWORD" ]; then
    mysqldump -h $DB_HOST -P $DB_PORT -u $DB_USER $DB_NAME > $BACKUP_FILE
else
    mysqldump -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASSWORD $DB_NAME > $BACKUP_FILE
fi

if [ $? -eq 0 ]; then
    echo "✅ Backup completado exitosamente"
    
    # Comprimir backup
    gzip $BACKUP_FILE
    echo "🗜️ Backup comprimido: ${BACKUP_FILE}.gz"
    
    # Eliminar backups antiguos (más de 30 días)
    find $BACKUP_DIR -name "*.gz" -mtime +30 -delete
    echo "🧹 Backups antiguos eliminados"
else
    echo "❌ Error al crear el backup"
    exit 1
fi