#!/usr/bin/env bash
# Config 02 — premia al COMPLETARSE el Pleno
#   ./cantar-pleno.sh [1] [700]
DIR="$(cd "$(dirname "$0")" && pwd)"
CP="$DIR:/Bingo/Pantalla-Universal.jar:$(ls /Bingo/lib/*.jar | tr '\n' ':')"
exec env DISPLAY=${DISPLAY:-:1} java -cp "$CP" CantarPleno "$@"
