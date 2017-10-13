package mazeGenerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Stack;
import maze.Cell;
import maze.Maze;
import static maze.Maze.HEX;
import static maze.Maze.NORMAL;
import static maze.Maze.TUNNEL;
import static maze.Maze.NUM_DIR;

/**
 * Generate maze with Recursive Back Tracker Algorithm
 *
 * @author rommel gaddi
 */
public class RecursiveBacktrackerGenerator implements MazeGenerator {

    private Maze mMaze;
    private HashSet<Cell> visitedCells;

    
    /**
     * Generate a perfect maze 
     */
    @Override
    public void generateMaze(Maze maze) {
        mMaze = maze;
        visitedCells = new HashSet<>();
       	ArrayList<Cell> mazeCells = new ArrayList<>();
        ArrayList<Cell> tunnelCells = new ArrayList<>();
       	Stack<Cell> previousCell = new Stack<>();
        Cell currentCell = null;
        int mazeSize = 0;
        int randomNeighbor = 0;
        boolean isThereUnvisitedNeighbors = true;
        Random randomInt = new Random(System.currentTimeMillis());
        
        
        // Start of Tunnel
        if (maze.type == TUNNEL) { 
        	
        	// get size of the maze
            mazeSize = maze.sizeR * maze.sizeC;
            // select a random starting cell
            currentCell = mMaze.map[randomInt.nextInt(mMaze.sizeR)][randomInt.nextInt(mMaze.sizeC)];
            // mark starting cell as visited
            visitedCells.add(currentCell);            

            // loop until all cells are visited
            while (visitedCells.size() < mazeSize) {

                while (isThereUnvisitedNeighbors) {

                    ArrayList<Integer> unvisitedNeighbors = new ArrayList<>();
                    // get all unvisited neighbors
                    for (int i = 0; i < NUM_DIR; i++) {
                        Cell currentNeighbor = currentCell.neigh[i];
                        if (isCellInMazeAndNotVisited(currentNeighbor) && !tunnelCells.contains(currentNeighbor)) {
                            unvisitedNeighbors.add(i);
                        }
                    }

                    // check for tunnels and add it as extra neighbor
                    if ((currentCell.tunnelTo != null)
                            && (!visitedCells.contains(currentCell.tunnelTo))) {
                        unvisitedNeighbors.add(6);
                    }

                    if (unvisitedNeighbors.size() > 0) {
                    	// select cell from random neighbors
                        randomNeighbor = unvisitedNeighbors.get(randomInt.nextInt(unvisitedNeighbors.size()));
                        
                        // check if random neighbor is a tunnel
                        if (randomNeighbor == 6) {
                            // go through the tunnel
                            previousCell.add(currentCell);
                            currentCell = currentCell.tunnelTo;
                        } else {
                        	// mark the cell if its a tunnel
                            if (currentCell.tunnelTo != null) {
                                tunnelCells.add(currentCell.tunnelTo);
                            }

                            // carve a path to the selected random neighbor
                            currentCell.wall[randomNeighbor].present = false;
                            previousCell.add(currentCell);
                            currentCell = currentCell.neigh[randomNeighbor];
                        }

                        // mark the current cell as visited
                        visitedCells.add(currentCell);
                    } else {
                        isThereUnvisitedNeighbors = false;
                    }
                }
                
                // back track one cell from the stack if there are no unvisited neighbors
                if (previousCell.size() > 0) {
                    currentCell = previousCell.pop();
                }

                isThereUnvisitedNeighbors = true;
            } 
        } //end of Tunnel
        
        // Start of Normal and Hex
        else { 
       	
        	if (maze.type == NORMAL) {
        		// get the size of the maze
        		mazeSize = maze.sizeR * maze.sizeC;
                // select a random starting cell
                currentCell = mMaze.map[randomInt.nextInt(mMaze.sizeR)][randomInt.nextInt(mMaze.sizeC)];
        	} else {
        		// get size of the maze
	            mazeCells = new ArrayList<>();
	            for (int i = 0; i < maze.sizeR; i++) {
	                for (int j = (i + 1) / 2; j < maze.sizeC + (i + 1) / 2; j++) {
	                    if (!isCellInMazeAndNotVisited(mMaze.map[i][j]))
	                        continue;
	                    mazeCells.add(mMaze.map[i][j]);
	                }
	            }
	            mazeSize = mazeCells.size();
	            // select a random starting cell
	            currentCell = mazeCells.get(randomInt.nextInt(mazeCells.size()));
        	}

        	// mark starting cell as visited
            visitedCells.add(currentCell);

            
            // loop until all cells are visited
            while (visitedCells.size() < mazeSize) {
                
                while (isThereUnvisitedNeighbors) {
                    
                    ArrayList<Integer> unvisitedNeighbors = new ArrayList<>();
                    // get all unvisited neighbors
                    for (int i = 0; i < NUM_DIR; i++) {
                        Cell currentNeighbor = currentCell.neigh[i];
                        if (isCellInMazeAndNotVisited(currentNeighbor)) {
                            unvisitedNeighbors.add(i);
                        }
                    }
                    
                    if (unvisitedNeighbors.size() > 0) {
                    	// select cell from random neighbors
                        randomNeighbor = unvisitedNeighbors.get(randomInt.nextInt(unvisitedNeighbors.size()));
                        // carve a path to the selected random neighbor
                        currentCell.wall[randomNeighbor].present = false;
                        // add the current cell to the stack
                        previousCell.add(currentCell);
                        // set the random neighbor as the current cell
                        currentCell = currentCell.neigh[randomNeighbor];

                        // mark the current cell as visited
                        visitedCells.add(currentCell);
                    } else {
                        // exit if no more unvisited neighbors
                    	isThereUnvisitedNeighbors = false;
                    }
                }

                // back track one cell from the stack if there are no unvisited neighbors
                if (previousCell.size() > 0) {
                    currentCell = previousCell.pop();
                }

                isThereUnvisitedNeighbors = true;
            }
        } // end of Normal and Hex
        
    } // end of generateMaze()


     /**
     * Check whether the cell is in the maze and not yet visited.
     */
    private boolean isCellInMazeAndNotVisited(Cell cell) {
    	
        if (mMaze.type == HEX) {
            return cell != null && 
            		!visitedCells.contains(cell) &&
            		cell.r >= 0 && 
            		cell.r < mMaze.sizeR && 
            		cell.c >= (cell.r + 1) / 2 && 
            		cell.c < mMaze.sizeC + (cell.r + 1) / 2;
        } else {
            return cell != null && 
            		!visitedCells.contains(cell) &&
            		cell.r >= 0 && 
            		cell.r < mMaze.sizeR && 
            		cell.c >= 0 && 
            		cell.c < mMaze.sizeC;
        }    	
    } // end of isCellInMazeAndNotVisited
    
} // end of class RecursiveBacktrackerGenerator()

