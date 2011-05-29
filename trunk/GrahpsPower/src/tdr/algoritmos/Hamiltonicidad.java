package tdr.algoritmos;

import tdr.vista.GraphPanel;

/**
 *
 * @author Alejandro Perez
 * @author Sebastian Ramirez
 */
public class Hamiltonicidad {

    private static boolean mAdy[][];
    //  private static boolean copiaMAdy[][];
    private static short nroVertices;
    // private static short vertActual;
    private static short[] visitados;
    private static short conteo = -1;
    private static boolean mAuxADY[][] = {{false, true, true, true, false},
        {true, false, false, true, true}, {true, false, false, false, true},
        {true, true, false, false, false}, {false, true, true, false, false}};

    /**
     * Comprueba si un grafo representado por una matriz de adyacencia es hamiltoniano
     * @param mAdy Matriz booleana con la representacion del grafo en matriz de adyacencia
     * @return Verdadero si el grafo es hamiltoniano (Posee un ciclo de hamilton)
     */
    public static boolean isHamiltoniano(boolean mAdy[][]) {
        if (mAdy.length < 3) {//Si el grafo tiene menos de 3 vertices no es hamiltoniano
            return false;
        }
        Hamiltonicidad.mAdy = mAdy;
        nroVertices = Short.valueOf(String.valueOf(mAdy.length));
        visitados = new short[nroVertices];
        for (short j = 0; j < nroVertices; j++) {
            visitados[j] = -1;
        }
        conteo = -1;
        short verticeInicial = 0;
        agregarVertice(verticeInicial);//Agregamos el primer vertice a el camino
        return comprobarCicloHamiltoniano(verticeInicial, mAdy[verticeInicial]);//Si encontro un ciclo retornar verdadero

    }

    /**
     * Comprueba si existe un ciclo hamiltoniano utilizando la tecnica de backtracking
     * @param verticeActual indice en la mAdy del Vertice de partida del recorrido
     * @param vertice Vector de adyacencias del vertice actual
     * @return Verdadero si existe un ciclo de hamilton
     */
    private static boolean comprobarCicloHamiltoniano(short verticeActual, boolean[] vertice) {
        //  System.out.println("Vertice actual = " + verticeActual);
        short sigVertice = -1;//Indice del vertice siguiente
        boolean aux[] = new boolean[vertice.length];//Siguiente vertice representado como array booleano
        if (isCiclo()) {//Invocamos al metodo isCiclo, que comprueba si hay una solucion
            //    System.out.println("TIENE CICLO HAMILTONIANO " + (visitados[0] + 1));
            return true;
        }
        System.arraycopy(vertice, 0, aux, 0, aux.length);//C
        sigVertice = escogerVertice(aux);
        //    System.out.println("Vertice escogido = " + sigVertice);
        while (sigVertice != -1) {
            agregarVertice(sigVertice);
            aux[sigVertice] = false;//Borramos el vertice escogido de el vector de adyacencias
            if (comprobarCicloHamiltoniano(sigVertice, mAdy[sigVertice])) {
                return true;
            }
            borrarVertice();//Si llegamos aca es porque el camino es erroneo
            //Se borra el ultimo vertice ingresado
            sigVertice = escogerVertice(aux);//Traemos el siguiente vertice
            //candidato para tratar de hallar una solucion
        }


        return false;
    }

    /**
     * Escoge el vertice siguiente para construir el arbol
     * (Un vertice que no haya sido visitado)
     * @param vertActual Vertice actual del algoritmo
     * @return posicion en la matriz de adyacencia del proximo vertice no visitado
     */
    private static short escogerVertice(boolean vertice[]) {
        for (short i = 0; i < nroVertices; i++) {
            if (vertice[i]) {
                if (!isVisitado(i)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Permite saber si un vertice ha sido visitado
     * @param vertice Vertice que se desea consultar
     * @return Verdadero si el grafo ha sido visitado
     */
    private static boolean isVisitado(short vertice) {
        for (short i : visitados) {
            if (i == vertice) {
                return true;
            }
        }
        return false;
    }

    /**
     * Permite saber si se completo un ciclo hamiltoniano
     * @return Verdadero si el ciclo esta completo
     */
    private static boolean isCiclo() {
        return (isVerticeAdy(visitados[nroVertices - 1], visitados[0]));
        //Invoca el metodo isVerticeAdy con la ultima posicion del array fin del arbol y la posicion 0 del array,(vertice de partida)
    }

    /**
     * Permite saber si un vertice es adyacente a otro
     * @param vertOrigen Vertice de partida
     * @param vertDestino Vertice de llegada
     * @return Verdadero si los vertices son adyacentes
     */
    private static boolean isVerticeAdy(short vertOrigen, short vertDestino) {
        // System.out.println("Vertice origen = " + vertOrigen + ", Vertice Destino = " + vertDestino);
        if (vertOrigen == -1) {//si es -1 significa que no hemos iniciado el array de visitados
            return false;
        }
        return (mAdy[vertOrigen][vertDestino]);
    }

    /**
     * Agrega un vertice al vector de visitados
     * @param vert Vertice que se agrega al vector de visitados
     */
    private static void agregarVertice(short vert) {
        visitados[++conteo] = vert;
    }

    /**
     * Borra el ultimo vertice, ya que conteo es el indice del vector de visitados
     */
    private static void borrarVertice() {
        visitados[conteo--] = -1;
    }

    /**
     * Este método permite determinar los vértices que se han visitado cuando se
     * recorre el grafo.
     * @return El vector de vértices que han sido visitados durante la ejecución
     * del programa.
     */
    public static short[] getVisitados() {
        return visitados;
    }

    static public void main(String args[]) {
        //System.out.println("Hamiltoniano =" + isHamiltoniano(GraphPanel.generarMatrizAdyAleatoria(100)));
        mAdy = mAuxADY;
        printADY();
        System.out.println("Aristas +>" + getNroAristas());
        System.out.println("Comprobacion por vertices =>" + comprobarGradoDeVertices());
        System.out.println("Comprobacion por nro de aristas +>" + comprobarNroAristas());
        System.out.println("Grado del vertice 4 +>" + getGradoVertice((short) 3));
        System.out.println("Comprobacion por vertices no conectados"+comprobarGradoVerticesNoConectados());

    }

    /**
     * Imprime en consola el vector de Caminos
     */
    public static void imprimirCamino() {
        System.out.print("Camino => [ ");
        for (int i = 0; i < visitados.length; i++) {
            System.out.print(visitados[i] + 1 + " * ");
        }
        System.out.println(" ]");
        System.out.println("Conteo => " + conteo);

    }

    private static void printADY() {
        for (boolean[] bs : mAdy) {
            for (boolean b : bs) {
                System.out.print(b ? "1" : "0");
            }
            System.out.println("");
        }
    }

    /***************************************************************************************
     *Condiciones necesarias y suficientes                                                                                 *
     ***************************************************************************************/
    /**
     * Proposición 4.5.3. Sea G=(V, E) un grafo simple. Si V = n >=3 y
    deg(v) >=n / 2 para todo v en V , entonces G es hamiltoniano.
     */
    /**
     * Comprobando proposicion 1
     */
    private static boolean comprobarGradoDeVertices() {
        short vertices_CMP = getNroVertices();
        if (vertices_CMP >= 3) {
            vertices_CMP = (short) (vertices_CMP / 2);
            for (boolean[] bs : mAdy) {
                short deg = 0;
                for (boolean b : bs) {
                    if (b) {
                        deg++;
                    }
                }
                if (deg < vertices_CMP) {
                    return false;
                }
            }
        } else {
            return false;
        }

        return true;
    }

    /**
     * Proposición 4.5.4. Un grafo simple con n vértices que tiene al menos
     * 0.5(n-2)(n-1)+2 aristas es hamiltoniano
     */
    /**
     * Comprobacion de la proposicion 2
     * @return Verdadero si el grafo cumple
     */
    private static boolean comprobarNroAristas() {
        short vertices = getNroVertices();
        return ((0.5 * (vertices - 1) * (vertices - 2) + 2) <= getNroAristas() ? true : false);
    }

    /**
     * Proposición 4.5.5. Sea G=(V, A) un grafo simple con V =n >=3 . 
     * Si para todo par de vértices u y v que no están conectados se cumple que
     * deg(u) +deg(v) >=n , entonces G es hamiltoniano.
     */
    private static boolean comprobarGradoVerticesNoConectados() {
        short vertices = getNroVertices();
        for (short i = 0; i < mAdy.length; i++) {
            short gradoU = getGradoVertice(i);
            for (short j = (short) (i + 1); j < mAdy.length; j++) {//Se leen solo los valores
                //Arriba de la diagonal ppal
                if (!mAdy[i][j]) {
                    short gradoV = getGradoVertice(j);
                    if (gradoU + gradoV < vertices) {
                        System.out.println("i,j"+i+","+j);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * ME encargo de esto por el momento
     * @return 
     */
    
    
    private static boolean tieneVertCorte(){
        int a =cantCompConexas(mAdy);
        for (int i = 0; i < mAdy.length; i++) {
            int b=cantCompConexas(quitarVertice(i));
            if(b>a)
                return true;
            
        }
        return false;
    }
    
    private static int cantCompConexas(boolean [][] matriz){
        return 1;
    }
    
    public static boolean [][] quitarVertice(int vertice){
        boolean [][] matAux=new boolean[mAdy.length-1][mAdy.length-1];
        return matAux;
    }
    
    /**
     * METODOS GENERALES
     */
    /**
     * Este metodo indica cuantos vertices tiene un grafo
     * representado como matriz de adyacencia
     * @return numero de vertices del grafo
     */
    private static short getNroVertices() {
        return (short) mAdy.length;
    }

    /**
     * Este metodo retorna el numero de aristas que posee un grafo representado
     * como matris de adyacencia booleana
     * @return Numero de aristas del grafo.
     */
    private static short getNroAristas() {
        short counter = 0;
        for (int i = 0; i < mAdy.length; i++) {
            for (int j = i + 1; j < mAdy.length; j++) {//Se leen solo los valores
                if (mAdy[i][j]) {                        //Arriba de la diagonal ppal
                    counter++;
                }
            }
        }
        return counter;
    }

    /**
     * Retorna el grado de un vertice
     * @param vertice Indice del vertice en la matriz ady
     * @return El grado del vertice
     */
    private static short getGradoVertice(short vertice) {
        short count = 0;
        for (boolean b : mAdy[vertice]) {
            if (b) {
                count++;
            }
        }
        return count;
    }
}
