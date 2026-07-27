# Config Universal

Unifica en **una sola ventana** las tres variantes del configurador mediante
pestañas (`JTabbedPane`):

- **01 · Mensaje / Intentos / Tablas** (ex Config_01)
- **02 · Tablas / Editar** (ex Config_02)
- **03 · Figuras / Letras (completo)** (ex Config_03, superset)

Cada pestaña reutiliza integramente la UI y la logica de su variante
(`Config01/02/03.java`); `Vista/Config.java` solo arma el `JTabbedPane`
reparentando el `contentPane` de cada una.

Cambios respecto a las variantes sueltas:
- **Se quito** el cambio de contraseña (`updPassword` eliminado; boton oculto).
- **Se aplica validacion por licencia** node-lock (`Controlador.Licencia`):
  `Entrada` exige equipo activado antes de abrir el configurador.

## Archivo unico: `/Bingo/db/config.ker`

Ya no existe `config.dat`. Toda la configuracion vive en **un solo archivo**,
`/Bingo/db/config.ker`, compartido por los tres programas:

| Claves | Las escribe | Las lee |
|---|---|---|
| `partida.*` — partida programada (intentos, mensaje, tablas ganadoras) | Config 01/02/03 | **Pantalla** |
| `config.modulos` — modulos visibles del configurador | Config Universal | Config Universal |
| `generacion.modos` — modo de generacion | Generador Universal | Generador Universal |

`Controlador/AccessFile` conserva su API de siempre, pero persiste en ese archivo
via `Controlador/Preferencias`. Al guardar se releen las claves del disco y solo
se imponen las propias, de modo que los tres programas conviven sin pisarse.

Si aparece un `config.dat` del formato viejo, se importa una sola vez y se borra.

La pestaña **Mantenimiento** borra ese archivo: se pierde todo lo anterior de
golpe, por eso conviene dejarla deshabilitada en las entregas a cliente.
