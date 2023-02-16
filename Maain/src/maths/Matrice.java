package maths;

import utils.Constantes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Implémentation d'une matrice au format CLI.
 *
 * Exemple de construction :
 * Une matrice est composée de :
 *     page0 : liens 35, 8, 50, 12   -> coeff 1/4
 *     page1 : liens 103, 49         -> coeff 1/2
 *     page2 : pas de liens          -> coeff 0.
 *     page3 : lien vers 12          -> coeff 1
 *
 *  Version CLI :
 *     C = [1/4,1/4,1/4,1/4, 1/2,1/2, 1]
 *     L[0,4, 6, 6, 7]
 *     I[8,12,35,50, 103,49, 12] on trie chaque liste de lien par ordre croissant.
 *
 */
public class Matrice {
    /**
     * Contiens les coefficients de chaque mot (id) dans la matrice.
     * Ils sont égaux à 1/(Nb ID dans la page).
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
     * Multiplie cette matrice par un vecteur u.
     * Pseudo code de l'algorithme :
     *     somme = 0
     *     Pour i de 0 à |L|-2
     *         Pour j de L[i] à L[i+1]-1
     *             v[I[j]] += C[j]*u[i]
     *
     *     //Passage de A0 à A.
     *         Si L[i]=L[i+1]
     *             somme += 1/n*u[i]
     *     //Passage de A à Ag.
     *         Pour k de 0 à n
     *             v[k] += somme
     *             v[k] = (1-epsilon)*v[k]+(epsilon/n)
     * @param u le vecteur.
     * @return le vecteur résultant de la multiplication.
     */
    public Vecteur multiplyByVector(Vecteur u){
        Vecteur v = new Vecteur(u.getNorme());
        final int n = this.L.size()-2; // -2, car L est de taille n + 1.
        int somme = 0;
        for(int i = 0; i < n; i++){
            for(int j = this.L.get(i);j < this.L.get(i+1); j++){
                float valCourante = v.getValueAt(this.I.get(j));
                float valActualisee = valCourante + this.C.get(j) * u.getValueAt(i);
                v.insertValueAt(this.I.get(j), valActualisee);

                if(Objects.equals(this.L.get(i), this.L.get(i + 1))){
                    somme += (1/n)*u.getValueAt(i);
                }

                for(int k = 0; k < n; k++){
                    float val = v.getValueAt(k) + somme;
                    float valEpsilon = (1 - Constantes.epsilon)*val+(Constantes.epsilon/n);
                    v.insertValueAt(k, valEpsilon);
                }
            }
        }
        return v;
    }
}
