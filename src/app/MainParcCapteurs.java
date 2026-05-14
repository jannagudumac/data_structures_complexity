package app;

import benchmark.BenchmarkResult;
import benchmark.BenchmarkRunner;
import benchmark.BenchmarkScenario;

import java.awt.GraphicsEnvironment;
import java.util.List;

/**
 * Point d'entrée du projet HAI822I — ParcCapteurs.
 *
 * Scénarios définis conformément au sujet :
 *   a% ajouts + b% retraits + c% recherches + d% inventaires + e% comptages = 100%
 *
 * Scénarios choisis pour mettre en évidence les différences entre structures :
 *   S1 — Équilibré        : répartition uniforme
 *   S2 — Lecture intensive : beaucoup de recherches (favorise HashMap)
 *   S3 — Écriture intensive: beaucoup d'ajouts/retraits (favorise HashMap)
 *   S4 — Inventaire intensif: beaucoup de parcours complets (structures similaires)
 *   S5 — Comptage dominant : comptage par type dominant
 */
public class MainParcCapteurs {

    public static void main(String[] args) {

        // ── Scénarios mixtes ──────────────────────────────────────────────
        BenchmarkScenario[] scenarios = {
            new BenchmarkScenario("S1-Equilibre",          20, 20, 20, 20, 20),
            new BenchmarkScenario("S2-LectureIntensive",    5,  5, 70, 10, 10),
            new BenchmarkScenario("S3-EcritureIntensive",  40, 40,  5,  5, 10),
            new BenchmarkScenario("S4-InventaireIntensif", 10,  5, 10, 70,  5),
            new BenchmarkScenario("S5-ComptageIntensif",   10,  5, 10,  5, 70),
        };

        // ── Tailles testées ───────────────────────────────────────────────
        int[] tailles = {100, 500, 1000, 2000, 5000, 10000};

        // ── Lancement ─────────────────────────────────────────────────────
        BenchmarkRunner runner = new BenchmarkRunner();
        List<BenchmarkResult> results = runner.runAll(scenarios, tailles);

        // ── Affichage console ─────────────────────────────────────────────
        System.out.println("\n\n=== RÉSULTATS COMPLETS ===");
        String curScenario = "";
        for (BenchmarkResult r : results) {
            if (!r.scenario().equals(curScenario)) {
                curScenario = r.scenario();
                System.out.println("\n--- " + curScenario + " ---");
            }
            System.out.println(r);
        }
        

        // ── Affichage console et Export CSV (utile pour tableur/rapport) ───────────────────────
        System.out.println("\n\n=== CSV EXPORT ===");
        System.out.println(toCsv(results));
        writeFile("results/results.csv",  toCsv(results));

        // ── Affichage console et Export JSON (utile pour graphiques) ───────────────────────────
        System.out.println("\n\n=== JSON EXPORT ===");
        System.out.println(toJson(results));
        writeFile("results/results.json", toJson(results));
        
        
        
        // Affichage graphique Swing si un environnement graphique est
        // effectivement exploitable (DISPLAY present et non vide).
        if (canLaunchUi()) {
            try {
                javax.swing.SwingUtilities.invokeLater(() -> new ui.BenchmarkUI(results));
            } catch (Throwable t) {
                System.out.println("Interface Swing non lancee : " + t.getMessage());
            }
        } else {
            System.out.println("Mode headless detecte : interface Swing non lancee.");
        }
    }

    // ── Helpers d'export ─────────────────────────────────────────────────────

    private static String toCsv(List<BenchmarkResult> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("structure,scenario,taille,ajout_ms,recherche_ms,suppression_ms,"
                + "inventaire_ms,comptage_ms,total_ms,memoire_ko\n");
        for (BenchmarkResult r : results) {
            sb.append(r.structure()).append(',')
              .append(r.scenario()).append(',')
              .append(r.taille()).append(',')
              .append(String.format("%.4f", r.tempsAjoutNs()      / 1e6)).append(',')
              .append(String.format("%.4f", r.tempsRechercheNs()  / 1e6)).append(',')
              .append(String.format("%.4f", r.tempsSuppressionNs()/ 1e6)).append(',')
              .append(String.format("%.4f", r.tempsInventaireNs() / 1e6)).append(',')
              .append(String.format("%.4f", r.tempsComptageNs()   / 1e6)).append(',')
              .append(String.format("%.4f", r.tempsTotalNs()      / 1e6)).append(',')
              .append(String.format("%.2f", r.memoireKo())).append('\n');
        }
        return sb.toString();
    }

    private static String toJson(List<BenchmarkResult> results) {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < results.size(); i++) {
            BenchmarkResult r = results.get(i);
            sb.append("  {\n")
              .append("    \"structure\": \"").append(r.structure()).append("\",\n")
              .append("    \"scenario\": \"").append(r.scenario()).append("\",\n")
              .append("    \"taille\": ").append(r.taille()).append(",\n")
              .append("    \"ajout_ms\": ").append(String.format("%.4f", r.tempsAjoutNs()/1e6)).append(",\n")
              .append("    \"recherche_ms\": ").append(String.format("%.4f", r.tempsRechercheNs()/1e6)).append(",\n")
              .append("    \"suppression_ms\": ").append(String.format("%.4f", r.tempsSuppressionNs()/1e6)).append(",\n")
              .append("    \"inventaire_ms\": ").append(String.format("%.4f", r.tempsInventaireNs()/1e6)).append(",\n")
              .append("    \"comptage_ms\": ").append(String.format("%.4f", r.tempsComptageNs()/1e6)).append(",\n")
              .append("    \"total_ms\": ").append(String.format("%.4f", r.tempsTotalNs()/1e6)).append(",\n")
              .append("    \"memoire_ko\": ").append(String.format("%.2f", r.memoireKo())).append("\n")
              .append("  }").append(i < results.size()-1 ? "," : "").append("\n");
        }
        sb.append("]");
        return sb.toString();
    }
    
    private static void writeFile(String path, String content) {
        try {
            java.io.File file = new java.io.File(path);
            file.getParentFile().mkdirs();
            java.io.FileWriter fw = new java.io.FileWriter(file);
            fw.write(content);
            fw.close();
            System.out.println("Fichier ecrit : " + file.getAbsolutePath());
        } catch (java.io.IOException e) {
            System.err.println("Erreur ecriture fichier : " + e.getMessage());
        }
    }

    private static boolean canLaunchUi() {
        String display = System.getenv("DISPLAY");
        return !GraphicsEnvironment.isHeadless() && display != null && !display.isBlank();
    }
}
