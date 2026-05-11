package generator;

import model.Capteur;
import model.TypeCapteur;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CapteurGenerator {
    private static final String[] LIEUX = {
            "Salle A", "Salle B", "Couloir", "Laboratoire", "Toit",
            "Entrepot", "Bureau", "Hall", "Serveur", "Parking"
    };

    public List<Capteur> generer(int n, long seed) {
        Random random = new Random(seed);
        List<Capteur> list = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            list.add(genererCapteur(random));
        }
        return list;
    }

    public Capteur genererCapteur(Random random) {
        TypeCapteur type = TypeCapteur.values()[random.nextInt(TypeCapteur.values().length)];
        String localisation = LIEUX[random.nextInt(LIEUX.length)];
        boolean actif = random.nextBoolean();
        double valeur = switch (type) {
            case TEMPERATURE -> 10 + random.nextDouble() * 25;
            case HUMIDITE -> random.nextDouble() * 100;
            case PRESSION -> 950 + random.nextDouble() * 100;
            case LUMINOSITE -> random.nextDouble() * 1000;
            case CO2 -> 300 + random.nextDouble() * 1200;
            case MOUVEMENT -> random.nextBoolean() ? 1.0 : 0.0;
        };
        return new Capteur(type, localisation, actif, valeur);
    }
}

