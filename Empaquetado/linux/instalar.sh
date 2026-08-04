#!/usr/bin/env bash
#
# Instalador de Bingo para Linux.
#
#   ./instalar.sh              instala en /Bingo (la ruta que espera el programa)
#   BINGO_DIR=/otra ./instalar.sh   instala en otra ruta (solo para pruebas:
#                                   el programa NO funcionara fuera de /Bingo)
#
# Reinstalar es seguro: los datos de db/ (tablas, licencia y configuracion) se
# conservan siempre. Solo se reemplazan los programas y las librerias.
#
set -u

DESTINO="${BINGO_DIR:-/Bingo}"
ORIGEN="$(cd "$(dirname "$0")" && pwd)"
USUARIO="$(id -un)"

echo
echo "=============================================================="
echo "  Instalacion de Bingo"
echo "=============================================================="
echo "  origen : $ORIGEN"
echo "  destino: $DESTINO"
echo

# ---------------------------------------------------------------- Java
if ! command -v java >/dev/null 2>&1; then
    echo "  ERROR: no se encontro Java."
    echo "  Instale Java 8 o superior y vuelva a ejecutar este script."
    echo
    exit 1
fi
echo "  Java detectado: $(java -version 2>&1 | head -1)"

# ------------------------------------------------------- carpeta destino
# /Bingo cuelga de la raiz, asi que crearla suele requerir sudo. Una vez
# creada se le pasa la propiedad al usuario y ya no hace falta mas.
SUDO=""
if [ ! -d "$DESTINO" ]; then
    if mkdir -p "$DESTINO" 2>/dev/null; then
        echo "  Carpeta creada: $DESTINO"
    else
        echo "  Se necesita permiso de administrador para crear $DESTINO"
        SUDO="sudo"
        $SUDO mkdir -p "$DESTINO" || { echo "  ERROR: no se pudo crear $DESTINO"; exit 1; }
        $SUDO chown "$USUARIO" "$DESTINO"
        echo "  Carpeta creada y asignada a $USUARIO"
    fi
elif [ ! -w "$DESTINO" ]; then
    echo "  $DESTINO existe pero no es escribible; se usara sudo."
    SUDO="sudo"
fi

# ------------------------------------------------------- instalacion previa
PRIMERA=1
if [ -f "$DESTINO/db/tablas.db" ]; then
    PRIMERA=0
    echo
    echo "  Se detecto una instalacion anterior."
    echo "  Se conservaran SUS DATOS:"
    echo "     db/tablas.db            tablas del juego"
    [ -f "$DESTINO/db/licencia.lic" ] && echo "     db/licencia.lic         activacion de este equipo"
    [ -f "$DESTINO/db/config.ker" ]   && echo "     db/config.ker           configuracion"
    echo
    printf "  Continuar? [s/N] "
    read -r RESP
    case "$RESP" in
        s|S|si|SI|Si) ;;
        *) echo "  Instalacion cancelada."; echo; exit 0 ;;
    esac
fi

# ------------------------------------------------------- copia
echo
echo "  Copiando programas y librerias..."
$SUDO rm -rf "$DESTINO/lib"
$SUDO mkdir -p "$DESTINO/lib" "$DESTINO/db"
$SUDO cp "$ORIGEN"/*.jar "$DESTINO"/
$SUDO cp "$ORIGEN"/lib/*.jar "$DESTINO"/lib/
$SUDO cp "$ORIGEN"/*.sh "$DESTINO"/ 2>/dev/null || true
[ -f "$ORIGEN/LEEME.txt" ] && $SUDO cp "$ORIGEN/LEEME.txt" "$DESTINO"/

# db/: solo se copia lo que NO exista, para no pisar datos del cliente
for f in "$ORIGEN"/db/*; do
    [ -e "$f" ] || continue
    base="$(basename "$f")"
    if [ -e "$DESTINO/db/$base" ]; then
        echo "     conservado: db/$base"
    else
        $SUDO cp "$f" "$DESTINO/db/"
        echo "     instalado : db/$base"
    fi
done

$SUDO chown -R "$USUARIO" "$DESTINO" 2>/dev/null || true
chmod +x "$DESTINO"/*.sh 2>/dev/null || true

# ------------------------------------------------------- accesos directos
MENU="$HOME/.local/share/applications"
mkdir -p "$MENU"
crear_acceso() {   # $1=archivo .desktop  $2=nombre  $3=script
    cat > "$MENU/$1" <<EOF
[Desktop Entry]
Type=Application
Name=$2
Comment=Bingo - $2
Exec=$DESTINO/$3
Path=$DESTINO
Terminal=false
Categories=Game;
EOF
    chmod +x "$MENU/$1"
}
crear_acceso bingo-pantalla.desktop     "Bingo - Pantalla"    pantalla.sh
crear_acceso bingo-generador.desktop    "Bingo - Generador"   generador.sh
crear_acceso bingo-configurador.desktop "Bingo - Configurador" configurador.sh
crear_acceso bingo-reportes.desktop     "Bingo - Reportes"    reportes.sh
echo "  Accesos creados en el menu de aplicaciones."

# ------------------------------------------------------- resumen
echo
echo "=============================================================="
echo "  Instalacion terminada en $DESTINO"
echo "=============================================================="
echo
echo "  Para ejecutar:"
echo "     $DESTINO/pantalla.sh        Pantalla de juego"
echo "     $DESTINO/generador.sh       Generador de tablas"
echo "     $DESTINO/configurador.sh    Configuracion de la partida"
echo "     $DESTINO/reportes.sh        Reportes en PDF"
echo
if [ "$PRIMERA" = "1" ]; then
    echo "  PRIMER USO:"
    echo "     1) Al abrir cualquier programa pedira la ACTIVACION de este"
    echo "        equipo: entregue el ID que muestra al Administrador del"
    echo "        Sistema y escriba la clave que le devuelvan."
    echo "     2) Abra el Generador y genere las tablas: la base viene vacia."
    echo
fi
echo "  Respalde la carpeta $DESTINO/db : ahi estan sus tablas,"
echo "  su activacion y su configuracion."
echo
