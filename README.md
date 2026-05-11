# HAI822I — ParcCapteurs

## Lancer le projet

**Eclipse** : File → Import → General → Existing Projects into Workspace → sélectionner ce dossier.

**Ligne de commande** :
```bash
mkdir bin
javac -d bin -sourcepath src $(find src -name "*.java")
java -cp bin app.MainParcCapteurs
```

## Bugs corrigés
1. `MainParcCapteurs.java` : `"1  0"` → `10`
2. `IDGenerator.java` : ajout de `reset()` appelé avant chaque répétition
3. `BenchmarkRunner.java` : parc vidé et rechargé entre chaque répétition

## Scénarios
| Scénario | Ajout | Retrait | Recherche | Inventaire | Comptage |
|----------|-------|---------|-----------|------------|----------|
| S1 Équilibré | 20% | 20% | 20% | 20% | 20% |
| S2 Lecture intensive | 5% | 5% | 70% | 10% | 10% |
| S3 Écriture intensive | 40% | 40% | 5% | 5% | 10% |
| S4 Inventaire intensif | 10% | 5% | 10% | 70% | 5% |
| S5 Comptage intensif | 10% | 5% | 10% | 5% | 70% |
