/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tdr.algoritmos;

/**
 *
 * @author Lis
 * Clase con metodos de acceso estaticos publicos para comprobar si un grafo
 * tiene ciclos hamiltonianos
 */
public class CicloHamiltoniano {
/**
 * Este metodo permite aplicar la condicion suficiente:
 * Mayor de tres vertices, con grado de cada vertice >= n/2
 * @param mAdy
 * @return Verdadero si se cumple la condicion
 */
    public static boolean comprobarPorGrado(boolean mAdy[][]) {
        int gradoVertice, minGrado = mAdy.length / 2;
        if (mAdy.length < 3) {
            return false;
        }
        for (boolean[] bs : mAdy) {
            gradoVertice = 0;
            for (boolean b : bs) {
                if (b) {
                    gradoVertice++;
                }
            }
            if (gradoVertice < minGrado) {
                return false;
            }
        }

        return true;
    }
/**
 * Comprueba si el grafo es conexo o no
 * @param mAdy
 * @return verdadero si el grafo es conexo
 */

    public static boolean comprobarConexidad(boolean mAdy[][]) {
        int gradoVertice, minGrado = mAdy.length / 2;
        for (boolean[] bs : mAdy) {
            gradoVertice = 0;
            for (boolean b : bs) {
                if (b) {
                    gradoVertice++;
                }
            }
            if (gradoVertice <1) {
                return false;
            }
        }

        return true;
    }
}
