# Empaquetado de la entrega

Fuente de verdad de lo que rodea a los JAR: lanzadores, instaladores y LEEME de
cada sistema operativo. Antes vivia suelto en una carpeta temporal, y al no
estar versionado se perdia entre sesiones.

```
windows/   *.bat + instalar.bat + LEEME.txt      -> Bingo-Windows.zip
linux/     *.sh  + instalar.sh  + LEEME.txt      -> Bingo-Linux.tar.gz
armar-paquetes.sh
```

## Armar los paquetes

```bash
./armar-paquetes.sh                 # desde /Bingo, deja los paquetes en /Bingo
./armar-paquetes.sh /otro /destino  # desde otro despliegue
```

Toma los 4 JAR y `lib/` del despliegue, agrega los lanzadores de este
directorio y produce los dos paquetes.

## Que NO viaja al cliente

| | Por que |
|---|---|
| `db/licencia.lic` | la activacion es por equipo; el cliente activa el suyo |
| `db/config.ker` | la configuracion arranca limpia |
| `GenLic.zip` | genera claves de activacion: **uso interno** |
| herramientas de prueba | estan en `pantalla/base` -> `Pantalla/pruebas/` |

`db/tablas.db` se incluye **vacia**: el cliente genera sus tablas con el
Generador en el primer uso.

## Instaladores

Copian todo a `C:\Bingo` o `/Bingo` —ruta fija que el programa necesita para
encontrar `db/`— comprueban Java y crean accesos directos.

**Reinstalar conserva los datos**: los archivos de `db/` solo se copian si no
existen, asi que las tablas, la activacion y la configuracion del cliente
sobreviven a una actualizacion. Si detecta instalacion previa, lista lo que va
a conservar y pide confirmacion.

Para probar un instalador sin tocar una instalacion real:

```bash
BINGO_DIR=/tmp/prueba ./instalar.sh
```
