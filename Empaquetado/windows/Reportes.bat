@echo off
title Bingo - Reportes
cd /d "%~dp0"
where javaw >nul 2>&1
if errorlevel 1 (
    echo.
    echo  No se encontro Java en este equipo.
    echo  Instale Java 8 o superior desde https://www.java.com y vuelva a intentar.
    echo.
    pause
    exit /b 1
)
if /i not "%~dp0"=="C:\Bingo\" (
    echo.
    echo  ATENCION: esta carpeta esta en  %~dp0
    echo  El programa busca sus datos en  C:\Bingo\db
    echo  Muevala a C:\Bingo o no encontrara la base de datos.
    echo.
    pause
)
start "" javaw -jar Reporte-Universal.jar
