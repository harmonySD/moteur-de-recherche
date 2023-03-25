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
 *     page2 : pas de liens          -> coeff 0
 *     page3 : lien vers 12          -> coeff 1
 *
 *  Version CLI :
 *     C = [1/4,1/4,1/4,1/4, 1/2,1/2, 1]
 *     L[0,4, 6, 6, 7]
 *     I[8,12,35,50, 103,49, 12] on trie chaque liste de lien par ordre croissant.
 *
 */
public class Matrice {
    public final int n;
    /**
     * Contiens les coefficients de chaque mot (id) dans la matrice.
     * Ils sont égaux à 1/(Nb ID dans la page).
     */
    public final List<Float> C;
    /**
     * Liste des indices de début de chaque ligne de la matrice.
     * L[i] représente le début de la ligne i. Cette ligne se fini à L[i+1]-1.
     */
    public final List<Integer> L;
    /**
     * Indice de la colonne associée au coefficient C. 
     *
     */
    public final List<Integer> I;

    public Matrice(int n ){
        this.C = new ArrayList<>();
        this.L = new ArrayList<>();
        this.I = new ArrayList<>();
        this.n=n;
    }

    /**
     * Insère les IDs, pointant vers d'autre page, d'une page dans la matrice.
     * @param pageIdList la liste des IDs pointant vers d'autre page.
     */
    public void insertPage(List<Integer> pageIdList){
        //Trier la liste
        Collections.sort(pageIdList);

        // C = 1/size.
        for(int i = 0; i < pageIdList.size(); i++){
            this.C.add(1f/pageIdList.size());
        }

        // L = Indice de début de la page.
        int indiceDebutPage = 0;
        if(this.L.size() != 0){
            indiceDebutPage =  pageIdList.size() + this.L.get(this.L.size()-1);
            this.L.add(indiceDebutPage);
        }else{
            this.L.add(indiceDebutPage);
            indiceDebutPage =  pageIdList.size() + this.L.get(this.L.size()-1);
            this.L.add(indiceDebutPage);

        }

        //I = Liste des id d'article trié.
        this.I.addAll(pageIdList);
    }

    /**
     * Multiplie cette matrice par un vecteur u.
     * Pseudo code de l'algorithme :
     *     somme = 0
     *     Pour i de 0 à |L|-2
     *      |  Pour j de L[i] à L[i+1]-1
     *      |   |  v[I[j]] += C[j]*u[i]
     *      |   |
     *      |  //Passage de A0 à A.
     *      |  Si L[i]=L[i+1]
     *      |   |  somme += 1/n*u[i]
     *     //Passage de A à Ag.
     *     somme = somme/n
 *         Pour k de 0 à n
 *          |  v[k] += somme
 *          |  v[k] = (1-epsilon)*v[k]+(epsilon/n)
     * @param u le vecteur.
     * @return le vecteur résultant de la multiplication.
     */
    public Vecteur multiplyByVector(Vecteur u){
        Vecteur v = new Vecteur(u.getNorme());
        final int n = this.L.size()-1; 
        int somme = 0;
        for(int i = 0; i < n; i++){
            for(int j = this.L.get(i); j <= this.L.get(i+1)-1; j++){

                // Termes de l'opération v[I[j]] += C[j]*u[i].
                int IAtj = this.I.get(j); // I[j]
                float valueOfv = v.getValueAt(IAtj); // v[I[j]]
                float CAtj = this.C.get(j); // C[j]
                float uAti = u.getValueAt(i); // u[i]
                // Calcul de la nouvelle valeur v[I[j]] += C[j]*u[i].
                float newValueOfv = valueOfv + (CAtj * uAti);
                // Remplacement dans le vecteur.
                v.insertOrUpdateValueAt(IAtj, newValueOfv);
            }
            //Passage de A0 à A.
            if(Objects.equals(this.L.get(i), this.L.get(i + 1))){
                somme += (1f/n) * u.getValueAt(i);
            }
        }
        somme = somme/n;
        //Passage de A à Ag.

        for(int k = 0; k < n; k++){
            float valueOfvPlusSomme = v.getValueAt(k) + somme;
            float vEpsilon = (1f - Constantes.epsilon) * valueOfvPlusSomme + (Constantes.epsilon/n);
            v.insertOrUpdateValueAt(k, vEpsilon);
        }
        return v;
    }
}
