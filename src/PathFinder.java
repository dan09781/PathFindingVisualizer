import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Random;
import java.lang.Math;

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
    int numCellsEachRow = 20;
    int delay = 5;
    String[] algorithms = {"Dijkstra","A*"};
    String[] tools = {"Start","End","Obstacle", "Eraser"};
    JFrame frame;
    Algorithm Alg = new Algorithm();
    Random r = new Random();
    JSlider size = new JSlider(1,10,2);
    JSlider speed = new JSlider(0,50,delay);
    JSlider obstacles = new JSlider(1,100,50);
    JLabel algL = new JLabel("Pick an algorithm");
    JLabel toolL = new JLabel("Toolbox");
    JLabel sizeL = new JLabel("Size:");
    JLabel cellsL = new JLabel(numCellsEachRow+"x"+numCellsEachRow);
    JLabel delayL = new JLabel("Delay:");
    JLabel msL = new JLabel(delay+"ms");
    JButton searchB = new JButton("Find Path");
    JButton resetB = new JButton("Reset Path");
    JButton clearMapB = new JButton("Clear Grid");
    JComboBox algorithmsBx = new JComboBox(algorithms);
    JComboBox toolBx = new JComboBox(tools);
    JPanel toolP = new JPanel();

    Node[][] map;
    Grid canvas;
    Border ehtchedLower = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
    //GENERAL VARIABLES
    int startx = -1, starty = -1, finishx = -1, finishy = -1;
    int tool = 0;
    int curAlg = 0;
    int WIDTH = 850;
    int HEIGHT = 800;
    int GRIDSIZE = 650;
    int CELLSIZE = GRIDSIZE/numCellsEachRow;
    //BOOLEANS
    boolean solving = false;

    class Algorithm {

        class PathWithDistance{
            ArrayList<Node> path;
            int distance;

            public PathWithDistance(ArrayList<Node> otherPath, int dist){
                path = otherPath;
                distance = dist;
            }

            public ArrayList<Node> getKey(){return path;}
            public int getValue(){return distance;}
        }


        //Comparator for comparing distance to a node
        class nodeCompare implements Comparator<PathWithDistance> {
            public int compare(PathWithDistance a, PathWithDistance b){
                if (a.getValue() >= b.getValue())
                    return 1;
                else
                    return -1;
            }
        }

        //Dijkstra implementation
        public void Dijkstra() {
            PriorityQueue<PathWithDistance> paths = new PriorityQueue<PathWithDistance>(new nodeCompare());
            ArrayList<Node> startNode = new ArrayList<Node>();
            startNode.add(map[startx][starty]);
            PathWithDistance startPath = new PathWithDistance(startNode,0);
            paths.add(startPath);
            while (paths.size() > 0 && solving){
                PathWithDistance curPath = paths.poll();
                Node lastNodeInPath = curPath.getKey().get(curPath.getKey().size()-1);
                if (lastNodeInPath.x == finishx && lastNodeInPath.y == finishy){
                    solving = false;
                    colorPath(curPath.getKey());
                    return;
                }
                if (lastNodeInPath.x != startx || lastNodeInPath.y != starty)
                    map[lastNodeInPath.x][lastNodeInPath.y].setType(4);
                ArrayList<Node> currentNeighbors = new ArrayList<Node>();
                currentNeighbors = getNeighbors(lastNodeInPath);
                for (int i=0;i<currentNeighbors.size();i++){
                    Node curNeighbor = currentNeighbors.get(i);
                    if (!map[curNeighbor.x][curNeighbor.y].visited){
                        map[curNeighbor.x][curNeighbor.y].visited = true;
                        int newDist = curPath.getValue() + 1;
                        ArrayList<Node> newPath = new ArrayList<>(curPath.getKey());
                        newPath.add(curNeighbor);
                        PathWithDistance newEntry = new PathWithDistance(newPath,newDist);
                        paths.add(newEntry);
                    }
                }
                updateMap();
                delay();
            }
            solving=false;
            return;
        }

        //A* implementation with euclidean distance heuristics
        public void AStar() {
            setHeuristic();
            PriorityQueue<PathWithDistance> paths = new PriorityQueue<PathWithDistance>(new nodeCompare());
            ArrayList<Node> startNode = new ArrayList<Node>();
            startNode.add(map[startx][starty]);
            PathWithDistance startPath = new PathWithDistance(startNode,0);
            paths.add(startPath);
            while (paths.size() > 0 && solving){
                PathWithDistance curPath = paths.poll();
                Node lastNodeInPath = curPath.getKey().get(curPath.getKey().size()-1);
                if (lastNodeInPath.x == finishx && lastNodeInPath.y == finishy){
                    solving = false;
                    colorPath(curPath.getKey());
                    return;
                }
                if (lastNodeInPath.x != startx || lastNodeInPath.y != starty)
                    map[lastNodeInPath.x][lastNodeInPath.y].setType(4);
                ArrayList<Node> currentNeighbors = new ArrayList<Node>();
                currentNeighbors = getNeighbors(lastNodeInPath);
                for (int i=0;i<currentNeighbors.size();i++){
                    Node curNeighbor = currentNeighbors.get(i);
                    if (!map[curNeighbor.x][curNeighbor.y].visited){
                        map[curNeighbor.x][curNeighbor.y].visited = true;
                        int newDist = curPath.getValue() + (int)(map[curNeighbor.x][curNeighbor.y].heuristic)*5;
                        ArrayList<Node> newPath = new ArrayList<>(curPath.getKey());
                        newPath.add(curNeighbor);
                        PathWithDistance newEntry = new PathWithDistance(newPath,newDist);
                        paths.add(newEntry);
                    }
                }
                updateMap();
                delay();
            }
            solving=false;
            return;
        }

        private ArrayList<Node> getNeighbors(Node current) {
            ArrayList<Node> neighbors = new ArrayList<Node>();
            if (current.x-1 >= 0 && map[current.x-1][current.y].getType() != 2)
                neighbors.add(map[current.x-1][current.y]);
            if (current.x+1 <= numCellsEachRow-1 && map[current.x+1][current.y].getType() != 2)
                neighbors.add(map[current.x+1][current.y]);
            if (current.y-1 >= 0 && map[current.x][current.y-1].getType() != 2)
                neighbors.add(map[current.x][current.y-1]);
            if (current.y+1 <= numCellsEachRow-1 && map[current.x][current.y+1].getType() != 2)
                neighbors.add(map[current.x][current.y+1]);
            return neighbors;
        }

        //Euclidean distance heuristic
        private void setHeuristic(){
            for (int x = 0;x<numCellsEachRow;x++){
                for (int y=0;y<numCellsEachRow;y++){
                    double distanceHeuristic = getEuclideanDistance(x, y, finishx, finishy);
                    map[x][y].heuristic = distanceHeuristic;
                }
            }
        }

        private double getEuclideanDistance(int curx, int cury, int finishx, int finishy){
            return 10*Math.sqrt((finishx-curx)*(finishx-curx) + (finishy-cury)*(finishy-cury));
        }

        //Once a path has been found, color it
        private void colorPath(ArrayList<Node> finalPath){
            for (int i=1;i<finalPath.size()-1;i++){
                finalPath.get(i).setType(5);
                updateMap();
                delay();
            }
        }
    }


    private class Node {
        int cellType;
        int x;
        int y;
        double heuristic = 0;
        boolean visited = false;

        public Node(int otherType, int otherx, int othery) {
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

        public Grid() {
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        public void paintComponent(Graphics g) {	//REPAINT
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
                updateMap();
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
            resetMap();
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
                updateMap();
            } catch(Exception z) {}
        }

        @Override
        public void mouseReleased(MouseEvent e) {}
    }

    public PathFinder() {
        clearMap();
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
                if((startx > -1 && starty > -1) && (finishx > -1 && finishy > -1))
                    solving = true;
            }
        });
        resetB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetMap();
                updateMap();
            }
        });
        clearMapB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearMap();
                updateMap();
            }
        });
        algorithmsBx.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                curAlg = algorithmsBx.getSelectedIndex();
                updateMap();
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
                clearMap();
                solving = false;
                updateMap();
            }
        });
        speed.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                delay = speed.getValue();
                updateMap();
            }
        });

        canvas = new Grid();
        canvas.setBounds((WIDTH-GRIDSIZE)/2,100,GRIDSIZE, GRIDSIZE);
        frame.getContentPane().add(canvas);
        run();
    }


    public void run() {
        while (true) {
            while (!solving) {
                delay();
            }

            if (curAlg == 0) {
                Alg.Dijkstra();
            }
            else if (curAlg == 1){
                Alg.AStar();
            }
        }
    }

    public void delay() {	//DELAY METHOD
        try {
            Thread.sleep(delay);
        } catch(Exception e) {}
    }

    public void updateMap() {	//updateMap ELEMENTS OF THE GUI
        CELLSIZE = GRIDSIZE/numCellsEachRow;
        canvas.repaint();
        cellsL.setText(numCellsEachRow+"x"+numCellsEachRow);
        msL.setText(delay+"ms");
    }


    public void clearMap() {	//CLEAR MAP
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

    public static void main(String[] args) {    //MAIN METHOD
        new PathFinder();
    }
}