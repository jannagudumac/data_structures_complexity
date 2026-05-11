package container;

import model.Capteur;
import model.CapteurException;
import model.TypeCapteur;

import java.util.List;
import java.util.Map;

public interface ParcCapteurs extends Iterable<Capteur> {
	
    void addCapteur(Capteur capteur) throws CapteurException;
    
    Capteur removeById(int id) throws CapteurException;
    
    Capteur findById(int id) throws CapteurException;
    
    List<Capteur> findAll();
    
    Map<TypeCapteur, Long> countByType();
    
    int size();
    
    boolean containsId(int id);
}

