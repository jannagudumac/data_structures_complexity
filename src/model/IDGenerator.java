package model;

/**
 * Générateur d'IDs.
 * BUG CORRIGÉ : le compteur était statique global → les IDs continuaient
 * de s'incrémenter entre les répétitions du benchmark, provoquant des
 * removeById/findById sur des IDs jamais insérés dans le parc courant.
 * Solution : on garde le compteur statique mais on expose une méthode reset()
 * appelée au début de chaque répétition de benchmark pour repartir de 0.
 */
public class IDGenerator {
    private static int counter = 0;

    public static int generateID() {
        return ++counter;
    }

    /** Remet le compteur à zéro — à appeler avant chaque répétition de benchmark. */
    public static void reset() {
        counter = 0;
    }
}
