package PathFinderVisualizer;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JComboBox;

public class PathFinder{
    //Grid variables
    int startx = -1, starty = -1, finishx = -1, finishy = -1;
    int tool = 0;
    int curAlg = 0;
    int WIDTH = 850;
    int HEIGHT = 800;
    int GRIDSIZE = 650;
    int numCellsEachRow = 20;
    int CELLSIZE = GRIDSIZE/numCellsEachRow;

    //Pathfinding algorithm in process of solving indicator
    boolean solving = false;

    String[] algorithms = {"Dijkstra","A*"};
    String[] tools = {"Start","End","Obstacle", "Eraser"};
    JFrame frame;

    Random r = new Random();
    GridUpdator gridUpdator = new GridUpdator(5);
    JSlider size = new JSlider(1,10,2);
    JSlider speed = new JSlider(0,50, gridUpdator.getDelay());
    JSlider obstacles = new JSlider(1,100,50);
    JLabel algL = new JLabel("Pick an algorithm");
    JLabel toolL = new JLabel("Toolbox");
    JLabel sizeL = new JLabel("Size:");
    JLabel cellsL = new JLabel(numCellsEachRow+"x"+numCellsEachRow);
    JLabel delayL = new JLabel("Delay:");
    JLabel msL = new JLabel(gridUpdator.getDelay()+"ms");
    JButton searchB = new JButton("Find Path");
    JButton resetB = new JButton("Reset Path");
    JButton clearMapB = new JButton("Clear Grid");
    JComboBox algorithmsBx = new JComboBox(algorithms);
    JComboBox toolBx = new JComboBox(tools);
    JPanel toolP = new JPanel();

    Node[][] map;
    Grid canvas;
    Border ehtchedLower = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

    //Initialize algorithm object
    Algorithm Alg = new Algorithm();


    static class Node {
        int cellType;
        int x;
        int y;
        double heuristic = 0;
        boolean visited = false;

        Node(int otherType, int otherx, int othery) {
            cellType = otherType;
            x = otherx;
            y = othery;
            visited = false;
            heuristic = 0;
        }

        public int getType() {return cellType;}
        public void setType(int type) {cellType = type;}
    }



    class Grid extends JPanel implements MouseListener, MouseMotionListener{	//Grid class

        Grid() {
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            for(int x = 0; x < numCellsEachRow; x++) {	//PAINT EACH NODE IN THE GRID
                for(int y = 0; y < numCellsEachRow; y++) {
                    //Type:
                    //0: starting node
                    //1: Ending node
                    //2: obstacle
                    //3: node that can be explored
                    //4: node that has been visited during pathfinding algorithm
                    //5: final path
                    switch(map[x][y].getType()) {
                        case 0:
                            g.setColor(Color.BLUE);
                            break;
                        case 1:
                            g.setColor(Color.RED);
                            break;
                        case 2:
                            g.setColor(Color.BLACK);
                            break;
                        case 3:
                            g.setColor(Color.WHITE);
                            break;
                        case 4:
                            g.setColor(Color.LIGHT_GRAY);
                            break;
                        case 5:
                            g.setColor(Color.GRAY);
                            break;
                    }
                    g.fillRect(x*CELLSIZE,y*CELLSIZE,CELLSIZE,CELLSIZE);
                    g.setColor(Color.BLACK);
                    g.drawRect(x*CELLSIZE,y*CELLSIZE,CELLSIZE,CELLSIZE);
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            try {
                int x = e.getX()/CELLSIZE;
                int y = e.getY()/CELLSIZE;
                Node current = map[x][y];
                if((tool == 2 || tool == 3) && (current.getType() != 0 && current.getType() != 1))
                    current.setType(tool);
                gridUpdator.updateMap();
            } catch(Exception z) {}
        }

        @Override
        public void mouseMoved(MouseEvent e) {}

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {
            gridUpdator.resetMap();
            try {
                int x = e.getX()/CELLSIZE;	//GET THE X AND Y OF THE MOUSE CLICK IN RELATION TO THE SIZE OF THE GRID
                int y = e.getY()/CELLSIZE;
                Node current = map[x][y];
                switch(tool ) {
                    case 0: {	//START NODE
                        if(current.getType()!=2) {	//IF NOT WALL
                            current.setType(0);
                            if(startx > -1 && starty > -1) {	//IF START EXISTS SET IT TO EMPTY
                                map[startx][starty].setType(3);
                            }
                            startx = x;	//SET THE START X AND Y
                            starty = y;
                        }
                        break;
                    }
                    case 1: {//FINISH NODE
                        if(current.getType()!=2) {	//Not an obstacle
                            if(finishx > -1 && finishy > -1)
                                map[finishx][finishy].setType(3);
                            finishx = x;
                            finishy = y;
                            current.setType(1);
                        }
                        break;
                    }
                    default:
                        if(current.getType() != 0 && current.getType() != 1)
                            current.setType(tool);
                        break;
                }
                gridUpdator.updateMap();
            } catch(Exception z) {}
        }

        @Override
        public void mouseReleased(MouseEvent e) {}
    }

    PathFinder() {
        gridUpdator.clearMap();
        Initialize();
    }

    private void Initialize() {
        frame = new JFrame();
        frame.setVisible(true);
        frame.setResizable(true);
        frame.setSize(WIDTH,HEIGHT);
        frame.setTitle("Visualize Path Finding Algorithms!!");
        frame.getContentPane().setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        toolP.setBorder(BorderFactory.createTitledBorder(ehtchedLower));

        toolP.setBounds(10,10,840,80);

        toolP.setLayout(null);

        searchB.setBounds(10,20, 100, 25);
        toolP.add(searchB);

        resetB.setBounds(110,20,100,25);
        toolP.add(resetB);

        clearMapB.setBounds(210,20, 100, 25);
        toolP.add(clearMapB);

        algL.setBounds(330,12,150,25);
        toolP.add(algL);

        algorithmsBx.setBounds(320,33, 120, 25);
        toolP.add(algorithmsBx);

        toolL.setBounds(460,12,120,25);
        toolP.add(toolL);

        toolBx.setBounds(450,33,120,25);
        toolP.add(toolBx);

        sizeL.setBounds(580,12,40,25);
        toolP.add(sizeL);
        size.setMajorTickSpacing(10);
        size.setBounds(590,33,100,25);
        toolP.add(size);
        cellsL.setBounds(590,55,40,25);
        toolP.add(cellsL);

        delayL.setBounds(690,12,50,25);
        toolP.add(delayL);
        speed.setMajorTickSpacing(5);
        speed.setBounds(700,33,100,25);
        toolP.add(speed);
        msL.setBounds(700,55,40,25);
        toolP.add(msL);

        frame.getContentPane().add(toolP);

        searchB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                solving = false;

                //Start solving if there exist valid starting and ending nodes
                if((startx > -1 && starty > -1) && (finishx > -1 && finishy > -1))
                    solving = true;
            }
        });
        resetB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gridUpdator.resetMap();
                gridUpdator.updateMap();
            }
        });
        clearMapB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gridUpdator.clearMap();
                gridUpdator.updateMap();
            }
        });
        algorithmsBx.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                curAlg = algorithmsBx.getSelectedIndex();
                gridUpdator.updateMap();
            }
        });
        toolBx.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                tool = toolBx.getSelectedIndex();
            }
        });
        size.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                numCellsEachRow = size.getValue()*10;
                cellsL.setText(numCellsEachRow+"x"+numCellsEachRow);
                msL.setText(gridUpdator.getDelay()+"ms");
                gridUpdator.clearMap();
                solving = false;
                gridUpdator.updateMap();
            }
        });
        speed.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                gridUpdator.setDelay(speed.getValue());
                gridUpdator.updateMap();
            }
        });

        canvas = new Grid();
        canvas.setBounds((WIDTH-GRIDSIZE)/2,100,GRIDSIZE, GRIDSIZE);
        frame.getContentPane().add(canvas);

        //Run the program
        run();
    }


    public void run() {
        while (true) {
            while (!solving) {
                gridUpdator.delay();
            }

            if (curAlg == 0) {
                Alg.Dijkstra(map, startx, starty, finishx, finishy, gridUpdator);
                solving = false;
            }
            else if (curAlg == 1){
                Alg.AStar(map, startx, starty, finishx, finishy, gridUpdator);
                solving = false;
            }
        }
    }

    public class GridUpdator {

        private int delay;


        GridUpdator(int otherDelay){
            delay=otherDelay;
        }

        public void setDelay(int otherDelay){
            delay=otherDelay;
        }

        public int getDelay(){
            return this.delay;
        }

        public void delay() {	//DELAY METHOD
            try {
                Thread.sleep(delay);
            } catch(Exception e) {}
        }

        public void updateMap() {	//updateMap ELEMENTS OF THE GUI
            CELLSIZE = GRIDSIZE/numCellsEachRow;
            canvas.repaint();
        }


        public void clearMap() {	//CLEAR MAP
            //Square 2d-array
            map = new Node[numCellsEachRow][numCellsEachRow];
            for(int x = 0; x < numCellsEachRow; x++) {
                for(int y = 0; y < numCellsEachRow; y++) {
                    map[x][y] = new Node(3,x,y);
                }
            }
            startx = -1;
            finishx = -1;
            starty = -1;
            finishy = -1;
            solving = false;
        }

        public void resetMap() {
            for(int x = 0; x < numCellsEachRow; x++) {
                for (int y = 0; y < numCellsEachRow; y++) {
                    Node current = map[x][y];
                    map[x][y].visited = false;
                    if (current.getType() == 4 || current.getType() == 5)
                        map[x][y] = new Node(3, x, y);
                }
            }
            solving = false;
        }
    }

    public static void main(String[] args) {    //MAIN METHOD
        new PathFinder();
    }
}