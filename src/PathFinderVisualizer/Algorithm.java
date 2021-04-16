package PathFinderVisualizer;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.lang.Math;
import java.util.Stack;
import PathFinderVisualizer.PathFinder.*;

public class Algorithm {

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
    //Used to sort pq in ascending order w/r/t distance
    class nodeCompare implements Comparator<PathWithDistance> {
        public int compare(PathWithDistance a, PathWithDistance b){
            if (a.getValue() >= b.getValue())
                return 1;
            else
                return -1;
        }
    }

    public void DFS(Node[][] map, int startx, int starty, int finishx, int finishy, GridUpdator update){
        //Create stack that will contain all paths
        Stack<PathWithDistance> paths = new Stack<PathWithDistance>();

        //Create arraylist containing all nodes in a path
        ArrayList<Node> startNode = new ArrayList<Node>();
        startNode.add(map[startx][starty]);

        //Starting path which is just the starting node with dist of 0
        PathWithDistance startPath = new PathWithDistance(startNode,0);
        paths.push(startPath);
        while (paths.size() > 0 && update.getSolving()){
            PathWithDistance curPath = paths.pop();

            //pop the last node in current path and explore its neighbors
            Node lastNodeInPath = curPath.getKey().get(curPath.getKey().size()-1);

            //If a path has been found, return
            if (lastNodeInPath.x == finishx && lastNodeInPath.y == finishy){
                colorPath(curPath.getKey(), update);
                return;
            }

            //For coloring purposes and visualization, set type
            if (lastNodeInPath.x != startx || lastNodeInPath.y != starty)
                map[lastNodeInPath.x][lastNodeInPath.y].setType(4);

            //Get neighbors of the last node in path
            ArrayList<Node> currentNeighbors = new ArrayList<Node>();
            currentNeighbors = getNeighbors(map, lastNodeInPath);

            //Iterate through the neighbors, add them to each path, add each path to paths stack
            for (int i=0;i<currentNeighbors.size();i++){
                Node curNeighbor = currentNeighbors.get(i);
                if (!map[curNeighbor.x][curNeighbor.y].visited){
                    map[curNeighbor.x][curNeighbor.y].visited = true;
                    int newDist = curPath.getValue() + 1;
                    ArrayList<Node> newPath = new ArrayList<>(curPath.getKey());
                    newPath.add(curNeighbor);
                    PathWithDistance newEntry = new PathWithDistance(newPath,newDist);
                    paths.push(newEntry);
                }
            }

            //Update map each iteration and put a delay for visualization
            update.updateMap();
            update.delay();
        }
    }

    //Dijkstra implementation
    public void Dijkstra(Node[][] map, int startx, int starty, int finishx, int finishy, GridUpdator update) {
        //Create pq with paths in ascending order w.r.t to their distances from the starting node
        PriorityQueue<PathWithDistance> paths = new PriorityQueue<PathWithDistance>(new nodeCompare());

        //Create arraylist containing all nodes in a path
        ArrayList<Node> startNode = new ArrayList<Node>();
        startNode.add(map[startx][starty]);

        //Starting path which is just the starting node with dist of 0
        PathWithDistance startPath = new PathWithDistance(startNode,0);
        paths.add(startPath);
        while (paths.size() > 0 && update.getSolving()){
            PathWithDistance curPath = paths.poll();

            //poll the last node in current path and explore its neighbors
            Node lastNodeInPath = curPath.getKey().get(curPath.getKey().size()-1);

            //If a path has been found, return
            if (lastNodeInPath.x == finishx && lastNodeInPath.y == finishy){
                colorPath(curPath.getKey(), update);
                return;
            }

            //For coloring purposes and visualization, set type
            if (lastNodeInPath.x != startx || lastNodeInPath.y != starty)
                map[lastNodeInPath.x][lastNodeInPath.y].setType(4);

            //Get neighbors of the last node in path
            ArrayList<Node> currentNeighbors = new ArrayList<Node>();
            currentNeighbors = getNeighbors(map, lastNodeInPath);

            //Iterate through the neighbors, add them to each path, add each path to paths pq
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

            //Update map each iteration and put a delay for visualization
            update.updateMap();
            update.delay();
        }
    }


    //A* implementation with euclidean distance heuristics
    public void AStar(Node[][] map, int startx, int starty, int finishx, int finishy, GridUpdator update) {
        setHeuristic(map, finishx, finishy);
        PriorityQueue<PathWithDistance> paths = new PriorityQueue<PathWithDistance>(new nodeCompare());
        ArrayList<Node> startNode = new ArrayList<Node>();
        startNode.add(map[startx][starty]);
        PathWithDistance startPath = new PathWithDistance(startNode,0);
        paths.add(startPath);
        while (paths.size() > 0 && update.getSolving()){
            PathWithDistance curPath = paths.poll();
            Node lastNodeInPath = curPath.getKey().get(curPath.getKey().size()-1);
            if (lastNodeInPath.x == finishx && lastNodeInPath.y == finishy){
                colorPath(curPath.getKey(), update);
                return;
            }
            if (lastNodeInPath.x != startx || lastNodeInPath.y != starty)
                map[lastNodeInPath.x][lastNodeInPath.y].setType(4);
            ArrayList<Node> currentNeighbors = new ArrayList<Node>();
            currentNeighbors = getNeighbors(map, lastNodeInPath);
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
            update.updateMap();
            update.delay();
        }
    }

    //Get all neighbors for a given node
    private ArrayList<Node> getNeighbors(Node[][] map, Node current) {
        ArrayList<Node> neighbors = new ArrayList<Node>();
        if (current.x-1 >= 0 && map[current.x-1][current.y].getType() != 2)
            neighbors.add(map[current.x-1][current.y]);
        if (current.x+1 <= map.length-1 && map[current.x+1][current.y].getType() != 2)
            neighbors.add(map[current.x+1][current.y]);
        if (current.y-1 >= 0 && map[current.x][current.y-1].getType() != 2)
            neighbors.add(map[current.x][current.y-1]);
        if (current.y+1 <= map.length-1 && map[current.x][current.y+1].getType() != 2)
            neighbors.add(map[current.x][current.y+1]);
        return neighbors;
    }

    //Setting heuristics for each node for A*
    private void setHeuristic(Node[][] map, int finishx, int finishy){
        for (int x = 0;x<map.length;x++){
            for (int y=0;y<map[x].length;y++){
                double distanceHeuristic = getEuclideanDistance(x, y, finishx, finishy);
                map[x][y].heuristic = distanceHeuristic;
            }
        }
    }

    //Euclidean distance heuristics
    private double getEuclideanDistance(int curx, int cury, int finishx, int finishy){
        return Math.sqrt((finishx-curx)*(finishx-curx) + (finishy-cury)*(finishy-cury));
    }

    //Once a path has been found, color it
    private void colorPath(ArrayList<Node> finalPath, GridUpdator update){
        for (int i=1;i<finalPath.size()-1;i++){
            finalPath.get(i).setType(5);
            update.updateMap();
            update.delay();
        }
    }
}
