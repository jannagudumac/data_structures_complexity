package benchmark;

/**
 * Resultat d'un benchmark pour une structure, une taille et un scenario donnes.
 * Les temps sont en nanosecondes (moyenne sur toutes les repetitions).
 */
public record BenchmarkResult(
        String structure,
        String scenario,
        int    taille,
        long   tempsAjoutNs,
        long   tempsRechercheNs,
        long   tempsSuppressionNs,
        long   tempsInventaireNs,
        long   tempsComptageNs,
        double memoireKo
) {
    /** Temps total toutes operations confondues. */
    public long tempsTotalNs() {
        return tempsAjoutNs + tempsRechercheNs + tempsSuppressionNs
                + tempsInventaireNs + tempsComptageNs;
    }

    @Override
    public String toString() {
        return String.format(
            "[%s | scenario=%s | n=%d] total=%.2fms ajout=%.2fms recherche=%.2fms "
            + "suppression=%.2fms inventaire=%.2fms comptage=%.2fms mem=%.1fKo",
            structure, scenario, taille,
            (double) tempsTotalNs()     / 1_000_000.0,
            (double) tempsAjoutNs       / 1_000_000.0,
            (double) tempsRechercheNs   / 1_000_000.0,
            (double) tempsSuppressionNs / 1_000_000.0,
            (double) tempsInventaireNs  / 1_000_000.0,
            (double) tempsComptageNs    / 1_000_000.0,
            memoireKo
        );
    }
}
