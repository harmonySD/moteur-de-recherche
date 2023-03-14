package maths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe représentant un vecteur de Float.
 */
public class Vecteur {
    /**
     * Liste contenant les valeurs du vecteur.
     */
    public List<Float> vecteur;

    public Vecteur(){
        this.vecteur = new ArrayList<>();
    }

    /**
     * Crée un vecteur de norme "norme". Le vecteur est rempli avec des 0.
     * @param norme la norme de ce vecteur.
     */
    public Vecteur(int norme){
        this.vecteur = new ArrayList<>();
        for(int i=0; i<norme; i++){
            this.vecteur.add(0f);
        }
    }

    /**
     * Insère une valeur dans le vecteur à l'index donnée.
     * Si une valeur est déjà présente à l'index donné elle est remplacée
     * par la valeur passée en paramètre sinon elle est ajoutée à la fin du vecteur.
     * @param value la valeur à ajouter/modifier dans le vecteur.
     * @param index la position d'ajout/ modification de la valeur.
     */
    public void insertValueAt(int index, float value){
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

    /**
     * @return Retourne la norme du vecteur.
     */
    public int getNorme(){
        return this.vecteur.size();
    }

    public Object[] toArray(){
        return this.vecteur.toArray();
    }
}
