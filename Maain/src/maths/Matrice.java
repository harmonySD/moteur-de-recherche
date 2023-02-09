package maths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implémentation d'une matrice au format CLI.
 */
public class Matrice {
    /**
     * Contiens les coefficients de chaque mot (id) dans la matrice.
     */
    private final List<Float> C;
    /**
     * Liste des indices de début de chaque ligne de la matrice.
     * L[i] représente le début de la ligne i. Cette ligne se fini à L[i+1]-1.
     */
    private final List<Integer> L;
    /**
     * Indice de la colonne associée au coefficient C.
     */
    private final List<Integer> I;

    public Matrice(){
        this.C = new ArrayList<>();
        this.L = new ArrayList<>();
        this.I = new ArrayList<>();
    }

    /**
     * Insère les IDs, pointant vers d'autre page, d'une page dans la matrice.
     * @param pageIdList la liste des IDs pointant vers d'autre page.
     */
    public void insertPage(List<Integer> pageIdList){
        //Trier la liste
        Collections.sort(pageIdList);
        //C = 1/size.
        for(int i = 0; i < pageIdList.size(); i++){
            this.C.add(1f/pageIdList.size());
        }
        //L = Indice de début de la page.
        this.L.add(pageIdList.size() + this.L.get(this.L.size()-1));
        //I = Liste des id d'article trié.
        this.I.addAll(pageIdList);
    }

    /**
     * Multiplie cette matrice par un vecteur.
     * @param u le vecteur.
     * @return le vecteur résultant de la multiplication.
     */
    public Vecteur multiplyByVector(Vecteur u){
        //TODO
        // Il faut aussi transformer tous les 0 en 1/n et aussi prendre en compte epsilon.
        /*  Pour i de 0 à |L|-1
                Pour j de L[i] à L[i+1]-1
                    v[...] += C[j]*u[i]
         */
        return new Vecteur();
    }
}
