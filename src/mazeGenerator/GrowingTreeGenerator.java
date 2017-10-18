package mazeGenerator;

import java.util.ArrayList;
import java.util.Random;
import java.util.HashSet;
import maze.Maze;
import maze.Cell;
import static maze.Maze.HEX;
import static maze.Maze.NORMAL;
import static maze.Maze.NUM_DIR;


/**
 * Implements the Growing Tree maze generating algorithm.
 *
 * @author rommel gaddi
 */
public class GrowingTreeGenerator implements MazeGenerator {
	// Growing tree maze generator. As it is very general, here we implement as "usually pick the most recent cell, but occasionally pick a random cell"
	
	double threshold = 0.1;
	
    private Maze mMaze;
    private HashSet<Cell> visitedCells;


    /**
     * Generate a perfect maze inside the input maze object using Growing Tree Algorithm:
     *
     * Input: Maze M, all walls built up, start and exit points marked.
     * Output: Maze M, appropriate walls knocked down to form a perfect maze from start to exit.
     *
     * 1. Pick a random starting cell and add it to set Z (initially Z is empty, after addition it contains
	 *	  just the starting cell).
     *
     * 2. Using a particular strategy (see below) select a cell b from Z. If cell b has unvisited neighboring
	 *	  cells, randomly select a neighbor, carve a path to it, and add the selected neighbor to set Z.
	 *	  If b has no unvisited neighbors, remove it from Z.
     *
     * 3. Repeat step 2 until Z is empty.
     *
     * @param maze The reference of Maze object to generate
     */
    @Override
    public void generateMaze(Maze maze) {
        mMaze = maze;
        visitedCells = new HashSet<>();
        ArrayList<Cell> mazeCells = new ArrayList<>();
        ArrayList<Cell> cellRepositoryZ = new ArrayList<>();
        Cell cellB = null;
        int randomNeighbor = 0;
        Random randomInt = new Random(System.currentTimeMillis());        
        
        // Accepts only Normal or Hex Maze Type
        if ((mMaze.type != NORMAL) && (mMaze.type != HEX) ) { 
        	System.out.println("Error! This generator only supports Normal and Hex type of maze");
        	return;
        }
        
        if (mMaze.type == HEX) {
        	
        	// get the size of the maze for Hex
            mazeCells = new ArrayList<>();
            for (int i = 0; i < mMaze.sizeR; i++) {
                for (int j = (i + 1) / 2; j < mMaze.sizeC + (i + 1) / 2; j++) {
                    if (!isCellInMazeAndNotVisited(mMaze.map[i][j]))
                        continue;
                    mazeCells.add(mMaze.map[i][j]);
                }
            }
        	// select random cell b
            cellB = mazeCells.get(randomInt.nextInt(mazeCells.size()));
        } 
        else if (mMaze.type == NORMAL) {
            // select random cell b
            cellB = mMaze.map[randomInt.nextInt(mMaze.sizeR)][randomInt.nextInt(mMaze.sizeC)];
        }           
        
        // mark current cell as visited
        visitedCells.add(cellB);
        
        // add the random starting cell b to temporary cell repository Z
        cellRepositoryZ.add(cellB);
        
        // loop while cell repository Z is not empty
        while (cellRepositoryZ.size() > 0) {
        	
        	// pick random cell b from temporary cell repository Z 
    		cellB = cellRepositoryZ.get(randomInt.nextInt(cellRepositoryZ.size()));
        	
    		// pick recently added cell b from temporary cell repository Z
    		//cellB = cellRepositoryZ.get(cellRepositoryZ.size()-1);
    		
    		// get unvisited neighbors of cell b
    		ArrayList<Integer> unvisitedNeighbors = new ArrayList<>();
            for (int i = 0; i < NUM_DIR; i++) {
                Cell currentNeighbor = cellB.neigh[i];
                if (isCellInMazeAndNotVisited(currentNeighbor)) {
                    unvisitedNeighbors.add(i);
                }
            }
            
            if (unvisitedNeighbors.size() > 0) {
            	// choose random neighbor of cell b
                randomNeighbor = unvisitedNeighbors.get(randomInt.nextInt(unvisitedNeighbors.size()));
                // carve a path to random neighbor
                cellB.wall[randomNeighbor].present = false;
                // add random neighbor to temporary cell repository Z
                cellRepositoryZ.add(cellB.neigh[randomNeighbor]);
                // mark random neighbor as visited
               	visitedCells.add(cellB.neigh[randomNeighbor]);

            } else {
                // if cell b has no neighbor, remove it from temporary cell repository Z
            	cellRepositoryZ.remove(cellB);
            }
            
        } // repeat until temporary cell repository is empty
        
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
  

}
