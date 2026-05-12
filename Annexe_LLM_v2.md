# Annexe — Usage du LLM

## Contexte

Cette annexe documente les usages réels du LLM au cours du projet `ParcCapteurs`, entre le 10 et le 11 mai 2026.

Deux assistants ont été utilisés :

- **Claude Sonnet 4.6** (Anthropic) — pour le développement Java, la correction de bugs, la construction du benchmark et la rédaction du rapport
- **GPT-5** (OpenAI) — pour la conception initiale, la génération de données et l'interprétation des résultats

---

## Modèles et période

| Outil | Famille de modèle | Période |
|-------|-------------------|---------|
| Claude Sonnet 4.6 | Anthropic Claude 4 | 10-11 mai 2026 |
| GPT-5 | OpenAI GPT-5 | 10-11 mai 2026 |

---

## Prompts et échanges — Claude Sonnet 4.6

### Session 1 — Analyse initiale du projet (10 mai 2026)

**Prompt :**
> *Je t'uploade le zip du projet HAI822I ParcCapteurs. Analyse le code, identifie les bugs et explique l'architecture.*

**Résultat obtenu :**
Identification de trois bugs :
1. `"1  0"` (espace parasite dans un littéral entier) → `10` dans `MainParcCapteurs.java`
2. `IDGenerator` statique non remis à zéro entre répétitions → `removeById` échoue sur des IDs inconnus
3. Parc non vidé entre répétitions → accumulation des capteurs

---

### Session 2 — Correction des bugs (10 mai 2026)

**Prompt :**
> *Corrige les bugs identifiés : l'espace dans `1  0`, le compteur IDGenerator statique non réinitialisé, et le parc non vidé entre répétitions.*

**Résultat obtenu :**
- `MainParcCapteurs.java` : `"1  0"` → `10`
- `IDGenerator.java` : ajout de `IDGenerator.reset()` au début de chaque répétition
- `BenchmarkRunner.java` : ajout de `viderParc()` avant chaque remplissage

---

### Session 3 — Ajout des scénarios mixtes (10 mai 2026)

**Prompt :**
> *Ajoute ce qu'il manque au code : des scénarios mixtes avec des pourcentages d'opérations variables (somme = 100%), une mesure séparée par opération, et un export CSV+JSON. Les scénarios doivent permettre d'identifier où chaque structure est la meilleure.*

**Résultat obtenu :**
- `BenchmarkScenario.java` : record avec validation que les pourcentages somment à 100
- `BenchmarkResult.java` : mesure séparée par opération (ajout, recherche, suppression, inventaire, comptage)
- `BenchmarkRunner.java` : 7 répétitions, 1000 opérations, mesure mémoire avant/après GC
- `MainParcCapteurs.java` : 5 scénarios définis, export CSV et JSON

---

### Session 4 — Rapport complet (10 mai 2026)

**Prompt :**
> *Fais le rapport complet en PDF selon les exigences du sujet HAI822I : sujet, protocole, résultats avec graphiques, complexités théoriques, conclusion, note LLM avec empreinte carbone, et annexe des prompts.*

**Résultat obtenu :** rapport PDF puis converti en `.docx` avec 6 sections, 5 figures matplotlib, tableaux de résultats et estimations carbone.

---

### Session 5 — Fenêtre Swing (11 mai 2026)

**Prompt :**
> *J'aimerais qu'un rapport détaillé avec les graphiques bâtons pour chaque scénario et pour chacune des 2 structures de données (LinkedList et HashMap) s'affiche dans une fenêtre Swing.*

**Résultat obtenu :**
- `ui/BenchmarkUI.java` : fenêtre Swing avec un onglet par scénario, 6 graphiques en barres par onglet (ajout, recherche, suppression, inventaire, comptage, total), rouge pour LinkedList et bleu pour HashMap.
- Ajout d'une ligne dans `MainParcCapteurs.java` : `SwingUtilities.invokeLater(() -> new ui.BenchmarkUI(results));`

---

### Session 6 — Correction bug remplissage (11 mai 2026)

**Prompt :**
> *Exception : `CapteurException: id déjà présent` dans BenchmarkRunner.*

**Résultat obtenu :**
Identification du bug : `parc.addCapteur(new Capteur(...))` dans la boucle de remplissage créait de nouveaux IDs via `IDGenerator`, différents de ceux de `base`. Correction : remplacement par `parc.addCapteur(c)` (insertion directe sans recréer d'objet).

---

### Session 7 — Export fichiers résultats (11 mai 2026)

**Prompt :**
> *Dans quel dossier sont envoyés les fichiers résultats CSV et JSON ? Oui et oui [je veux qu'ils soient écrits sur disque dans un dossier `results/`].*

**Résultat obtenu :**
Modification de `MainParcCapteurs.java` : ajout de la méthode `writeFile()` et remplacement des `System.out.println` d'export par des appels à `writeFile("results/benchmark.csv", ...)` et `writeFile("results/benchmark.json", ...)`.

---

### Session 8 — Rapport final avec graphiques réels (11 mai 2026)

**Prompt :**
> *Reprends le rapport et intègre les graphiques fournis en les explicitant. Fournis-moi une version du rapport en `.md` et une en `.doc`. Modifie l'annexe en intégrant mes prompts.*

**Résultat obtenu :** présent document — rapport `.md` enrichi avec les 8 graphiques réels commentés, et annexe LLM mise à jour avec les prompts réels de la session Claude Sonnet 4.6.

---

## Historique reconstitué — GPT-5

### Session 1 — Conception initiale (10 mai 2026)

- objectif : structurer le projet Java autour de `ParcCapteurs`
- demandes typiques : proposer une interface commune, comparer LinkedList et HashMap, suggérer une organisation des packages
- résultat : clarification de l'architecture générale

### Session 2 — Génération de données (10 mai 2026)

- objectif : définir une génération de capteurs réaliste et reproductible
- demandes typiques : générer des objets métier cohérents, assurer l'unicité des identifiants, utiliser une graine fixe
- résultat : stratégie de génération pseudo-aléatoire reproductible

### Session 3 — Protocole expérimental (11 mai 2026)

- objectif : construire un benchmark exploitable pour le rapport
- demandes typiques : choisir des tailles de test, définir des scénarios mixtes, répéter les mesures, exporter CSV/JSON
- résultat : protocole avec tailles variées, répétitions et sorties structurées

### Session 4 — Interprétation (11 mai 2026)

- objectif : analyser les résultats obtenus
- demandes typiques : relier les observations aux complexités théoriques, identifier les cas favorables à HashMap, expliquer les victoires de LinkedList
- résultat : axes d'interprétation réutilisés dans le rapport final

---

## Exemples de demandes GPT-5 (reconstituées)

- *"propose une architecture Java simple pour comparer LinkedList et HashMap dans un parc de capteurs"*
- *"comment générer des capteurs de manière réaliste avec des identifiants uniques et une graine fixe"*
- *"quels scénarios de benchmark choisir pour faire ressortir les différences entre recherche, suppression, inventaire et comptage"*
- *"aide-moi à interpréter des résultats où HashMap gagne surtout sur la recherche mais moins sur l'inventaire complet"*

---

## Protocole d'estimation carbone

Ordre de grandeur retenu :

1. Approximer le texte traité sur l'ensemble des usages en équivalent de 4 pages de 500 mots
2. Appliquer la valeur de référence de Ren et al. (2024) : **~15 gCO2e par page de 500 mots**
3. Estimation haute : **4 × 15 = 60 gCO2e**

Cette valeur est un ordre de grandeur. L'impact réel dépend fortement de la taille du modèle, du matériel, du batching et de l'infrastructure d'exécution.

**Références :**

- Shaolei Ren et al., *Reconciling the contrasting narratives on the environmental impact of large language models*, Scientific Reports, 1 novembre 2024. https://www.nature.com/articles/s41598-024-76682-6
- Mauricio Fadel Argerich, Marta Patiño Martínez, *Measuring and improving the energy efficiency of large language models inference*, IEEE Access, 5 juin 2024. https://doi.org/10.1109/ACCESS.2024.3409745
