package container;

import model.Capteur;
import model.CapteurException;
import model.TypeCapteur;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ParcCapteursLinkedList implements ParcCapteurs {
    private final LinkedList<Capteur> capteurs = new LinkedList<>();

    @Override
    public void addCapteur(Capteur capteur) throws CapteurException {
        if (capteur == null) throw new CapteurException("capteur nul");
        if (containsId(capteur.getId())) throw new CapteurException("id déjà présent");
        capteurs.add(capteur);
    }

    @Override
    public Capteur removeById(int id) throws CapteurException {
        Iterator<Capteur> it = capteurs.iterator();
        while (it.hasNext()) {
            Capteur c = it.next();
            if (c.getId() == id) {
                it.remove();
                return c;
            }
        }
        throw new CapteurException("capteur introuvable");
    }

    @Override
    public Capteur findById(int id) throws CapteurException {
        for (Capteur c : capteurs) {
            if (c.getId() == id) return c;
        }
        throw new CapteurException("capteur introuvable");
    }

    @Override
    public List<Capteur> findAll() {
        return new ArrayList<>(capteurs);
    }

    @Override
    public Map<TypeCapteur, Long> countByType() {
        Map<TypeCapteur, Long> res = new EnumMap<>(TypeCapteur.class);
        for (Capteur c : capteurs) {
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
        for (Capteur c : capteurs) if (c.getId() == id) return true;
        return false;
    }

    @Override
    public Iterator<Capteur> iterator() {
        return capteurs.iterator();
    }
}
