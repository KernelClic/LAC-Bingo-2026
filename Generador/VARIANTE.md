# Generador — Universal

Generador Universal.

Diverge en CODIGO de `*/base`. Los cambios compartidos llegan por cascada
desde `main` -> `base` -> esta variante. Los ajustes propios se hacen en
ramas `feat/...` y se mergean de vuelta AQUI.

## Diferencias propias de esta variante

### Modos de generacion configurables (gesto oculto)

`Ctrl+Shift+DobleClic` sobre el rotulo **TABLAS A GENERAR** abre la ventana
`Vista/OpcionesGeneracion`, donde el administrador elige que modos se le
ofrecen al operador: **Solo Normal**, **Solo Personalizada** o **Ambas**
(por defecto). El modo no habilitado se oculta de la pantalla; si queda uno
solo, se selecciona de una vez junto con sus paneles (Excepciones / Tablas
Ganadoras).

La eleccion se guarda en el archivo **binario** `/Bingo/db/config.ker`
(`c:\Bingo\db\config.ker` en Windows) via `Controlador/PreferenciasGenerador`:
firma `KERC` + version + bloque clave/valor enmascarado con XOR, para que no se
pueda leer ni editar con un editor de texto. Al ser clave/valor se pueden
agregar preferencias nuevas sin romper los archivos ya escritos.
