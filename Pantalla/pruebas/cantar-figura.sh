#!/usr/bin/env bash
# Config 03 — premia a UNA casilla de completar
#   ./cantar-figura.sh [pleno] [1] [700]
DIR="$(cd "$(dirname "$0")" && pwd)"
CP="$DIR:/Bingo/Pantalla-Universal.jar:$(ls /Bingo/lib/*.jar | tr '\n' ':')"
exec env DISPLAY=${DISPLAY:-:1} java -cp "$CP" CantarFigura "$@"
