#!/bin/bash
# ============================================================
# Usage : ./import_sql.sh <fichier.sql>
# Exemple : ./import_sql.sh data/v2/delete_all.sql
# ============================================================

set -e

# --- Configuration ---
CONTAINER="tomefy-oracle-11-sql"
DB_USER="ARBIOCHEMSTANDARD"
DB_PASS="arbiochemstandard"
DB_HOST="127.0.0.1"
DB_PORT="1521"
DB_SERVICE="EE.oracle.docker"

# --- Vérifications ---
if [ -z "$1" ]; then
    echo "Usage: $0 <fichier.sql>"
    exit 1
fi
FILE="$1"

if [ ! -f "$FILE" ]; then
    echo "❌ Fichier introuvable : $FILE"
    exit 1
fi

if ! podman ps --format '{{.Names}}' | grep -q "^${CONTAINER}$"; then
    echo "❌ Le conteneur $CONTAINER n'est pas demarre"
    exit 1
fi

# --- Execution directe par stdin (PAS de podman cp) ---
echo "=== Execution de $(basename "$FILE") ==="

podman exec -i "${CONTAINER}" \
    sqlplus -S "${DB_USER}/${DB_PASS}@//${DB_HOST}:${DB_PORT}/${DB_SERVICE}" \
    < "$FILE"

echo "=== Termine : $(basename "$FILE") ==="