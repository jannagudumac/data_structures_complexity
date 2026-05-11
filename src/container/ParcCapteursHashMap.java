package container;


import model.Capteur;
import model.CapteurException;
import model.TypeCapteur;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ParcCapteursHashMap implements ParcCapteurs {
    private final HashMap<Integer, Capteur> capteurs = new HashMap<>();

    @Override
    public void addCapteur(Capteur capteur) throws CapteurException {
        if (capteur == null) throw new CapteurException("capteur nul");
        if (capteurs.containsKey(capteur.getId())) throw new CapteurException("id déjà présent");
        capteurs.put(capteur.getId(), capteur);
    }

    @Override
    public Capteur removeById(int id) throws CapteurException {
        Capteur c = capteurs.remove(id);
        if (c == null) throw new CapteurException("capteur introuvable");
        return c;
    }

    @Override
    public Capteur findById(int id) throws CapteurException {
        Capteur c = capteurs.get(id);
        if (c == null) throw new CapteurException("capteur introuvable");
        return c;
    }

    @Override
    public List<Capteur> findAll() {
        return new ArrayList<>(capteurs.values());
    }

    @Override
    public Map<TypeCapteur, Long> countByType() {
        Map<TypeCapteur, Long> res = new EnumMap<>(TypeCapteur.class);
        for (Capteur c : capteurs.values()) {
            res.merge(c.getType(), 1L, Long::sum);
        }
        return res;
    }

    @Override
    public int size() {
        return capteurs.size();
    }

    @Override
    public boolean containsId(int id) {
        return capteurs.containsKey(id);
    }

    @Override
    public Iterator<Capteur> iterator() {
        return capteurs.values().iterator();
    }
}

