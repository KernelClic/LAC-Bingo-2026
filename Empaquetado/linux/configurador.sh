#!/usr/bin/env bash
# Bingo - Configurador
DIR="$(cd "$(dirname "$0")" && pwd)"
if ! command -v java >/dev/null 2>&1; then
    echo
    echo "  No se encontro Java en este equipo."
    echo "  Instale Java 8 o superior y vuelva a intentar."
    echo
    exit 1
fi
if [ "$DIR" != "/Bingo" ]; then
    echo
    echo "  ATENCION: esta carpeta esta en  $DIR"
    echo "  El programa busca sus datos en  /Bingo/db"
    echo "  Muevala a /Bingo o no encontrara la base de datos."
    echo
fi
cd "$DIR" && exec java -jar Config-Universal.jar "$@"
