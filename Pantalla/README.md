# Pantalla (base comun)

Rama de producto de larga vida. Recibe de `main` los cambios compartidos
(Licencia/KeyGen + nucleo) via merge en cascada.

Importar codigo base desde el repo actual: `LAC-Bingo/Bingo_Pantalla  (rama Produccion2)`.

## Modo "partida programada" (config.dat)

Funcionalidad reincorporada desde la v01 del repo viejo (se habia perdido en
Produccion2). Al arrancar, `Entrada` busca `/Bingo/db/config.dat`:

- **Si existe** -> se carga la configuracion binaria (generada por la app
  `Config`) y se activa `modoProgramado`. En ese modo la deteccion de ganador
  usa las sobrecargas `verificar*(vBingo, t10, t11)` del `Conector`, que
  **fuerzan a ganar los cartones pre-fijados** cuando falta 1 casilla para la
  figura (partida controlada por el operador).
- **Si no existe** -> `modoProgramado=false` y el juego es el normal en vivo
  (deteccion real de ganadores). Comportamiento identico al anterior: cero
  regresion para las salas que no usen config.dat.

Formato de `config.dat` (registros de tamaño fijo, RandomAccessFile):
reg.1 = intentos + mensaje + tablas; reg.2 = tablas; reg.3..18 =
`SetConfigLetra(1..16)` con los cartones pre-fijados por figura.

**Alcance actual del amaño:** solo las figuras fijas que conserva esta Pantalla
(Pleno, U Grande, Letra T, Letra L, Letra X). Las demas letras del v01
(O, N, C, H, I, Z, S, E, L invertida, Cuadrado, Casita) ahora son figuras
configurables via `matriz.txt` (`verificarArchivo`), que todavia **no** tienen
ruta de amaño. Los 16 slots del config.dat se leen y guardan igualmente
(sin perdida de datos) para una posible extension futura a `verificarArchivo`.
