# Path Finding Visualizer

Java application for visualizing pathfinding algorithms. Implemented Dijkstra and A* algorithms with various user-controlled parameters

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

## Running the program
```
java -cp <class-path> <class-name>
```
This command navigates to the specified class path, set with `-cp` option, that stores the compiled .class files and runs the .class file of the specified name

#### Exmaple:
```
java -cp src/classes PathFinder
```

## Features
![asdasd](/Users/danielmin/Desktop/ProjectPictures/PathFinder/MainScreen.png?raw=true "asd")

