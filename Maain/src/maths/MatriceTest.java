package maths;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;
import java.util.List;

class MatriceTest {
    Matrice matrice;
    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        matrice = new Matrice();
        List<Integer> page0 = Arrays.asList(35, 8, 50, 12);
        matrice.insertPage(page0);

        List<Integer> page1 = Arrays.asList(103, 49);
        matrice.insertPage(page1);

        List<Integer> page2 = Arrays.asList();
        matrice.insertPage(page2);

        List<Integer> page3 = Arrays.asList(12);
        matrice.insertPage(page3);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void multiplyByVector() {
        Vecteur u = new Vecteur();
        u.insertValue(1);
        u.insertValue(1);
        u.insertValue(1);
        u.insertValue(1);

        Vecteur outVecteur = matrice.multiplyByVector(u);
        Assertions.assertArrayEquals(u.toArray(), outVecteur.toArray());
    }
}