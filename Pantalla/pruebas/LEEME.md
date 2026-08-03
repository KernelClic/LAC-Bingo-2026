# Lanzadores de prueba — partida programada

> Estas herramientas NO se despliegan ni se entregan: viven aqui, en el repo.
> Para usarlas, compilarlas contra el jar desplegado (ver "Recompilar" al final)
> en una carpeta temporal.

Herramientas internas para verificar el amaño de la Pantalla sin tener que
cantar los números a mano. Abren la Pantalla real (`Pantalla-Universal.jar`),
hacen el login, marcan los números de una tabla y al terminar imprimen los
ganadores anunciados.

**No forman parte de la entrega**: quedan fuera de `Bingo-Windows.zip` y de
`Bingo-Linux.tar.gz`.

Para auditar una partida real sin estas herramientas, la Pantalla trae un
diagnostico integrado que se enciende sin recompilar:

```bash
java -Dbingo.diag=true -jar Pantalla-Universal.jar 2>&1 | tee juego.log
```

## Uso

```bash
./cantar-pleno.sh  [tabla] [pausaMs]              # por defecto: 1, 700
./cantar-figura.sh [pleno|u|t] [tabla] [pausaMs]  # por defecto: pleno, 1, 700
```

| Lanzador | Qué prueba | Cómo |
|---|---|---|
| `cantar-pleno.sh` | **Config 02** — premia al COMPLETARSE el Pleno | canta los 24 números de la tabla |
| `cantar-figura.sh` | **Config 03** — premia a UNA casilla de completar | canta uno menos que la figura |

La casilla central es libre (`-1` en la base) y no se canta: `vBingo` ya arranca
con ese valor.

## Qué esperar

Con partida en `/Bingo/db/config.ker` (`partida.n > 0`):

```
$ ./cantar-figura.sh t 5 400
figura        : Letra T
modoProgramado: true
pre-fijadas   : 30, -1
ganadores anunciados: 1
   tabla=5  juego=Letra T  codigo=30      <- premiada sin completar la figura
```

Sin partida guardada, juego normal: `modoProgramado: false` y gana únicamente
quien complete de verdad.

## Alcance del amaño

Solo llega a **Pleno, U Grande, Letra T, Letra L y Letra X**. Las demás letras
(O, N, C, H, I, Z, S, E, L invertida, Cuadrado, Casita) se detectan hoy por
`matriz.txt` y esa ruta todavía no tiene versión programada: sus datos se
guardan en los registros 8..18 pero la Pantalla no los usa para forzar ganador.

## Recompilar

Si cambia el código de la Pantalla:

```bash
CP=$(ls /Bingo/lib/*.jar | tr '\n' ':')
javac -encoding UTF-8 --release 8 -d . -cp "/Bingo/Pantalla-Universal.jar:$CP" *.java
```
