package benchmark;

/**
 * Décrit un scénario de benchmark mixte.
 *
 * Les pourcentages doivent sommer à 100.
 * Chaque pourcentage représente la proportion d'opérations de ce type
 * parmi un total de NB_OPS opérations par répétition.
 *
 * Exemples de scénarios :
 *  - "Lecture intensive"   : recherche élevée
 *  - "Écriture intensive"  : ajouts/suppressions élevés
 *  - "Équilibré"           : répartition uniforme
 *  - "Inventaire intensif" : parcours complet dominant
 */
public record BenchmarkScenario(
        String nom,
        int ajoutPct,
        int retraitPct,
        int recherchePct,
        int inventairePct,
        int comptagePct
) {
    public BenchmarkScenario {
        int total = ajoutPct + retraitPct + recherchePct + inventairePct + comptagePct;
        if (total != 100)
            throw new IllegalArgumentException(
                "Les pourcentages doivent sommer à 100, obtenu : " + total);
    }

    @Override
    public String toString() {
        return String.format("%s [ajout=%d%% retrait=%d%% recherche=%d%% inventaire=%d%% comptage=%d%%]",
                nom, ajoutPct, retraitPct, recherchePct, inventairePct, comptagePct);
    }
}
