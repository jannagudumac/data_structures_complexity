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

Les mesures ont été produites le 11 mai 2026 à partir de l'exécution réelle du programme Java et du notebook `graphs/graphs.ipynb`.

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

### 5.1 Bilan global

Sur les 30 couples `(scénario, taille)` :

- `HashMap` gagne 20 fois
- `LinkedList` gagne 10 fois

Répartition par scénario :

| Scénario | Victoires `LinkedList` | Victoires `HashMap` |
|---|---:|---:|
| `S1-Equilibre` | 2 | 4 |
| `S2-LectureIntensive` | 1 | 5 |
| `S3-EcritureIntensive` | 1 | 5 |
| `S4-InventaireIntensif` | 1 | 5 |
| `S5-ComptageIntensif` | 5 | 1 |

### 5.2 Temps totaux moyens par scénario

| Scénario | `LinkedList` moyen (ms) | `HashMap` moyen (ms) | Conclusion |
|---|---:|---:|---|
| `S1-Equilibre` | 8.389 | 7.497 | avantage `HashMap` |
| `S2-LectureIntensive` | 5.472 | 3.073 | avantage net `HashMap` |
| `S3-EcritureIntensive` | 5.053 | 3.223 | avantage net `HashMap` |
| `S4-InventaireIntensif` | 8.639 | 7.763 | léger avantage `HashMap` |
| `S5-ComptageIntensif` | 14.234 | 14.006 | quasi-égalité |

### 5.3 Exemples représentatifs

À grande taille (`n = 10000`) :

| Scénario | `LinkedList` (ms) | `HashMap` (ms) | Gagnant |
|---|---:|---:|---|
| `S1-Equilibre` | 24.001 | 24.309 | `LinkedList` de très peu |
| `S2-LectureIntensive` | 17.316 | 9.423 | `HashMap` |
| `S3-EcritureIntensive` | 14.674 | 8.272 | `HashMap` |
| `S4-InventaireIntensif` | 29.162 | 26.403 | `HashMap` |
| `S5-ComptageIntensif` | 43.387 | 44.318 | `LinkedList` de très peu |

Le cas le plus favorable à `HashMap` observé dans ces mesures est `S2-LectureIntensive`, `n = 5000` :

- `LinkedList` : `9.884 ms`
- `HashMap` : `4.531 ms`

Soit un rapport d'environ `2.18x` en faveur de `HashMap`.

## 7. Interprétation

### 6.1 Lecture intensive

Les résultats confirment très clairement la théorie.

Dans `S2-LectureIntensive`, la recherche passe de :

- `0.148 ms` à `4.557 ms` pour `LinkedList` entre `n=100` et `n=10000`
- `0.091 ms` à `0.092 ms` pour `HashMap`

La croissance de `LinkedList` est d'environ `30.8x`, alors que `HashMap` reste pratiquement constant. C'est exactement ce qu'on attend d'une recherche linéaire contre une recherche en temps moyen constant.

### 6.2 Ecriture intensive

Même avec beaucoup d'ajouts, `HashMap` reste le meilleur choix, car le scénario contient aussi beaucoup de suppressions par identifiant.

Dans `S3-EcritureIntensive`, la suppression passe de :

- `0.394 ms` à `2.867 ms` pour `LinkedList`
- `0.172 ms` à `0.016 ms` pour `HashMap`

Le coût du retrait par identifiant pénalise fortement `LinkedList`, puisque toute la liste doit être parcourue. Les rares victoires de `LinkedList` à petite taille s'expliquent surtout par des effets de constantes et par le fait que les structures restent petites.

### 6.3 Inventaire intensif

Dans `S4-InventaireIntensif`, les écarts sont beaucoup plus faibles. C'est logique :

- `findAll()` est `O(n)` pour les deux structures
- `countByType()` est aussi `O(n)` pour les deux structures

À `n=5000`, on obtient :

- `LinkedList` : `11.715 ms`
- `HashMap` : `11.463 ms`

L'écart est faible, ce qui montre que lorsque le parcours complet domine, le bénéfice de `HashMap` sur les recherches ponctuelles pèse moins dans le total.

### 6.4 Comptage intensif

`S5-ComptageIntensif` est le scénario où `LinkedList` résiste le mieux. Cela ne signifie pas que `LinkedList` devient théoriquement meilleure, mais plutôt que les deux structures sont contraintes de tout parcourir.

Le temps de comptage croît fortement dans les deux cas entre `n=100` et `n=10000` :

- `LinkedList` : `0.470 ms` → `40.746 ms`
- `HashMap` : `0.587 ms` → `42.767 ms`

La domination du comptage masque donc l'avantage de `HashMap` sur les accès par identifiant. On observe alors des résultats proches, parfois légèrement en faveur de `LinkedList`.

## 8. Occupation mémoire

Les mesures mémoire suggèrent bien que `HashMap` consomme davantage que `LinkedList`, ce qui est cohérent avec la présence d'une table de hachage et de structures internes supplémentaires.

Exemples mesurés :

- `S1-Equilibre`, `n=1000` : `63.63 Ko` pour `LinkedList` contre `93.94 Ko` pour `HashMap`
- `S2-LectureIntensive`, `n=10000` : `921.45 Ko` pour `HashMap`

Cependant, cette partie des résultats doit être interprétée avec prudence :

- 43 mesures mémoire sur 60 valent `0`
- la mesure actuelle repose sur un `GC` avant/après, ce qui reste très sensible au comportement de la JVM
- l'expérience mémoire est donc indicative, mais pas suffisamment stable pour une conclusion quantitative fine

Conclusion mémoire :

- qualitativement, `HashMap` semble plus gourmand
- quantitativement, le protocole actuel n'est pas assez robuste pour classer précisément les deux structures sur ce critère

## 9. Limites de l'expérience

Les résultats sont réels et reproductibles, mais plusieurs limites doivent être signalées :

- l'outil de mesure n'est pas un micro-benchmark de type JMH
- la JVM peut introduire du bruit via le JIT, le ramasse-miettes et l'état mémoire global
- les scénarios mélangent les opérations, ce qui est pertinent pour l'usage, mais complique l'isolation parfaite des coûts
- la mesure mémoire doit être améliorée
- certaines inversions faibles entre structures à petite taille relèvent probablement davantage des constantes et du bruit expérimental que d'un changement de complexité

## 10. Conclusion

Les mesures réelles confirment l'analyse théorique.

`HashMap<Integer, Capteur>` est le meilleur choix général pour `ParcCapteurs`, surtout lorsque l'application effectue beaucoup de recherches et de suppressions par identifiant. C'est le cas le plus fréquent dans un conteneur piloté par identifiants, et les gains deviennent importants dès que la taille du parc augmente.

`LinkedList<Capteur>` reste acceptable pour :

- de très petites tailles
- des scénarios dominés par le parcours complet ou le comptage
- des situations où l'on cherche avant tout une structure simple et légère

Mais dès que les opérations par identifiant deviennent importantes, `LinkedList` devient rapidement moins adaptée.

Conclusion pratique :

- si l'usage principal est l'accès par identifiant, choisir `HashMap`
- si le travail consiste surtout à parcourir tous les capteurs ou à compter par type, les deux structures deviennent proches, sans avantage décisif pour `LinkedList`
- pour un usage réel de type inventaire applicatif, `HashMap` est le choix recommandé

## 11. Note sur l'usage des LLM

Un LLM a été utilisé entre le 10 et le 11 mai 2026 pour :

- réfléchir à l'architecture Java du projet
- proposer un protocole de benchmark
- discuter la génération de données
- aider à l'interprétation des résultats

Traçabilité minimale conservée :

- modèle utilisé : assistant de type GPT-5 / assistant de code
- période : `2026-05-10` à `2026-05-11`
- conservation : reconstitution fidèle de plusieurs usages réels effectués dans d'autres chats

Les formulations de l'annexe ne sont donc pas des citations mot à mot. Elles correspondent à une synthèse rédigée a posteriori à partir d'échanges réels antérieurs, sans inclure la conversation ayant servi à produire la version finale du rapport.

Une annexe séparée a été ajoutée dans `Annexe_LLM.md`.

## 12. Estimation de l'empreinte carbone du recours au LLM

Cette estimation reste indicative, car nous ne disposons pas des mesures internes exactes du modèle utilisé ici.

Protocole retenu :

1. approximer le volume de texte traité sur l'ensemble des usages reconstitués par un équivalent de `4` pages de `500` mots
2. utiliser comme ordre de grandeur la valeur fournie par Ren et al. (2024) pour un LLM "typique" de taille moyenne : environ `15 gCO2e` pour une page de `500` mots
3. calculer une estimation haute de cet ensemble d'usages : `4 x 15 = 60 gCO2e`

Interprétation :

- estimation indicative haute : environ `60 gCO2e`
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
