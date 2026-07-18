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

## Layout de carpetas (fuente unica en `main`)

- `Compartido/Licencia-KeyGen/`  libreria node-lock + generador de claves.
- `Compartido/Nucleo-Comun/`     Conector/Modelo/acceso a datos comun.

Cada rama de producto agrega SU carpeta de app (`Pantalla/`, `Generador/`,
`Reporte-Universal/`, `Config/`, `Figura-Archivo/`).
