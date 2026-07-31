# Licencia / KeyGen (compartido)

Libreria node-lock (huella MAC+hostname -> ID -> clave) compilada DENTRO de
cada app, y el generador interno de claves (--keygen) de el Administrador del Sistema.

> Vive en `main` porque es compartido por todos. Se corrige aqui y baja en
> cascada. Importar desde el repo actual: `LAC-Bingo/Bingo_Pantalla/src/Controlador/Licencia.java`.

## Generador de claves autonomo (`Vista/GenLic`)

Herramienta **interna**: convierte el ID de equipo que reporta el cliente en su
clave de activacion. **No se entrega al cliente**, y por eso queda fuera de los
paquetes `Bingo-Windows.zip` y `Bingo-Linux.tar.gz`.

```
src/Controlador/Licencia.java         algoritmo y secreto (el mismo de las 4 apps)
src/Controlador/AccesoAleatorio.java  version reducida: solo la ruta de datos
src/Vista/GenLic.java                 ventana + modo consola
```

`AccesoAleatorio` va reducido a proposito: el completo arrastra todo el paquete
`Modelo`, y esta herramienta no lee ni escribe archivos, solo calcula. Asi el
jar queda autonomo (~9 KB, sin librerias).

Compilar y empaquetar:

```bash
javac -encoding UTF-8 --release 8 -d build src/Controlador/*.java src/Vista/GenLic.java
jar cfe Bingo_GenLic.jar Vista.GenLic -C build .
```

Uso: ventana con doble clic, o por consola
`java -jar Bingo_GenLic.jar 1234-5678-9ABC-DEF0`.

> **Si cambia `Licencia.java` hay que recompilar el generador**, o dejara de
> producir claves validas. El paquete listo se arma en `/Bingo/GenLic.zip`.
