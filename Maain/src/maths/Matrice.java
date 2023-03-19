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
        // Collections.sort(pageIdList);

        // C = 1/size. //plus maintenant 
        // C = 1/ (size-nb de 0)
        // for(int i = 0; i < pageIdList.size(); i++){
        //     this.C.add(1f/pageIdList.size());
        // }

        // PLUS rempli I indice des id non nul 
        int cmp=0;//compteur de 0 
        for(int i = 0; i < pageIdList.size(); i++){
            if(pageIdList.get(i)==0){
                cmp++;
            }else{
                this.I.add(i);
            }
        }
        if(cmp!=n){
            for(int i=0; i< pageIdList.size()-cmp; i++){
                this.C.add(1f/(pageIdList.size()-cmp));
            }
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

        //FAUX
        //I = Liste des id d'article trié.
        // this.I.addAll(pageIdList);



        


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
        System.out.println("u norme"+u.getNorme());
        // u.getValueAt(0);
        // System.out.println("ok");
        // u.getValueAt(173718);
        // System.out.println("ok");
        // u.getValueAt(173719);
        System.out.println("norme c"+this.C.size());
        System.out.println("norme i"+this.I.size());
        Vecteur v = new Vecteur(u.getNorme());
        final int n = this.L.size()-1; // -2, car L est de taille n + 1.
        System.out.println("n"+n);
        int somme = 0;
        for(int i = 0; i < n; i++){
            for(int j = this.L.get(i);j <= this.L.get(i+1)-1; j++){
                float valCourante = v.getValueAt(i); 
                // System.out.println("test "+(this.L.get(i+1)-1));
                // System.out.println("c"+this.C.get(j));
                // System.out.println("i"+this.I.get(j+1));
                // System.out.println("i"+this.I.get(j+2));
                // System.out.println("u"+u.getValueAt(this.I.get(j)));
                float valActualisee = valCourante + this.C.get(j) * u.getValueAt(this.I.get(j));
                // float valActualisee = valCourante + this.C.get(j) * u.getValueAt(i);
                v.insertValueAt(i, valActualisee);
                // v.insertValueAt(this.I.get(j), valActualisee);

                //Passage de A0 à A.
                if(Objects.equals(this.L.get(i), this.L.get(i + 1))){
                    somme += (1f/n)*u.getValueAt(i);
                }

                //Passage de A à Ag.
                // System.out.println("normee"+v.getNorme());
                // System.out.println(n);
                for(int k = 0; k < n; k++){
                    // System.out.println ("k"+k);

                    // System.out.println(v.getNorme());
                    float val = v.getValueAt(k) + somme;
                    // System.out.println("v"+val);
                    float valEpsilon = (1f - Constantes.epsilon)*val+(Constantes.epsilon/n);
                    v.insertValueAt(k, valEpsilon);
                }
            }
        }
        return v;
    }
}
