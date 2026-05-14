package benchmark;

import container.ParcCapteurs;
import container.ParcCapteursHashMap;
import container.ParcCapteursLinkedList;
import generator.CapteurGenerator;
import model.Capteur;
import model.CapteurException;
import model.IDGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Moteur de benchmark.
 *
 * Pour chaque combinaison (scénario x taille x structure), on mesure
 * séparément le temps de chaque type d'opération et la mémoire consommée.
 *
 * Protocole :
 *  - Les opérations sont proportionnelles aux pourcentages du scénario.
 *  - Le jeu de données initial est généré avec une graine fixe (reproductible).
 *  - IDGenerator est remis à zéro avant chaque répétition pour que les IDs
 *    correspondent toujours au parc courant.
 *  - Le parc est vidé puis rechargé entre chaque répétition.
 *  - La mémoire est mesurée séparément, à structure+taille fixes, via
 *    plusieurs sondes GC puis un échantillon médian pour limiter le bruit.
 */
public class BenchmarkRunner {

    private static final int NB_OPS      = 1000;
    private static final int REPETITIONS = 7;

    private final CapteurGenerator generator = new CapteurGenerator();
    private final Map<MemoryKey, Double> memoryCache = new HashMap<>();

    /** Lance tous les scenários sur toutes les tailles pour les deux structures. */
    public List<BenchmarkResult> runAll(BenchmarkScenario[] scenarios, int[] tailles) {
        List<BenchmarkResult> results = new ArrayList<>();
        for (BenchmarkScenario sc : scenarios) {
            System.out.println("\n=== Scénario : " + sc + " ===");
            for (int taille : tailles) {
                System.out.print("  n=" + taille + " ... ");
                results.add(measure("LinkedList", ParcCapteursLinkedList::new, sc, taille));
                results.add(measure("HashMap",    ParcCapteursHashMap::new,    sc, taille));
                System.out.println("OK");
            }
        }
        return results;
    }

    private BenchmarkResult measure(String name, Supplier<ParcCapteurs> parcFactory,
                                    BenchmarkScenario sc, int taille) {
        double memKo = memoryCache.computeIfAbsent(
                new MemoryKey(name, taille),
                key -> measureMemory(parcFactory, taille)
        );

        int nbAjout      = NB_OPS * sc.ajoutPct()      / 100;
        int nbRetrait    = NB_OPS * sc.retraitPct()    / 100;
        int nbRecherche  = NB_OPS * sc.recherchePct()  / 100;
        int nbInventaire = NB_OPS * sc.inventairePct() / 100;
        int nbComptage   = NB_OPS - nbAjout - nbRetrait - nbRecherche - nbInventaire;

        long totalAjout = 0, totalRecherche = 0, totalSuppression = 0,
             totalInventaire = 0, totalComptage = 0;

        ParcCapteurs parc = parcFactory.get();
        forceGc();

        for (int rep = 0; rep < REPETITIONS; rep++) {
            IDGenerator.reset();
            List<Capteur> base = generator.generer(taille, 1234L);

            viderParc(parc, base);
            IDGenerator.reset();
            base = generator.generer(taille, 1234L);
            remplirParc(parc, base);

            Random rng = new Random(5678L + rep);

            // AJOUT
            long t0 = System.nanoTime();
            for (int i = 0; i < nbAjout; i++) {
                try { parc.addCapteur(generator.genererCapteur(rng)); }
                catch (CapteurException ignored) { }
            }
            totalAjout += System.nanoTime() - t0;

            // RECHERCHE
            t0 = System.nanoTime();
            for (int i = 0; i < nbRecherche; i++) {
                int id = base.get(rng.nextInt(base.size())).getId();
                try { parc.findById(id); }
                catch (CapteurException ignored) { }
            }
            totalRecherche += System.nanoTime() - t0;

            // SUPPRESSION
            t0 = System.nanoTime();
            for (int i = 0; i < nbRetrait; i++) {
                int id = base.get(rng.nextInt(base.size())).getId();
                try { parc.removeById(id); }
                catch (CapteurException ignored) { }
            }
            totalSuppression += System.nanoTime() - t0;

            // INVENTAIRE
            t0 = System.nanoTime();
            for (int i = 0; i < nbInventaire; i++) parc.findAll();
            totalInventaire += System.nanoTime() - t0;

            // COMPTAGE
            t0 = System.nanoTime();
            for (int i = 0; i < nbComptage; i++) parc.countByType();
            totalComptage += System.nanoTime() - t0;
        }

        int r = Math.max(1, REPETITIONS);
        return new BenchmarkResult(
                name, sc.nom(), taille,
                totalAjout       / r,
                totalRecherche   / r,
                totalSuppression / r,
                totalInventaire  / r,
                totalComptage    / r,
                memKo
        );
    }

    /**
     * Mesure le surcoût mémoire du conteneur une fois peuplé avec un jeu
     * de capteurs fixe. La mesure reste indépendante du scénario pour éviter
     * d'introduire du bruit lié aux opérations du benchmark.
     */
    private double measureMemory(Supplier<ParcCapteurs> parcFactory, int taille) {
        // Premier essai ignoré : il absorbe surtout le bruit de chargement
        // de classes et d'allocations initiales de la JVM.
        measureMemoryOnce(parcFactory, taille);

        double[] samples = new double[3];
        for (int probe = 0; probe < samples.length; probe++) {
            samples[probe] = measureMemoryOnce(parcFactory, taille);
        }
        Arrays.sort(samples);
        return samples[1];
    }

    private double measureMemoryOnce(Supplier<ParcCapteurs> parcFactory, int taille) {
        IDGenerator.reset();
        List<Capteur> base = generator.generer(taille, 1234L);

        forceGc();
        long memAvant = usedMemory();

        ParcCapteurs parc = parcFactory.get();
        remplirParc(parc, base);

        forceGc();
        long memApres = usedMemory();
        double memKo = Math.max(0, memApres - memAvant) / 1024.0;

        base = null;
        parc = null;
        forceGc();
        return memKo;
    }

    private void remplirParc(ParcCapteurs parc, List<Capteur> base) {
        try {
            for (Capteur c : base) {
                parc.addCapteur(c);
            }
        } catch (CapteurException e) {
            throw new RuntimeException("Erreur remplissage : " + e.getMessage(), e);
        }
    }

    private void viderParc(ParcCapteurs parc, List<Capteur> base) {
        for (Capteur c : base) {
            try { parc.removeById(c.getId()); }
            catch (CapteurException ignored) { }
        }
    }

    private void forceGc() {
        System.gc();
        System.runFinalization();
    }

    private long usedMemory() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    private record MemoryKey(String structure, int taille) { }
}
