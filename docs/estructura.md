# Flujo de trabajo y cascada

## Crear un ajuste especifico de una variante
```
git switch pantalla/vegano
git switch -c feat/pantalla-vegano-<tema>
# ...commits...
git switch pantalla/vegano
git merge --no-ff feat/pantalla-vegano-<tema>
git branch -d feat/pantalla-vegano-<tema>
```

## Cascadear un cambio compartido (hecho en main)
```
git switch main            # commit del fix compartido aqui
# baja a las bases
for base in pantalla/base generador/base; do
  git switch "$base" && git merge --no-ff main
done
# baja a las variantes
for v in 15 vegano salon universal; do
  git switch "pantalla/$v" && git merge --no-ff pantalla/base
  git switch "generador/$v" && git merge --no-ff generador/base
done
# apps unicas
for a in reporte-universal config figura-archivo; do
  git switch "$a" && git merge --no-ff main
done
```

## Subir un fix generico descubierto en una variante
```
# desde la variante, aislar el/los commit(s) genericos y llevarlos a base:
git switch pantalla/base
git cherry-pick <hash>
# luego cascadear pantalla/base hacia las 4 variantes (como arriba)
```

## Entregar una version
```
git switch pantalla/vegano
git tag -a pantalla/vegano-v1.3 -m "Entrega Vegano 1.3"
```

