# HAI822I — ParcCapteurs

Projet de benchmark Java pour comparer deux implémentations du conteneur `ParcCapteurs` :

- `LinkedList<Capteur>`
- `HashMap<Integer, Capteur>`

Le projet mesure les performances sur plusieurs tailles de données et plusieurs scénarios mixtes, puis exporte les résultats en CSV/JSON pour analyse et visualisation.

Depuis la mise à jour du `14 mai 2026`, le benchmark :

- mesure la mémoire sur un chemin dédié, indépendant des scénarios, afin d'éviter les valeurs `0` instables
- protège mieux le lancement Swing en environnement headless ou avec un `DISPLAY` invalide

## Auteurs

- Janna Gudumac
- Paul Maurin

## Répartition du travail

- Paul Maurin : travail principal sur le code Java
- Janna Gudumac : travail principal sur le notebook Python et l'intégration des graphiques
- Rédaction du rapport : travail réalisé ensemble

## Contenu du dépôt

### Code source

- `src/model/` : objets métier (`Capteur`, `TypeCapteur`, exceptions, générateur d'identifiants)
- `src/container/` : interface du conteneur et deux implémentations
- `src/generator/` : génération de données
- `src/benchmark/` : moteur de benchmark et résultats
- `src/app/` : point d'entrée
- `src/ui/` : interface Swing de visualisation

### Résultats et visualisation

- `results/results.csv` : export CSV du benchmark
- `results/results.json` : export JSON du benchmark
- `graphs/graphs.ipynb` : notebook de génération des graphiques
- `graphs/*.png` : graphiques générés
- `graphs/recap_winner.csv` : tableau récapitulatif des structures gagnantes

### Rédaction

- `Rapport_ParcCapteurs.md` : rapport principal
- `Annexe_LLM.md` : annexe sur l'usage des LLM
- `SujetDevoirMaison.pdf` : énoncé du devoir

## Prérequis

### Java

Le projet doit être compilé et exécuté avec une version compatible Java 17 ou plus récente.

Exemple de vérification :

```bash
javac -version
java -version
```

### Python

Le notebook utilise :

- `pandas`
- `numpy`
- `matplotlib`
- `jupyter`
- `jinja2`

Un environnement virtuel local peut être utilisé si besoin.

## Compilation et exécution

### Compilation

Depuis la racine du projet :

```bash
mkdir -p bin
javac -d bin -sourcepath src $(find src -name "*.java")
```

### Exécution du benchmark

```bash
java -cp bin app.MainParcCapteurs
```

Le programme :

- exécute les benchmarks pour toutes les tailles et tous les scénarios
- affiche les résultats dans la console
- écrit :
  - `results/results.csv`
  - `results/results.json`

Si un environnement graphique réellement exploitable est disponible, l'interface Swing s'ouvre automatiquement. Sinon, le benchmark fonctionne quand même, exporte les fichiers et n'échoue plus sur un faux contexte graphique.

## Scénarios testés

| Scénario | Ajout | Retrait | Recherche | Inventaire | Comptage |
|---|---:|---:|---:|---:|---:|
| `S1-Equilibre` | 20% | 20% | 20% | 20% | 20% |
| `S2-LectureIntensive` | 5% | 5% | 70% | 10% | 10% |
| `S3-EcritureIntensive` | 40% | 40% | 5% | 5% | 10% |
| `S4-InventaireIntensif` | 10% | 5% | 10% | 70% | 5% |
| `S5-ComptageIntensif` | 10% | 5% | 10% | 5% | 70% |

Tailles mesurées :

- `100`
- `500`
- `1000`
- `2000`
- `5000`
- `10000`

## Génération des graphiques

Le notebook se trouve dans `graphs/` et lit les résultats depuis `../results/`.

Les graphes actuellement versionnés ont été régénérés après la correction du chemin de mesure mémoire. Le graphique `g5_memoire.png` repose donc maintenant sur des mesures cohérentes par couple `(structure, taille)`.

### Depuis le dossier `graphs/`

```bash
jupyter notebook graphs.ipynb
```

Si vous utilisez un environnement virtuel local :

```bash
python3 -m venv .venv
.venv/bin/pip install pandas numpy matplotlib jupyter jinja2
```

Le notebook produit :

- `g1_total_par_scenario.png`
- `g2_ops_scenario_equilibre.png`
- `g3_barres_scenarios.png`
- `g4_ratio_performance.png`
- `g5_memoire.png`
- `g6_heatmap.png`
- `g7_repartition_operations.png`
- `g8_loglog.png`
- `recap_winner.csv`

## Eclipse

Import recommandé :

`File -> Import -> General -> Existing Projects into Workspace`

Puis sélectionner ce dossier.

## Fichiers importants pour le rendu

Pour un rendu académique, les fichiers les plus importants sont :

- `src/`
- `results/results.csv`
- `results/results.json`
- `graphs/graphs.ipynb`
- `Rapport_ParcCapteurs.md`
- `Annexe_LLM.md`
- `SujetDevoirMaison.pdf`

## Remarques

- Les fichiers dans `bin/` sont des artefacts compilés.
- Les fichiers PNG et exports CSV de synthèse sont régénérables.
- Le benchmark utilise des graines fixes pour garder des mesures reproductibles.
- La mesure mémoire est maintenant séparée des scénarios : elle est sondée plusieurs fois après `GC`, puis consolidée par médiane pour limiter le bruit de la JVM.
