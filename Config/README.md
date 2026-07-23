# Config

Rama de producto de larga vida. Recibe de `main` los cambios compartidos
(Licencia/KeyGen + nucleo) via merge en cascada.

Importar codigo base desde el repo actual: `LAC-Bingo/Bingo_Config`.

## Familia Config (base + variantes)

`config/base` es el nucleo comun del configurador (scaffold + Controlador/Modelo
que compilan contra `/Bingo/lib`). Las variantes cambian solo `Vista/Config.java`
y `Vista/Config.form` (la UI y como se arma el `config.dat`):

- `config/01` -> mensaje + No. intentos + tablas ganadoras (Bingo_Config_01)
- `config/02` -> tablas ganadoras + editar/eliminar (Bingo_Config_02)
- `config/03` -> superset: todas las letras/figuras (Bingo_Config_03)

Todas escriben `/Bingo/db/config.dat` (registros de tamaño fijo) que la
`Pantalla` consume en su "modo partida programada". Los cambios compartidos
bajan por cascada `config/base -> config/NN`.
