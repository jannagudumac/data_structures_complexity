# Annexe — Usage du LLM

## Contexte

Cette annexe documente un historique reconstitué de plusieurs usages réels du LLM au cours du projet `ParcCapteurs`.

Les éléments ci-dessous correspondent à une reconstruction a posteriori d'échanges effectués dans d'autres chats entre le 10 et le 14 mai 2026.

Les formulations ne sont pas nécessairement mot à mot, mais elles reflètent fidèlement les usages réels du LLM pendant le projet.

Cette annexe synthétise les usages saillants ; elle n'en reproduit pas nécessairement chaque tour intégralement.

## Modèle et période

- outil : assistant conversationnel et assistant de code
- famille de modèle : GPT-5
- période d'utilisation : `2026-05-10` à `2026-05-14`

## Historique reconstitué

### Session 1 — Conception initiale

- date : `2026-05-10`
- objectif : structurer le projet Java autour de `ParcCapteurs`
- demandes typiques :
  - proposer une interface commune pour le conteneur
  - comparer `LinkedList` et `HashMap` au regard des opérations du sujet
  - suggérer une organisation simple des classes et packages
- résultat : clarification de l'architecture générale et des choix de structures

### Session 2 — Génération de données

- date : `2026-05-10`
- objectif : définir une génération de capteurs réaliste et reproductible
- demandes typiques :
  - générer des objets métier cohérents
  - assurer l'unicité des identifiants
  - utiliser une graine fixe pour rendre les mesures comparables
- résultat : stratégie de génération pseudo-aléatoire reproductible

### Session 3 — Protocole expérimental

- date : `2026-05-11`
- objectif : construire un benchmark exploitable pour le rapport
- demandes typiques :
  - choisir plusieurs tailles de test
  - définir des scénarios mixtes avec pourcentages
  - répéter les mesures et exporter les résultats
- résultat : protocole avec tailles variées, répétitions et sorties CSV/JSON

### Session 4 — Interprétation

- date : `2026-05-11`
- objectif : analyser les résultats obtenus
- demandes typiques :
  - relier les observations aux complexités théoriques
  - identifier les cas favorables à `HashMap`
  - expliquer pourquoi `LinkedList` reste parfois compétitive
- résultat : axes d'interprétation réutilisés dans le rapport final

### Session 5 — Correctifs benchmark et documentation

- date : `2026-05-14`
- objectif : corriger une incohérence signalée entre les graphiques PNG et les résultats de simulation
- demandes typiques :
  - vérifier si les PNG correspondaient bien aux exports CSV/JSON
  - corriger le chemin de mesure mémoire
  - corriger le crash Swing/headless en environnement graphique invalide
  - régénérer les exports, les graphiques et les documents
- résultat : pipeline de benchmark fiabilisé, graphique mémoire cohérent, documentation resynchronisée

## Exemples de demandes reconstituées

- "propose une architecture Java simple pour comparer LinkedList et HashMap dans un parc de capteurs"
- "comment générer des capteurs de manière réaliste avec des identifiants uniques et une graine fixe"
- "quels scénarios de benchmark choisir pour faire ressortir les différences entre recherche, suppression, inventaire et comptage"
- "aide-moi à interpréter des résultats où HashMap gagne surtout sur la recherche mais moins sur l'inventaire complet"
- "fix the reports and readme and everything to include the fix, and also include the \"memory-measurement path and the Swing/headless crash\" as the prompt"

## Traces produites

Les échanges reconstitués ont contribué à orienter :

- la conception des classes
- le protocole expérimental
- la discussion des résultats
- la correction du chemin de mesure mémoire et du crash Swing/headless
- la rédaction de la synthèse

Ils ne constituent pas à eux seuls une trace logicielle complète, mais une aide à la conception et à l'analyse.

## Protocole d'estimation carbone

Ordre de grandeur retenu :

1. approximer le texte traité sur l'ensemble des usages reconstitués en équivalent pages de 500 mots
2. appliquer une valeur de référence issue de la littérature
3. annoncer explicitement l'incertitude liée à l'absence de mesures internes du fournisseur

Références utilisées :

- Ren et al. 2024, *Scientific Reports* : ordre de grandeur environnemental par page générée
- Argerich et Patiño Martínez 2024, *IEEE Access* : forte dépendance de la consommation au modèle et au mode d'inférence
