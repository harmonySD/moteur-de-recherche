package maths;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant un vecteur de Float.
 */
public class Vecteur {
    /**
     * Liste contenant les valeurs du vecteur.
     */
    private List<Float> vecteur;

    public Vecteur(){
        this.vecteur = new ArrayList<>();
    }

    /**
     * Insère une valeur dans le vecteur à l'index donnée.
     * Si une valeur est déjà présente à l'index donné elle est remplacée
     * par la valeur passée en paramètre sinon elle est ajoutée à la fin du vecteur.
     * @param value la valeur à ajouter/modifier dans le vecteur.
     * @param index la position d'ajout/ modification de la valeur.
     */
    public void insertValueAt(float value, int index){
        if(this.vecteur.size() > index){
            this.vecteur.set(index, value);
        }else {
            this.vecteur.add(index, value);
        }
    }

    /**
     * Insère la valeur à la fin du vecteur.
     * @param value la valeur à insérer.
     */
    public void insertValue(float value){
        this.vecteur.add(value);
    }

    /**
     * Retourne la valeur du vecteur à l'index donné.
     * @param index l'index dont on veut récupérer la valeur dans le vecteur.
     * @return la valeur à l'index donné.
     */
    public float getValueAt(int index){
        return this.vecteur.get(index);
    }
}
