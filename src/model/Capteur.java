package model;

import java.util.Objects;

public class Capteur {
	
	//Attributs
    private final int id;
    private final TypeCapteur type;
    private final String localisation;
    private boolean isActive;
    private double valeurCourante=-1;

    //Constructeurs
    
    public Capteur(TypeCapteur type, String localisation, boolean actif, double valeurCourante) {
        this.id = IDGenerator.generateID();
        this.type = type;
        this.localisation = localisation;
        this.isActive = actif;
        this.valeurCourante = valeurCourante;
    }

    //Accesseurs
    
    public int getId() {
    	return id; }
    
    public TypeCapteur getType() {
    	return type; }
    
    public String getLocalisation() {
    	return localisation; }
    
    public boolean getIsActive() { 
    	return isActive; }
    
    public double getValeurCourante() { 
    	return valeurCourante; }
    
    public void setIsActive(boolean actif) { 
    	this.isActive = actif; }
    
    public void setValeurCourante(double valeurCourante) { 
    	this.valeurCourante = valeurCourante; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (!(o instanceof Capteur capteur)) {return false;}
        return id == capteur.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return "Capteur{id=" + id + ", type=" + type + ", localisation='" + localisation + "', actif=" + isActive + ", valeur=" + valeurCourante + "}";
    }
    
}
