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
            if (gradoVertice < 1) {
                return false;
            }
        }

        return true;
    }

    /**
     * Este metodo permite verificar si un grafo tiene o no un ciclo
     * de hamilton
     * @param mAdy Matriz booleana que representa el grafo en forma de
     * matriz de adyacencia
     * @return Verdadero si el grafo tiene un ciclo de hamilton
     */
    static public boolean esHamiltoniano(boolean mAdy[][]) {
        return false;
    }

    class Visitados {

        private boolean[] visitados;
        private int nroVertices;
        private int count;

        public Visitados(int nroVertices) {
            visitados = new boolean[nroVertices];
            this.nroVertices = nroVertices;
            count = 0;
        }

        public void addVertice(int vertice) {
            visitados[vertice] = true;
            count++;
        }

        public void eraseVertice(int vertice) {
            visitados[vertice] = false;
        }

        public boolean isVisitado(int vertice) {
            return visitados[vertice];
        }
    }
}
