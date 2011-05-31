package tdr.vista;

import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.*;
import tdr.algoritmos.Hamiltonicidad;

/** @author John B. Matthews; distribution per GPL. */
public class GraphPanel extends JPanel
        implements ActionListener, ChangeListener {

    private static final int WIDE = 580;
    private static final int HIGH = 480;
    private static final int RADIUS = 15;
    protected ControlPanel control;
    private int radius = RADIUS;
    private Kind kind = Kind.Circular;
    private JComboBox kindBox = new JComboBox();
    private List<Node> nodes = new ArrayList<Node>();
    private List<Node> selected = new ArrayList<Node>();
    private List<Edge> edges = new ArrayList<Edge>();
    private Point mousePt = new Point(WIDE / 2, HIGH / 2);
    private Rectangle mouseRect = new Rectangle();
    private boolean selecting = false;
    private int letra = 1;

    GraphPanel() {
        this.setPreferredSize(new Dimension(WIDE, HIGH));
        this.control = new ControlPanel();
        this.addMouseListener(new MouseHandler());
        this.addMouseMotionListener(new MouseMotionHandler());
    }

    /**
     * Constructor para mostrar el ciclo hamiltoniano encontrado
     * @param camino Vertor con la trayectoria de los vertices
     * @param gp GraphPanel con el que se construyo el grafo
     */
    GraphPanel(short[] camino, GraphPanel gp) {
        this.addMouseListener(new MouseHandler());
        this.addMouseMotionListener(new MouseMotionHandler());
        this.setPreferredSize(new Dimension(WIDE, HIGH));
        this.nodes = gp.nodes;
        for (int i = 0; i < camino.length; i++) {
            if (i < camino.length - 1) {

                Node n1 = nodes.get(camino[i]);
                if (i == 0) {
                    boolean aux = n1.isSelected();
                    n1.setSelected(true);
                    n1.updateColor(nodes, Color.black);
                    n1.setSelected(aux);
                }
                Node n2 = nodes.get(camino[i + 1]);
                edges.add(new Edge(n1, n2));
            } else if (i == camino.length - 1) {
                Node n1 = nodes.get(camino[i]);
                Node n2 = nodes.get(camino[0]);
                edges.add(new Edge(n1, n2));
            }
        }
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(new Color(0x00f0f0f0));
        g.fillRect(0, 0, getWidth(), getHeight());
        for (Edge e : edges) {
            e.draw(g);
        }
        for (Node n : nodes) {
            n.draw(g);
        }
        if (selecting) {
            g.setColor(Color.darkGray);
            g.drawRect(mouseRect.x, mouseRect.y,
                    mouseRect.width, mouseRect.height);
        }
    }

    public void actionPerformed(ActionEvent e) {
        Color color = control.colorIcon.getColor();
        String cmd = e.getActionCommand();
        if ("Limpiar".equals(cmd)) {
            nodes.clear();
            edges.clear();
            letra = 1;
        } else if ("Color".equals(cmd)) {
            color = JColorChooser.showDialog(
                    this, "Escoger un color", color);
            if (color != null) {
                Node.updateColor(nodes, color);
                control.colorIcon.setColor(color);
                control.repaint();
            }
        } else if ("Conectar".equals(cmd)) {
            Node.getSelected(nodes, selected);
            if (selected.size() > 1) {
                for (int i = 0; i < selected.size() - 1; ++i) {
                    Node n1 = selected.get(i);
                    Node n2 = selected.get(i + 1);
                    edges.add(new Edge(n1, n2));
                }
            }
        } else if ("Borrar Arista".equals(cmd)) {
            borrarVertices();

        } else if ("Borrar".equals(cmd)) {
            deleteSelected();
        } else if ("Comprobar Hamiltonicidad".equals(cmd)) {
            // long timeIni = System.currentTimeMillis();
            Hamiltonicidad.setMAdy(getGrafoMatrizAdy());
            System.out.println("Vertice de corte"+Hamiltonicidad.tieneVertCorte());
            System.out.println("comprobar grado vertices"+Hamiltonicidad.comprobarGradoDeVertices());
            System.out.println("vertices no conectados"+Hamiltonicidad.comprobarGradoVerticesNoConectados());
            System.out.println("comprobar nro aristas"+Hamiltonicidad.comprobarNroAristas());
            if( Hamiltonicidad.tieneVertCorte() || !(Hamiltonicidad.comprobarGradoDeVertices() || Hamiltonicidad.comprobarGradoVerticesNoConectados()||Hamiltonicidad.comprobarNroAristas())){
                JOptionPane.showMessageDialog(this, "No es hamiltoniano");
            }else{
            //long timeFin = System.currentTimeMillis();
                System.out.println("Evalua el algoritmo exaustivo");
                boolean res = tdr.algoritmos.Hamiltonicidad.isHamiltoniano();
                JOptionPane.showMessageDialog(null, (res ? new GraphPanel(Hamiltonicidad.getVisitados(), this) : "El Grafo no es Hamiltoniano"));
            }
            //    JOptionPane.showMessageDialog(null, "Tiempo de Ejecucion " + ((timeFin - timeIni) / 1000.0000f) + " Segundos");
            // JOptionPane.showMessageDialog(this, (res ? "El grafo es conexo" : "El grafo no es conexo"));
        } else if ("Tipo".equals((cmd))) {
            kind = (Kind) kindBox.getSelectedItem();
            Node.updateKind(nodes, kind);
        } else if ("Nuevo".equals(cmd)) {
            Node.selectNone(nodes);
            Point p = mousePt.getLocation();
            Node n = new Node(p, radius, color, kind, "V" + String.valueOf(letra++));
            n.setSelected(true);
            nodes.add(n);
        } else if ("ListaConexiones".equals(cmd)) {
            /* for (Iterator<Edge> it = edges.iterator(); it.hasNext();) {
            Edge edge = it.next();
            System.out.print("(" + edge.getArista()[0] + "," + edge.getArista()[1] + "), ");
            }
            System.out.println("");*/
            String nroVertices = JOptionPane.showInputDialog("Cantidad de vertices del grafo");
            boolean[][] mAdy = (nroVertices != null /*   System.out.println("");
                    for (boolean[] bs : mAdy) {
                    for (boolean b : bs) {
                    System.out.print(b ? 1 : 0);
                    }
                    System.out.println("");
                    }*/)
                    ? generarMatrizAdyAleatoria(Integer.parseInt(nroVertices)) : getGrafoMatrizAdy();
            if (mAdy == null) {
                return;
            }

            guardarGrafo(mAdy);

        } else {
            for (Kind k : Kind.values()) {
                if (k.toString().equals(cmd)) {
                    kindBox.setSelectedItem(k);
                }
            }
        }
        this.repaint();
    }

    private void deleteSelected() {
        ListIterator<Node> iter = nodes.listIterator();
        while (iter.hasNext()) {
            Node n = iter.next();
            if (n.isSelected()) {
                deleteEdges(n);
                iter.remove();
                letra--;
                ListIterator<Node> it = nodes.listIterator();
                int num = 1;
                while (it.hasNext()) {
                    Node node = it.next();
                    node.etiqueta = "V" + String.valueOf(num);
                    num++;
                }
            }
        }
        this.repaint();
    }

    private void deleteEdges(Node n) {
        ListIterator<Edge> iter = edges.listIterator();
        while (iter.hasNext()) {
            Edge e = iter.next();
            if (e.n1 == n || e.n2 == n) {
                iter.remove();
            }
        }
    }

    public void stateChanged(ChangeEvent e) {
        JSpinner s = (JSpinner) e.getSource();
        radius = (Integer) s.getValue();
        Node.updateRadius(nodes, radius);
        this.repaint();
    }

    private class MouseHandler extends MouseAdapter {

        @Override
        public void mouseReleased(MouseEvent e) {
            selecting = false;
            mouseRect.setBounds(0, 0, 0, 0);
            if (e.isPopupTrigger()) {
                showPopup(e);
            }
            e.getComponent().repaint();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            mousePt = e.getPoint();
            if (e.isShiftDown()) {
                Node.selectToggle(nodes, mousePt);
            } else if (e.isPopupTrigger()) {
                Node.selectOne(nodes, mousePt);
                showPopup(e);
            } else if (Node.selectOne(nodes, mousePt)) {
                selecting = false;
            } else {
                Node.selectNone(nodes);
                selecting = true;
            }
            e.getComponent().repaint();
        }

        private void showPopup(MouseEvent e) {
            control.popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    private class MouseMotionHandler extends MouseMotionAdapter {

        Point delta = new Point();

        @Override
        public void mouseDragged(MouseEvent e) {
            if (selecting) {
                mouseRect.setBounds(
                        Math.min(mousePt.x, e.getX()),
                        Math.min(mousePt.y, e.getY()),
                        Math.abs(mousePt.x - e.getX()),
                        Math.abs(mousePt.y - e.getY()));
                Node.selectRect(nodes, mouseRect);
            } else {
                delta.setLocation(
                        e.getX() - mousePt.x,
                        e.getY() - mousePt.y);
                Node.updatePosition(nodes, delta);
                mousePt = e.getPoint();
            }
            e.getComponent().repaint();
        }
    }

    protected class ControlPanel extends JPanel {

        protected JButton newButton = new JButton("Nuevo");
        private ColorIcon colorIcon = new ColorIcon(Color.blue);
        private JPopupMenu popup = new JPopupMenu();

        ControlPanel() {
            this.setLayout(new FlowLayout(FlowLayout.LEFT));
            this.setBackground(Color.lightGray);
            newButton.addActionListener(GraphPanel.this);
            this.add(newButton);
            JButton clearButton = new JButton("Limpiar");
            clearButton.addActionListener(GraphPanel.this);
            this.add(clearButton);
            for (Kind k : Kind.values()) {
                kindBox.addItem(k);
            }
            kindBox.setActionCommand("Kind");
            kindBox.addActionListener(GraphPanel.this);
            this.add(kindBox);
            JButton colorButton = new JButton("Color");
            colorButton.addActionListener(GraphPanel.this);
            this.add(colorButton);
            this.add(new JLabel(colorIcon));
            JSpinner js = new JSpinner();
            js.setModel(new SpinnerNumberModel(RADIUS, 5, 100, 5));
            js.addChangeListener(GraphPanel.this);
            this.add(new JLabel("Size:"));
            this.add(js);

            JMenuItem menuItem = new JMenuItem("Nuevo");
            menuItem.addActionListener(GraphPanel.this);
            popup.add(menuItem);
            menuItem = new JMenuItem("Color");
            menuItem.addActionListener(GraphPanel.this);
            popup.add(menuItem);
            menuItem = new JMenuItem("Conectar");
            menuItem.addActionListener(GraphPanel.this);
            popup.add(menuItem);
            menuItem = new JMenuItem("Borrar");
            menuItem.addActionListener(GraphPanel.this);
            popup.add(menuItem);
            menuItem = new JMenuItem("Borrar Arista");
            menuItem.addActionListener(GraphPanel.this);
            popup.add(menuItem);
            menuItem = new JMenuItem("Comprobar Hamiltonicidad");
            menuItem.addActionListener(GraphPanel.this);
            popup.add(menuItem);

            JMenu subMenu = new JMenu("Tipo");
            for (Kind k : Kind.values()) {
                menuItem = new JMenuItem(k.toString());
                menuItem.addActionListener(GraphPanel.this);
                subMenu.add(menuItem);
            }
            popup.add(subMenu);
        }
    }

    /** The kinds of node in a graph. */
    private enum Kind {

        Circular, Rounded, Square
    }

    /** An Edge is a pair of Nodes. */
    private static class Edge {

        private Node n1;
        private Node n2;

        public Edge(Node n1, Node n2) {
            this.n1 = n1;
            this.n2 = n2;
        }

        public void draw(Graphics g) {
            Point p1 = n1.getLocation();
            Point p2 = n2.getLocation();
            g.setColor(Color.darkGray);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }

        String[] getArista() {
            return new String[]{n1.getEtiqueta(), n2.getEtiqueta()};
        }
    }

    /** A Node represents a node in a graph. */
    private static class Node {

        private Point p;
        private int r;
        private Color color;
        private Kind kind;
        private boolean selected = false;
        private Rectangle b = new Rectangle();
        private String etiqueta;

        /** Construct a new node. */
        public Node(Point p, int r, Color color, Kind kind, String etiqueta) {
            this.p = p;
            this.r = r;
            this.color = color;
            this.kind = kind;
            setBoundary(b);
            this.etiqueta = etiqueta;
        }

        public String getEtiqueta() {
            return etiqueta;
        }

        /** Calculate this node's rectangular boundary. */
        private void setBoundary(Rectangle b) {
            b.setBounds(p.x - r, p.y - r, 2 * r, 2 * r);
        }

        /** Draw this node. */
        public void draw(Graphics g) {
            g.setColor(this.color);
            if (this.kind == Kind.Circular) {
                g.fillOval(b.x, b.y, b.width, b.height);
            } else if (this.kind == Kind.Rounded) {
                g.fillRoundRect(b.x, b.y, b.width, b.height, r, r);
            } else if (this.kind == Kind.Square) {
                g.fillRect(b.x, b.y, b.width, b.height);
            }
            if (selected) {
                g.setColor(Color.darkGray);
                g.drawRect(b.x, b.y, b.width, b.height);
            }
            g.setColor(Color.white);
            g.drawString(etiqueta, b.x + b.width / 2 - 6, b.y + b.height / 2 + 3);

        }

        /** Return this node's location. */
        public Point getLocation() {
            return p;
        }

        /** Return true if this node contains p. */
        public boolean contains(Point p) {
            return b.contains(p);
        }

        /** Return true if this node is selected. */
        public boolean isSelected() {
            return selected;
        }

        /** Mark this node as slected. */
        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        /** Collected all the selected nodes in list. */
        public static void getSelected(List<Node> list, List<Node> selected) {
            selected.clear();
            for (Node n : list) {
                if (n.isSelected()) {
                    selected.add(n);
                }
            }
        }

        /** Select no nodes. */
        public static void selectNone(List<Node> list) {
            for (Node n : list) {
                n.setSelected(false);
            }
        }

        /** Select a single node; return true if not already selected. */
        public static boolean selectOne(List<Node> list, Point p) {
            for (Node n : list) {
                if (n.contains(p)) {
                    if (!n.isSelected()) {
                        Node.selectNone(list);
                        n.setSelected(true);
                    }
                    return true;
                }
            }
            return false;
        }

        /** Select each node in r. */
        public static void selectRect(List<Node> list, Rectangle r) {
            for (Node n : list) {
                n.setSelected(r.contains(n.p));
            }
        }

        /** Toggle selected state of each node containing p. */
        public static void selectToggle(List<Node> list, Point p) {
            for (Node n : list) {
                if (n.contains(p)) {
                    n.setSelected(!n.isSelected());
                }
            }
        }

        /** Update each node's position by d (delta). */
        public static void updatePosition(List<Node> list, Point d) {
            for (Node n : list) {
                if (n.isSelected()) {
                    n.p.x += d.x;
                    n.p.y += d.y;
                    n.setBoundary(n.b);
                }
            }
        }

        /** Update each node's radius r. */
        public static void updateRadius(List<Node> list, int r) {
            for (Node n : list) {
                if (n.isSelected()) {
                    n.r = r;
                    n.setBoundary(n.b);
                }
            }
        }

        /** Update each node's color. */
        public static void updateColor(List<Node> list, Color color) {
            for (Node n : list) {
                if (n.isSelected()) {
                    n.color = color;
                }
            }
        }

        /** Update each node's kind. */
        public static void updateKind(List<Node> list, Kind kind) {
            for (Node n : list) {
                if (n.isSelected()) {
                    n.kind = kind;
                }
            }
        }
    }

    private static class ColorIcon implements Icon {

        private static final int WIDE = 20;
        private static final int HIGH = 20;
        private Color color;

        public ColorIcon(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(color);
            g.fillRect(x, y, WIDE, HIGH);
        }

        public int getIconWidth() {
            return WIDE;
        }

        public int getIconHeight() {
            return HIGH;
        }
    }

    /**
     * Devuelve una matriz booleana, que es la representacion del grafo
     * en mAdy, obtenida de la interfaz grafica de usuario.
     * @return Una matriz ady que es la equivalente al grafo ingresado a partir de la interfaz grafica
     */
    public boolean[][] getGrafoMatrizAdy() {
        boolean[][] grafo = new boolean[nodes.size()][nodes.size()];
        //System.out.println("Size = " + nodes.size());
        int count = 0;
        for (Iterator<Node> it = nodes.iterator(); it.hasNext();) {
            Node node = it.next();
            String etiqueta = node.getEtiqueta();
            for (Iterator<Edge> itE = edges.iterator(); itE.hasNext();) {
                Edge edge = itE.next();
                System.out.println("(" + edge.getArista()[0] + "," + edge.getArista()[1] + "), ");
                if (etiqueta.equals(edge.n1.getEtiqueta())) {
                    int indice = findPosition(edge.n2.getEtiqueta());
                    if (indice == -1) {
                        System.out.println("Error de parsing");
                    }
                    grafo[count][indice] = true;
                    grafo[indice][count] = true;

                } else if (etiqueta.equals(edge.n2.getEtiqueta())) {
                    int indice = findPosition(edge.n1.getEtiqueta());
                    if (indice == -1) {
                        System.out.println("Error de parsing");
                    }
                    grafo[count][indice] = true;
                    grafo[indice][count] = true;

                }

            }
            count++;
        }

        return grafo;
    }

    /**
     * Permite encontrar la posicion de una etiqueta que identifica a un nodo, en
     * la lista de nodos
     * @param etiqueta Identificador de nodo
     * @return La posicion del nodo en la lista
     */
    private int findPosition(String etiqueta) {
        int count = 0;
        for (Iterator<Node> it = nodes.iterator(); it.hasNext();) {
            Node node = it.next();
            if (etiqueta.equals(node.getEtiqueta())) {
                return count;
            }
            count++;
        }
        return -1;
    }

    /**
     * Genera una matriz aleatoria, que representa un grafo
     * @param nroVertices Numero de vertices que tendrá el grafo
     * @return Una matriz booleana que representa el grafo en matriz ADY
     */
    public static boolean[][] generarMatrizAdyAleatoria(int nroVertices) {
        if (nroVertices > 100) {
            return null;
        }
        boolean[][] grafo = null;
        if (nroVertices > 0) {
            Random rdn = new Random();
            grafo = new boolean[nroVertices][nroVertices];
            for (int i = 0; i < grafo.length; i++) {
               // boolean[] bs = grafo[i];
                for (int j = i; j < grafo.length; j++) {
                    if (i != j) {
                        grafo[i][j] = rdn.nextBoolean();
                        grafo[j][i]=grafo[i][j];
                    } else {
                        grafo[i][j] = false;
                    }
                    
                }
               
            }
        }
        for (boolean[] bs : grafo) {
            for (boolean b : bs) {
                System.out.print(b+"\t");
            }
            System.out.println("");
        }
        
        return grafo;

    }

    /**
     * Permite Gardar un grafo en un archivo
     * @param mAdy Matriz de adyacencia booleana que representa al grafo
     * @return Verdadero si se completa la operacion
     */
    public boolean guardarGrafo(boolean[][] mAdy) {
        FileWriter fichero = null;
        PrintWriter pw = null;
        try {
            fichero = new FileWriter("grafo.txt");
            pw = new PrintWriter(fichero);
            String grafo = "";
            for (boolean[] bs : mAdy) {
                for (boolean b : bs) {
                    grafo += (b ? "1" : "0");
                }
                grafo += ("\n");
            }
            if (pw != null) {
                pw.println(grafo);
            }
        } catch (FileNotFoundException exx) {

            System.out.println(exx.getMessage());
            return false;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());

            return false;
        }
        pw.close();
        try {
            fichero.close();
        } catch (IOException ex) {

            Logger.getLogger(GraphPanel.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    private void borrarVertices() {
        ListIterator<Node> iter = nodes.listIterator();
        short countVertices = 0;
        Node aux = null;
        while (iter.hasNext()) {
            Node n = iter.next();
            if (n.isSelected()) {
                countVertices++;
                aux = n;
            }
        }
        if (countVertices > 1) {
            JOptionPane.showMessageDialog(this, "Ha seleccionado más de un vértice");
            return;
        }
        if (aux != null) {
            ListIterator<Edge> iterE = edges.listIterator();
            //Creamos interfaz grafica
            JPanel panelBorrado = new JPanel();
            panelBorrado.setLayout(new GridLayout(0, 1));
            LinkedList<JCheckBox> verticesSeleccionados = new LinkedList<JCheckBox>();
            while (iterE.hasNext()) {
                Edge e = iterE.next();
                if (e.n1 == aux || e.n2 == aux) {
                    System.out.println("Arista de " + e.n1.getEtiqueta() + " A " + e.n2.getEtiqueta());
                    verticesSeleccionados.add(new JCheckBox("Arista de " + aux.getEtiqueta() + " A "
                            + (e.n1.getEtiqueta().equalsIgnoreCase(aux.getEtiqueta()) ? e.n2.getEtiqueta() : e.n1.getEtiqueta())));
                    Iterator<JCheckBox> itCheck = verticesSeleccionados.iterator();
                    while (itCheck.hasNext()) {
                        panelBorrado.add(itCheck.next());
                    }
                }
            }
            if (verticesSeleccionados.size() == 0) {
                return;
            }
            if (JOptionPane.showConfirmDialog(this, panelBorrado, "Seleccione las Aristas que desea borrar", JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
                Iterator<JCheckBox> itCheck = verticesSeleccionados.iterator();
                while (itCheck.hasNext()) {
                    JCheckBox checkAux = itCheck.next();
                    if (checkAux.isSelected()) {
                        System.out.println("Borrar " + checkAux.getText() + iter.hasNext());
                        iterE = edges.listIterator();
                        while (iterE.hasNext()) {
                            Edge e = iterE.next();
                            String etiquetaCheck = checkAux.getText();
                            System.out.println(etiquetaCheck.indexOf(e.n1.getEtiqueta()));
                            System.out.println(etiquetaCheck.indexOf(e.n2.getEtiqueta()));
                            if (etiquetaCheck.indexOf(e.n1.getEtiqueta()) != -1
                                    && etiquetaCheck.indexOf(e.n2.getEtiqueta()) != -1) {
                                iterE.remove();
                                System.out.println("Borrando" + etiquetaCheck);
                            }
                        }
                    }
                }
            }
        } else {
            return;
        }

        this.repaint();
    }

    /**
     *  @deprecated
     * @param nombreArchivo
     * @return
     */
    public boolean[][] leerGrafo(String nombreArchivo) {
        boolean mAdy[][] = null;
        return mAdy;

    }
}
