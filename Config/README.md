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

Genera el mismo `/Bingo/db/config.dat` que consume la Pantalla en modo programado.
