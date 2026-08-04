@echo off
setlocal enabledelayedexpansion
title Instalacion de Bingo

rem ============================================================
rem  Instalador de Bingo para Windows.
rem
rem  Instala en C:\Bingo, que es la ruta que el programa espera:
rem  busca su base de datos en C:\Bingo\db y no funciona desde
rem  otra ubicacion.
rem
rem  Reinstalar es seguro: los datos de db\ (tablas, licencia y
rem  configuracion) se conservan. Solo se reemplazan los
rem  programas y las librerias.
rem ============================================================

set "DESTINO=C:\Bingo"
set "ORIGEN=%~dp0"
if "%ORIGEN:~-1%"=="\" set "ORIGEN=%ORIGEN:~0,-1%"

echo.
echo ==============================================================
echo   Instalacion de Bingo
echo ==============================================================
echo   origen : %ORIGEN%
echo   destino: %DESTINO%
echo.

rem ------------------------------------------------------ Java
where java >nul 2>&1
if errorlevel 1 (
    echo   ERROR: no se encontro Java.
    echo   Instale Java 8 o superior desde https://www.java.com
    echo   y vuelva a ejecutar este instalador.
    echo.
    pause
    exit /b 1
)
echo   Java detectado.

rem ------------------------------------------- permisos de escritura
rem Crear carpetas en la raiz de C: suele requerir administrador.
if not exist "%DESTINO%" (
    mkdir "%DESTINO%" 2>nul
    if errorlevel 1 (
        echo.
        echo   No se pudo crear %DESTINO%.
        echo   Cierre esta ventana y vuelva a ejecutar el instalador
        echo   con boton derecho ^> "Ejecutar como administrador".
        echo.
        pause
        exit /b 1
    )
    echo   Carpeta creada: %DESTINO%
)

rem --------------------------------------------- instalacion previa
set PRIMERA=1
if exist "%DESTINO%\db\tablas.db" (
    set PRIMERA=0
    echo.
    echo   Se detecto una instalacion anterior.
    echo   Se conservaran SUS DATOS:
    echo      db\tablas.db            tablas del juego
    if exist "%DESTINO%\db\licencia.lic" echo      db\licencia.lic         activacion de este equipo
    if exist "%DESTINO%\db\config.ker"   echo      db\config.ker           configuracion
    echo.
    set /p RESP="  Continuar? [s/N] "
    if /i not "!RESP!"=="s" if /i not "!RESP!"=="si" (
        echo   Instalacion cancelada.
        echo.
        pause
        exit /b 0
    )
)

rem ------------------------------------------------------- copia
echo.
echo   Copiando programas y librerias...
if exist "%DESTINO%\lib" rmdir /s /q "%DESTINO%\lib"
mkdir "%DESTINO%\lib" 2>nul
if not exist "%DESTINO%\db" mkdir "%DESTINO%\db"

copy /y "%ORIGEN%\*.jar" "%DESTINO%\" >nul
copy /y "%ORIGEN%\lib\*.jar" "%DESTINO%\lib\" >nul
copy /y "%ORIGEN%\*.bat" "%DESTINO%\" >nul
if exist "%ORIGEN%\LEEME.txt" copy /y "%ORIGEN%\LEEME.txt" "%DESTINO%\" >nul

rem db\ : solo se copia lo que NO exista, para no pisar datos del cliente
for %%F in ("%ORIGEN%\db\*") do (
    if exist "%DESTINO%\db\%%~nxF" (
        echo      conservado: db\%%~nxF
    ) else (
        copy /y "%%F" "%DESTINO%\db\" >nul
        echo      instalado : db\%%~nxF
    )
)

rem --------------------------------------------- accesos directos
echo   Creando accesos directos en el Escritorio...
for %%A in ("Pantalla.bat;Bingo - Pantalla" "Generador.bat;Bingo - Generador" "Configurador.bat;Bingo - Configurador" "Reportes.bat;Bingo - Reportes") do (
    for /f "tokens=1,2 delims=;" %%B in (%%A) do (
        powershell -NoProfile -Command ^
          "$s=(New-Object -ComObject WScript.Shell).CreateShortcut([Environment]::GetFolderPath('Desktop')+'\%%C.lnk');" ^
          "$s.TargetPath='%DESTINO%\%%B'; $s.WorkingDirectory='%DESTINO%'; $s.Save()" >nul 2>&1
    )
)

rem ------------------------------------------------------- resumen
echo.
echo ==============================================================
echo   Instalacion terminada en %DESTINO%
echo ==============================================================
echo.
echo   Para ejecutar, doble clic en los accesos del Escritorio o en:
echo      %DESTINO%\Pantalla.bat        Pantalla de juego
echo      %DESTINO%\Generador.bat       Generador de tablas
echo      %DESTINO%\Configurador.bat    Configuracion de la partida
echo      %DESTINO%\Reportes.bat        Reportes en PDF
echo.
if "%PRIMERA%"=="1" (
    echo   PRIMER USO:
    echo      1^) Al abrir cualquier programa pedira la ACTIVACION de este
    echo         equipo: entregue el ID que muestra al Administrador del
    echo         Sistema y escriba la clave que le devuelvan.
    echo      2^) Abra el Generador y genere las tablas: la base viene vacia.
    echo.
)
echo   Respalde la carpeta %DESTINO%\db : ahi estan sus tablas,
echo   su activacion y su configuracion.
echo.
pause
