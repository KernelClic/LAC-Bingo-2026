# LAC-Bingo — Repositorio multi-producto

Modelo de **rama de larga vida por producto**. `main` es la base comun
(Licencia/KeyGen + nucleo compartido) y **ancestro de todas** las ramas, de
modo que un cambio compartido se hace UNA vez en `main` y baja en cascada.

## Arbol de ramas

```
main                         <- BASE COMUN: Licencia/KeyGen + nucleo (Conector/Modelo)
|
|-- reporte-universal        <- app unica   (recibe de main)
|-- config                   <- app unica   (recibe de main)
|-- figura-archivo           <- app unica   (recibe de main)
|
|-- pantalla/base            <- codigo comun de TODAS las Pantallas (recibe de main)
|   |-- pantalla/15
|   |-- pantalla/vegano
|   |-- pantalla/salon
|   |-- pantalla/universal
|
|-- generador/base           <- codigo comun de TODOS los Generadores (recibe de main)
    |-- generador/15
    |-- generador/vegano
    |-- generador/salon
    |-- generador/universal
```

## Las 4 reglas de oro

1. **Lo compartido BAJA** — licencia/nucleo se corrige en `main` y se cascadea
   `main -> */base -> cada variante`. Nunca se corrige licencia en una variante.
2. **Lo especifico SE QUEDA** — un ajuste solo de una variante se hace en una
   rama corta `feat/<variante>-<tema>` y se mergea de vuelta a esa variante.
3. **Lo generico-descubierto SUBE una vez** — si un bug hallado en una variante
   es de todas, se lleva a `*/base` (o `main`) y se vuelve a cascadear.
4. **Entregas = tags, no ramas** — cada version a cliente es un tag sobre la
   variante (p.ej. `pantalla/vegano-v1.3`).

Ver `docs/estructura.md` para el detalle del flujo y los comandos de cascada.

## Como modificar una variante (ejemplo: Pantalla Salon)

Cada producto vive en su rama; con los worktrees cada rama es una carpeta en
`../LAC-Bingo-2026-wt/<rama>` (ver mas abajo). Pasos para un cambio SOLO de Salon:

```bash
# 1. Ir al worktree de Salon (ya esta en la rama pantalla/salon)
cd ../LAC-Bingo-2026-wt/pantalla/salon

# 2. (Recomendado) rama de trabajo corta
git switch -c feat/pantalla-salon-<tema>

# 3. Editar el codigo de Salon, p.ej. Pantalla/src/Vista/Pantalla.java

# 4. Compilar y probar (sin ant; contra /Bingo/lib; DISPLAY=:1)
CP=$(ls /Bingo/lib/*.jar | tr '\n' ':')
javac --release 8 -encoding UTF-8 -d /tmp/salon_build -cp "$CP" $(find Pantalla/src -name '*.java')
DISPLAY=:1 java -cp "/tmp/salon_build:$CP" Vista.Pantalla   # Vista.Pantalla abre la ventana sin login

# 5. Commit
git add -A && git commit -m "Pantalla Salon: <que cambiaste>"

# 6. Integrar a la rama del producto y subir
git switch pantalla/salon
git merge --no-ff feat/pantalla-salon-<tema>
git branch -d feat/pantalla-salon-<tema>
git push origin pantalla/salon
```

### ¿Donde va el cambio? (decide ANTES de editar)

| El cambio es...                                   | Se hace en...                                   |
|---------------------------------------------------|-------------------------------------------------|
| Solo de Salon (tema, textos, su figura)           | `pantalla/salon` (lo de arriba). Se queda.      |
| De TODAS las Pantallas (bug del flash, JDBC, ...) | `pantalla/base` y se CASCADEA a las 4 variantes.|
| De TODOS los productos (licencia, nucleo)         | `main` y se cascadea a todo.                     |

Si un fix generico se empezo por error en Salon: llevarlo a base con
`git cherry-pick <hash>` desde `pantalla/base` y volver a cascadear (regla 3).

### Cascada de un cambio comun (hecho en pantalla/base) hacia las variantes
```bash
cd ../LAC-Bingo-2026-wt/pantalla/base   # commit del fix comun aqui
for v in 15 vegano salon universal; do
  git -C ../$v merge --no-ff pantalla/base && git -C ../$v push origin pantalla/$v
done
```

## Worktrees (ver todas las ramas a la vez)

El repo principal (`LAC-Bingo-2026/`) esta en `main`. Cada otra rama tiene su
carpeta en `../LAC-Bingo-2026-wt/<rama>` compartiendo el mismo `.git`. Asi editas
varios productos en paralelo sin `git switch`. Gestion:
`git worktree list` | `git worktree add <ruta> <rama>` | `git worktree remove <ruta>`.

## Layout de carpetas (fuente unica en `main`)

- `Compartido/Licencia-KeyGen/`  libreria node-lock + generador de claves.
- `Compartido/Nucleo-Comun/`     Conector/Modelo/acceso a datos comun.

Cada rama de producto agrega SU carpeta de app (`Pantalla/`, `Generador/`,
`Reporte-Universal/`, `Config/`, `Figura-Archivo/`).
