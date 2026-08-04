#!/usr/bin/env bash
#
# Arma los paquetes de entrega para Windows y Linux a partir de un despliegue.
#
#   ./armar-paquetes.sh [origen] [salida]
#       origen  carpeta con los jars, lib/ y db/   (por defecto /Bingo)
#       salida  donde dejar los paquetes           (por defecto /Bingo)
#
# Los lanzadores, instaladores y LEEME salen de este mismo directorio, que es
# la fuente de verdad del empaquetado: antes vivian sueltos en una carpeta
# temporal y se perdian al terminar la sesion.
#
set -eu

AQUI="$(cd "$(dirname "$0")" && pwd)"
ORIGEN="${1:-/Bingo}"
SALIDA="${2:-/Bingo}"
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT

for f in Pantalla-Universal.jar Generador-Universal.jar Config-Universal.jar Reporte-Universal.jar; do
    [ -f "$ORIGEN/$f" ] || { echo "ERROR: falta $ORIGEN/$f"; exit 1; }
done
[ -d "$ORIGEN/lib" ] || { echo "ERROR: falta $ORIGEN/lib"; exit 1; }
[ -f "$AQUI/plantilla/tablas.db" ] || { echo "ERROR: falta $AQUI/plantilla/tablas.db"; exit 1; }

armar() {                       # $1 = windows|linux
    local so="$1" dest="$TMP/$1/Bingo"
    mkdir -p "$dest/lib" "$dest/db"
    cp "$ORIGEN"/*.jar "$dest"/
    cp "$ORIGEN"/lib/*.jar "$dest"/lib/

    # db: SIN licencia.lic (cada equipo se activa) y SIN config.ker (arranca
    # limpio; el programa lo crea con sus valores por defecto al primer uso).
    #
    # tablas.db NO se copia del despliegue: se entrega la PLANTILLA VACIA. La
    # del despliegue lleva las tablas de quien haya estado probando aqui, y
    # mandarlas a un cliente seria entregarle un reparto que no es el suyo
    # —incluido el amaño, si se genero en modo Personalizada—. La plantilla
    # conserva credenciales, licencia y figuras; solo va sin tablas.
    cp "$AQUI/plantilla/tablas.db" "$dest/db/"
    for f in matriz.txt mensajes_figuras.cfg; do
        [ -f "$ORIGEN/db/$f" ] && cp "$ORIGEN/db/$f" "$dest/db/"
    done

    if [ "$so" = "windows" ]; then
        cp "$AQUI"/windows/*.bat "$AQUI"/windows/LEEME.txt "$dest"/
        # los .bat necesitan fin de linea CRLF
        for f in "$dest"/*.bat "$dest"/LEEME.txt; do sed -i 's/\r*$/\r/' "$f"; done
    else
        cp "$AQUI"/linux/*.sh "$AQUI"/linux/LEEME.txt "$dest"/
        chmod +x "$dest"/*.sh
    fi
    echo "  $so: $(find "$dest" -type f | wc -l) archivos"
}

echo "Armando desde $ORIGEN"
armar windows
armar linux

rm -f "$SALIDA/Bingo-Windows.zip" "$SALIDA/Bingo-Linux.tar.gz"
( cd "$TMP/windows" && zip -qr "$SALIDA/Bingo-Windows.zip" Bingo )
( cd "$TMP/linux"   && tar czf "$SALIDA/Bingo-Linux.tar.gz" Bingo )

echo
echo "Listo:"
ls -la "$SALIDA/Bingo-Windows.zip" "$SALIDA/Bingo-Linux.tar.gz" | awk '{printf "  %-40s %10s bytes\n", $9, $5}'
echo
echo "Recordar: el generador de claves (GenLic.zip) NO va en estos paquetes."
