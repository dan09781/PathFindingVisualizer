# Path Finding Visualizer

Java application for visualizing pathfinding algorithms. Implemented Dijkstra and A* algorithms with various user-controlled parameters

## How to run
## Compiling the program
```
javac -d <relative-path-to-class-directory> -sourcepath <relative-path-to-source-directory> <relative-path-to-java-source-file>
```
This command creates a folder and stores the compiled .class files in that folder. In more detail:
1. Specify the path to a folder that will store the compiled .class files using `-d` option
2. Specify the directory where the java source file is and the path to actual java source file using the `-sourcepath` option
#### Example:
```
javac -d src/classes -sourcepath src/ src/PathFinder.java
```

### Running the program
```
java -cp <class-path> <class-name>
```

#### Exmaple:
```
java -cp src/classes PathFinder
```

