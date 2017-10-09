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
    private boolean visitedCellsNormal[][];
    private HashSet<Cell> visitedCellsHex;
    private int numUnvisitedCells;
    private Cell currentCell;
    private ArrayList<Cell> mazeCells;
    private Random randomInt = new Random(System.currentTimeMillis());
    
    /**
     * Generate a perfect maze 
     */
    @Override
    public void generateMaze(Maze maze) {
        mMaze = maze;
        visitedCellsNormal = new boolean[maze.sizeR][maze.sizeC];
        visitedCellsHex = new HashSet<>();
        
        boolean isThereUnvisitedNeighbors = true;
        int randomNeighbor;
        Stack<Cell> previousCell = new Stack<>();
        ArrayList<Cell> lockedCells = new ArrayList<>();

        // Start of Tunnel
        if (maze.type == TUNNEL) { 
            numUnvisitedCells = maze.sizeR * maze.sizeC;

            selectStartingCellAndMarkVisited();

            while (numUnvisitedCells > 0) {

                while (isThereUnvisitedNeighbors) {

                    ArrayList<Integer> unvisitedNeighbors = new ArrayList<>();
                    for (int i = 0; i < NUM_DIR; i++) {
                        Cell currentNeighbor = currentCell.neigh[i];
                        if (isCellInMazeAndNotVisited(currentNeighbor) && !lockedCells.contains(currentNeighbor)) {
                            unvisitedNeighbors.add(i);
                        }
                    }

                    if ((currentCell.tunnelTo != null)
                            && (!visitedCellsNormal[currentCell.tunnelTo.r][currentCell.tunnelTo.c])) {
                        // Add an extra neighbor position for the tunnel neighbor
                        unvisitedNeighbors.add(6);
                    }

                    if (unvisitedNeighbors.size() > 0) {
                        int randomNeighborIndex = randomInt.nextInt(unvisitedNeighbors.size());
                        randomNeighbor = unvisitedNeighbors.get(randomNeighborIndex);

                        if (randomNeighbor != 6) {

                            // Don't go through the tunnel if there is one

                            // Lock the other end of the tunnel if there is one
                            if (currentCell.tunnelTo != null) {
                                lockedCells.add(currentCell.tunnelTo);
                            }

                            // Carve path and move
                            currentCell.wall[randomNeighbor].present = false;
                            previousCell.add(currentCell);
                            currentCell = currentCell.neigh[randomNeighbor];
                        } else {

                            // Go through the tunnel, no need to carve a path
                            previousCell.add(currentCell);
                            currentCell = currentCell.tunnelTo;
                        }

                        // Mark the new current cell as visited
                        visitedCellsNormal[currentCell.r][currentCell.c] = true;
                        numUnvisitedCells--;
                    } else {
                        isThereUnvisitedNeighbors = false;
                    }
                }

                if (previousCell.size() > 0) {
                    currentCell = previousCell.pop();
                }

                isThereUnvisitedNeighbors = true;
            } 
        } //end of Tunnel
        
        // Start of Normal and Hex
        else { 

        	// Get the size of the maze
        	if (maze.type == NORMAL) {
        		numUnvisitedCells = maze.sizeR * maze.sizeC;
        	} else {
	            mazeCells = new ArrayList<>();
	            for (int i = 0; i < maze.sizeR; i++) {
	                for (int j = (i + 1) / 2; j < maze.sizeC + (i + 1) / 2; j++) {
	                    if (!isCellInMazeAndNotVisited(mMaze.map[i][j]))
	                        continue;
	                    mazeCells.add(mMaze.map[i][j]);
	                }
	            }
	            numUnvisitedCells = mazeCells.size();
        	}
        	
            // select a random cell and mark it as visited 
            selectStartingCellAndMarkVisited();

            while (numUnvisitedCells > 0) {
                
                
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
                        if (maze.type == NORMAL)
                        	visitedCellsNormal[currentCell.r][currentCell.c] = true;
                        else
                        	visitedCellsHex.add(currentCell);
                        
                        numUnvisitedCells--;
                    } else {
                        // Step 2.2
                    	isThereUnvisitedNeighbors = false;
                    }
                }

                // back track one cell from the stack
                if (previousCell.size() > 0) {
                    currentCell = previousCell.pop();
                }

                isThereUnvisitedNeighbors = true;
            }
        } // end of Normal and Hex
        
    } // end of generateMaze()

     /**
     * Randomly select a starting cell for the maze.
     */
    private void selectStartingCellAndMarkVisited() {
        if (mMaze.type == HEX) {
        	// select random cell
            currentCell = mazeCells.get(randomInt.nextInt(mazeCells.size()));
            // mark starting cell as visited
            visitedCellsHex.add(currentCell);
            numUnvisitedCells--;
        } else {
            int row = randomInt.nextInt(mMaze.sizeR);
            int col = randomInt.nextInt(mMaze.sizeC);
            // select random cell
            currentCell = mMaze.map[row][col];
            // mark starting cell as visited
            visitedCellsNormal[currentCell.r][currentCell.c] = true;
            numUnvisitedCells--;
        }
 
    } // end of selectStartingCellAndMarkVisited()

     /**
     * Check whether the cell is in the maze and not yet visited.
     */
    private boolean isCellInMazeAndNotVisited(Cell cell) {
    	
        if (mMaze.type == HEX) {
            return cell != null && 
            		!visitedCellsHex.contains(cell) &&
            		cell.r >= 0 && 
            		cell.r < mMaze.sizeR && 
            		cell.c >= (cell.r + 1) / 2 && 
            		cell.c < mMaze.sizeC + (cell.r + 1) / 2;
        } else {
            return cell != null && 
            		!visitedCellsNormal[cell.r][cell.c] &&
            		cell.r >= 0 && 
            		cell.r < mMaze.sizeR && 
            		cell.c >= 0 && 
            		cell.c < mMaze.sizeC;
        }    	
    } // end of isCellInMazeAndNotVisited
    
} // end of class RecursiveBacktrackerGenerator()

