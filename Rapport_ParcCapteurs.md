# Rapport expérimental — ParcCapteurs

## 1. Auteurs et répartition du travail

- Janna Gudumac
- Paul Maurin

Répartition du travail :

- Paul Maurin : travail principal sur le code Java
- Janna Gudumac : travail principal sur le notebook Python et l'intégration des graphiques
- Rédaction du rapport : travail réalisé ensemble

## 2. Sujet

Ce projet étudie le choix d'une structure de données pour un conteneur `ParcCapteurs` manipulant des objets `Capteur`.

Les deux implémentations comparées sont :

- `LinkedList<Capteur>`
- `HashMap<Integer, Capteur>`

Les opérations demandées par le sujet sont :

- ajout d'un capteur
- retrait par identifiant
- recherche par identifiant
- inventaire complet
- comptage par type

L'objectif est de déterminer dans quels contextes chaque structure est la plus adaptée, à partir de mesures réelles de temps d'exécution et d'occupation mémoire.

## 3. Modèle métier et hypothèses

Chaque capteur possède un identifiant unique généré automatiquement, ainsi qu'un type (`TypeCapteur`). Les données sont générées automatiquement par `CapteurGenerator`, avec une graine fixe afin de rendre les expériences reproductibles.

Hypothèses retenues :

- les identifiants sont uniques
- le conteneur doit offrir le même comportement fonctionnel pour les deux structures
- les mesures sont faites sur des jeux de données identiques pour les deux implémentations
- l'ordre de parcours n'est pas un critère d'évaluation ici

## 4. Structures comparées et complexités théoriques

### `LinkedList<Capteur>`

- `addCapteur` : `O(n)` dans cette implémentation, car on vérifie d'abord l'unicité de l'identifiant avec `containsId`
- `removeById` : `O(n)`
- `findById` : `O(n)`
- `findAll` : `O(n)`
- `countByType` : `O(n)`

### `HashMap<Integer, Capteur>`

- `addCapteur` : `O(1)` en moyenne
- `removeById` : `O(1)` en moyenne
- `findById` : `O(1)` en moyenne
- `findAll` : `O(n)`
- `countByType` : `O(n)`

Conséquence attendue :

- `HashMap` doit dominer dès que les recherches et suppressions par identifiant sont fréquentes
- les écarts doivent se réduire lorsque le temps total est surtout porté par `findAll` et `countByType`, car ces deux opérations restent linéaires dans les deux structures

## 5. Protocole expérimental

Les mesures présentées dans cette version ont été régénérées le 14 mai 2026 à partir de l'exécution réelle du programme Java et du notebook `graphs/graphs.ipynb`.

Cette régénération suit la correction de deux défauts du pipeline :

- le chemin de mesure mémoire a été séparé du benchmark temporel, afin d'éviter les valeurs nulles instables et de rendre `g5_memoire.png` cohérent
- le lancement Swing a été sécurisé en contexte headless ou avec un `DISPLAY` invalide, ce qui permet au benchmark d'exporter proprement ses résultats même sans interface utilisable

Paramètres du benchmark :

- 5 scénarios mixtes
- 6 tailles de données : `100`, `500`, `1000`, `2000`, `5000`, `10000`
- `1000` opérations par scénario
- `7` répétitions par mesure
- graines fixes pour la génération initiale et les tirages pseudo-aléatoires

Scénarios testés :

- `S1-Equilibre` : 20% ajout, 20% retrait, 20% recherche, 20% inventaire, 20% comptage
- `S2-LectureIntensive` : 5%, 5%, 70%, 10%, 10%
- `S3-EcritureIntensive` : 40%, 40%, 5%, 5%, 10%
- `S4-InventaireIntensif` : 10%, 5%, 10%, 70%, 5%
- `S5-ComptageIntensif` : 10%, 5%, 10%, 5%, 70%

Les temps sont mesurés séparément pour chaque famille d'opérations, puis agrégés en un temps total moyen. Les données finales ont été exportées dans :

- `results/results.csv`
- `results/results.json`

La mémoire est désormais mesurée sur un chemin dédié :

- pour chaque couple `(structure, taille)`, on peuple la structure avec le même jeu de capteurs
- on ignore une première sonde pour absorber le bruit de démarrage de la JVM
- on conserve ensuite la médiane de trois mesures après `GC`
- la valeur obtenue est réutilisée pour tous les scénarios de cette structure et de cette taille

Le notebook exécuté a généré les figures suivantes :

- `graphs/g1_total_par_scenario.png`
- `graphs/g2_ops_scenario_equilibre.png`
- `graphs/g3_barres_scenarios.png`
- `graphs/g4_ratio_performance.png`
- `graphs/g5_memoire.png`
- `graphs/g6_heatmap.png`
- `graphs/g7_repartition_operations.png`
- `graphs/g8_loglog.png`

## 6. Résultats principaux

### 6.1 Bilan global

Sur les 30 couples `(scénario, taille)` :

- `HashMap` gagne 24 fois
- `LinkedList` gagne 6 fois

Répartition par scénario :

| Scénario | Victoires `LinkedList` | Victoires `HashMap` |
|---|---:|---:|
| `S1-Equilibre` | 2 | 4 |
| `S2-LectureIntensive` | 1 | 5 |
| `S3-EcritureIntensive` | 0 | 6 |
| `S4-InventaireIntensif` | 1 | 5 |
| `S5-ComptageIntensif` | 2 | 4 |

### 6.2 Temps totaux moyens par scénario

| Scénario | `LinkedList` moyen (ms) | `HashMap` moyen (ms) | Conclusion |
|---|---:|---:|---|
| `S1-Equilibre` | 13.969 | 13.835 | quasi-égalité, léger avantage `HashMap` |
| `S2-LectureIntensive` | 8.121 | 4.422 | avantage net `HashMap` |
| `S3-EcritureIntensive` | 5.239 | 3.180 | avantage net `HashMap` |
| `S4-InventaireIntensif` | 14.035 | 10.381 | avantage `HashMap` |
| `S5-ComptageIntensif` | 23.018 | 20.317 | avantage `HashMap` |

### 6.3 Exemples représentatifs

À grande taille (`n = 10000`) :

| Scénario | `LinkedList` (ms) | `HashMap` (ms) | Gagnant |
|---|---:|---:|---|
| `S1-Equilibre` | 34.438 | 43.087 | `LinkedList` |
| `S2-LectureIntensive` | 22.828 | 10.468 | `HashMap` |
| `S3-EcritureIntensive` | 15.270 | 8.593 | `HashMap` |
| `S4-InventaireIntensif` | 44.670 | 32.246 | `HashMap` |
| `S5-ComptageIntensif` | 60.601 | 48.273 | `HashMap` |

Le cas le plus favorable à `HashMap` observé dans cette campagne est `S2-LectureIntensive`, `n = 10000` :

- `LinkedList` : `22.828 ms`
- `HashMap` : `10.468 ms`

Soit un rapport d'environ `2.18x` en faveur de `HashMap`.

## 7. Interprétation

### 7.1 Lecture intensive

Les résultats confirment très clairement la théorie.

Dans `S2-LectureIntensive`, la recherche passe de :

- `0.156 ms` à `6.130 ms` pour `LinkedList` entre `n=100` et `n=10000`
- `0.021 ms` à `0.076 ms` pour `HashMap`

La croissance de `LinkedList` est très forte, alors que `HashMap` reste dans un ordre de grandeur faible. C'est bien ce qu'on attend d'une recherche linéaire contre une recherche en temps moyen constant.

### 7.2 Ecriture intensive

Même avec beaucoup d'ajouts, `HashMap` reste le meilleur choix, car le scénario contient aussi beaucoup de suppressions par identifiant.

Dans `S3-EcritureIntensive`, la suppression passe de :

- `0.332 ms` à `2.504 ms` pour `LinkedList`
- `0.195 ms` à `0.022 ms` pour `HashMap`

Le coût du retrait par identifiant pénalise fortement `LinkedList`, puisque toute la liste doit être parcourue. Dans cette campagne, `HashMap` gagne les 6 tailles du scénario `S3`.

### 7.3 Inventaire intensif

Dans `S4-InventaireIntensif`, les deux structures restent dominées par les parcours complets. C'est logique :

- `findAll()` est `O(n)` pour les deux structures
- `countByType()` est aussi `O(n)` pour les deux structures

À `n=5000`, on obtient :

- `LinkedList` : `25.960 ms`
- `HashMap` : `19.848 ms`

L'écart est moins spectaculaire que dans les scénarios dominés par la recherche ponctuelle, mais il reste réel. Le parcours complet masque une partie de l'avantage de `HashMap`, sans l'annuler.

### 7.4 Comptage intensif

`S5-ComptageIntensif` reste le scénario le plus proche, car les deux structures doivent parcourir l'ensemble des capteurs pour compter par type.

Le temps de comptage croît fortement dans les deux cas entre `n=100` et `n=10000` :

- `LinkedList` : `1.119 ms` → `57.093 ms`
- `HashMap` : `1.369 ms` → `46.650 ms`

La domination du comptage masque donc en partie l'avantage de `HashMap` sur les accès par identifiant. On observe encore quelques inversions à très petite taille, mais `HashMap` reprend l'avantage dès `n = 1000` dans cette campagne.

## 8. Occupation mémoire

Après correction du chemin de mesure mémoire, les valeurs sont désormais cohérentes pour un même couple `(structure, taille)` quel que soit le scénario.

Les résultats confirment que `HashMap` consomme davantage que `LinkedList`, ce qui est cohérent avec la présence d'une table de hachage et de structures internes supplémentaires.

Exemples mesurés :

- `n=1000` : `23.48 Ko` pour `LinkedList` contre `52.97 Ko` pour `HashMap`
- `n=5000` : `117.23 Ko` pour `LinkedList` contre `264.47 Ko` pour `HashMap`
- `n=10000` : `234.42 Ko` pour `LinkedList` contre `530.84 Ko` pour `HashMap`

Le ratio observé est assez stable, autour de `2.2x` à `2.3x` en faveur de `LinkedList` sur ce critère mémoire.

Cependant, cette partie des résultats doit encore être interprétée avec prudence :

- la mesure repose toujours sur un différentiel mémoire de processus Java, pas sur un outil spécialisé de type JOL
- le `GC`, le JIT et l'état global de la JVM peuvent encore introduire du bruit
- il s'agit donc d'une estimation plus robuste qu'avant, mais encore approximative

Conclusion mémoire :

- qualitativement et quantitativement, `HashMap` apparaît nettement plus gourmand dans cette campagne
- la tendance est maintenant exploitable dans les graphes, même si une instrumentation plus spécialisée resterait préférable

## 9. Limites de l'expérience

Les résultats sont réels et reproductibles, mais plusieurs limites doivent être signalées :

- l'outil de mesure n'est pas un micro-benchmark de type JMH
- la JVM peut introduire du bruit via le JIT, le ramasse-miettes et l'état mémoire global
- les valeurs temporelles peuvent varier d'une campagne à l'autre, même avec graines fixes
- les scénarios mélangent les opérations, ce qui est pertinent pour l'usage, mais complique l'isolation parfaite des coûts
- la mesure mémoire a été améliorée, mais reste une approximation de haut niveau
- le chemin Swing/headless a été corrigé pour fiabiliser les exports en environnement non graphique
- certaines inversions faibles entre structures à petite taille relèvent probablement davantage des constantes et du bruit expérimental que d'un changement de complexité

## 10. Conclusion

Les mesures réelles confirment l'analyse théorique.

`HashMap<Integer, Capteur>` est le meilleur choix général pour `ParcCapteurs`, surtout lorsque l'application effectue beaucoup de recherches et de suppressions par identifiant. Dans cette campagne, il gagne `24` cas sur `30`.

`LinkedList<Capteur>` reste acceptable pour :

- de très petites tailles
- quelques cas marginaux où les constantes d'exécution favorisent la liste chaînée
- des situations où l'on cherche avant tout une structure simple et légère

Mais dès que la taille du parc augmente ou que les opérations par identifiant deviennent importantes, `LinkedList` devient rapidement moins adaptée.

Conclusion pratique :

- si l'usage principal est l'accès par identifiant, choisir `HashMap`
- si le travail consiste surtout à parcourir tous les capteurs ou à compter par type, l'écart se réduit parfois, mais `HashMap` reste majoritairement devant dans cette campagne
- si la mémoire devient un critère fort, `LinkedList` reste plus légère
- pour un usage réel de type inventaire applicatif, `HashMap` est le choix recommandé

## 11. Note sur l'usage des LLM

Un LLM a été utilisé entre le 10 et le 14 mai 2026 pour :

- réfléchir à l'architecture Java du projet
- proposer un protocole de benchmark
- discuter la génération de données
- aider à l'interprétation des résultats
- corriger le chemin de mesure mémoire et le crash Swing/headless
- resynchroniser les exports, graphiques et documents après correction

Traçabilité minimale conservée :

- modèle utilisé : assistant de type GPT-5 / assistant de code
- période : `2026-05-10` à `2026-05-14`
- conservation : reconstitution fidèle de plusieurs usages réels effectués dans d'autres chats

Les formulations de l'annexe ne sont donc pas des citations mot à mot. Elles correspondent à une synthèse rédigée a posteriori à partir d'échanges réels, sans en reproduire l'intégralité tour par tour.

Une annexe séparée a été ajoutée dans `Annexe_LLM.md`.

## 12. Estimation de l'empreinte carbone du recours au LLM

Cette estimation reste indicative, car nous ne disposons pas des mesures internes exactes du modèle utilisé ici.

Protocole retenu :

1. approximer le volume de texte traité sur l'ensemble des usages reconstitués par un équivalent de `5` pages de `500` mots
2. utiliser comme ordre de grandeur la valeur fournie par Ren et al. (2024) pour un LLM "typique" de taille moyenne : environ `15 gCO2e` pour une page de `500` mots
3. calculer une estimation haute de cet ensemble d'usages : `5 x 15 = 75 gCO2e`

Interprétation :

- estimation indicative haute : environ `75 gCO2e`
- estimation potentiellement bien plus basse si le système a utilisé, pour une partie du travail, des modèles plus petits ou des infrastructures plus efficaces

Cette estimation doit donc être comprise comme un ordre de grandeur, pas comme une mesure physique exacte.

Le point important à retenir est surtout méthodologique :

- l'impact dépend fortement de la taille du modèle
- il dépend aussi du matériel, du batching, de la quantification et de l'infrastructure d'exécution

## 13. Références

### Références du projet

- Sujet du devoir : `SujetDevoirMaison.pdf`
- Données mesurées : `results/results.csv`
- Notebook exécuté : `graphs/graphs.ipynb`

### Références sur l'empreinte environnementale des LLM

- Shaolei Ren et al., "Reconciling the contrasting narratives on the environmental impact of large language models", *Scientific Reports*, 1 novembre 2024. https://www.nature.com/articles/s41598-024-76682-6
- Mauricio Fadel Argerich, Marta Patiño Martínez, "Measuring and improving the energy efficiency of large language models inference", *IEEE Access*, 5 juin 2024. https://oa.upm.es/86674/ et DOI https://doi.org/10.1109/ACCESS.2024.3409745
