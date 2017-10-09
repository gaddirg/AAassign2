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
 * Generate maze with Growing Tree Algorithm
 *
 * @author rommel gaddi
 */
public class GrowingTreeGenerator implements MazeGenerator {
	// Growing tree maze generator. As it is very general, here we implement as "usually pick the most recent cell, but occasionally pick a random cell"
	
	double threshold = 0.1;
	
    private Maze mMaze;
    private boolean visitedCellsNormal[][];
    private HashSet<Cell> visitedCellsHex;
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
        
        int randomNeighbor;
        ArrayList<Cell> cellRepository = new ArrayList<>();

        // check if Normal or Hex
        if ((mMaze.type == NORMAL) || (mMaze.type == HEX) ) { 
        	
            // Select a random starting cell (b) and mark it as visited 
            selectStartingCellAndMarkVisited();
            
            // add the random starting cell (b) to temporary cell repository (Z)
            cellRepository.add(currentCell);
            
            while (cellRepository.size() > 0) {
            	
            	// get random cell (b) from temporary cell repository (z) and set it as current cell
        		currentCell = cellRepository.get(randomInt.nextInt(cellRepository.size()));
        		
        		// get unvisited neighbors of current cell (b)
        		ArrayList<Integer> unvisitedNeighbors = new ArrayList<>();
                for (int i = 0; i < NUM_DIR; i++) {
                    Cell currentNeighbor = currentCell.neigh[i];
                    if (isCellInMazeAndNotVisited(currentNeighbor)) {
                        unvisitedNeighbors.add(i);
                    }
                }
                
                if (unvisitedNeighbors.size() > 0) {
                	// choose random neighbor of current cell (b)
                    randomNeighbor = unvisitedNeighbors.get(randomInt.nextInt(unvisitedNeighbors.size()));
                    // carve a path to random neighbor
                    currentCell.wall[randomNeighbor].present = false;
                    // add random neighbor to temporary cell repository(z)
                    cellRepository.add(currentCell.neigh[randomNeighbor]);
                    // mark random neighbor as visited
                    if (mMaze.type == NORMAL)
                    	visitedCellsNormal[currentCell.neigh[randomNeighbor].r][currentCell.neigh[randomNeighbor].c] = true;
                    else
                    	visitedCellsHex.add(currentCell.neigh[randomNeighbor]);

                } else {
                    // if current cell (b) has no neighbor, remove it from temporary cell repository (z)
                	cellRepository.remove(currentCell);
                }
                
            } // repeat until temporary cell repository is empty
            
        } // end of Normal and Hex
        
        
    } // end of generateMaze()

     /**
     * Randomly select a starting cell for the maze.
     */
    private void selectStartingCellAndMarkVisited() {
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
        	// select random cell
            currentCell = mazeCells.get(randomInt.nextInt(mazeCells.size()));
            // mark starting cell as visited
            visitedCellsHex.add(currentCell);
        } else if (mMaze.type == NORMAL) {
            int row = randomInt.nextInt(mMaze.sizeR);
            int col = randomInt.nextInt(mMaze.sizeC);
            // select random cell
            currentCell = mMaze.map[row][col];
            // mark starting cell as visited
            visitedCellsNormal[currentCell.r][currentCell.c] = true;
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
  

}
