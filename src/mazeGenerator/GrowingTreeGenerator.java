package mazeGenerator;

import static maze.Maze.HEX;
import static maze.Maze.NORMAL;
import static maze.Maze.NUM_DIR;
import java.util.*;
import maze.Maze;
import maze.Cell;

/**
 * Generate maze with Growing Tree Algorithm
 *
 * @author rommel gaddi
 */
public class GrowingTreeGenerator implements MazeGenerator {
	// Growing tree maze generator. As it is very general, here we implement as "usually pick the most recent cell, but occasionally pick a random cell"
	
	double threshold = 0.1;
	
    private Maze mMaze;
    private boolean mNormalVisited[][];
    private HashSet<Cell> mHexVisited;
    private Cell mCurrentCell;
    private ArrayList<Cell> mMazeCells;
    private Random nRandom = new Random(System.currentTimeMillis());
  

    /**
     * Generate a perfect maze 
     */    
    @Override
    public void generateMaze(Maze maze) {
        mMaze = maze;
        mNormalVisited = new boolean[maze.sizeR][maze.sizeC];
        mHexVisited = new HashSet<>();
        
        int randomNeighbor;
        ArrayList<Cell> mTempCellRepository = new ArrayList<>();

        // check if Normal or Hex
        if ((mMaze.type == NORMAL) || (mMaze.type == HEX) ) { 
        	
            // Select a random starting cell (b) and mark it as visited 
            selectStartingCellAndMarkVisited();
            
            // add the random starting cell (b) to temporary cell repository (Z)
            mTempCellRepository.add(mCurrentCell);
            
            while (mTempCellRepository.size() > 0) {
            	
            	// get random cell (b) from temporary cell repository (z) and set it as current cell
        		mCurrentCell = mTempCellRepository.get(nRandom.nextInt(mTempCellRepository.size()));
        		
        		// get unvisited neighbors of current cell (b)
        		ArrayList<Integer> unvisitedNeighbors = new ArrayList<>();
                for (int i = 0; i < NUM_DIR; i++) {
                    Cell currentNeighbor = mCurrentCell.neigh[i];
                    if (isCellInMazeAndNotVisited(currentNeighbor)) {
                        unvisitedNeighbors.add(i);
                    }
                }
                
                if (unvisitedNeighbors.size() > 0) {
                	// choose random neighbor of current cell (b)
                    randomNeighbor = unvisitedNeighbors.get(nRandom.nextInt(unvisitedNeighbors.size()));
                    // carve a path to random neighbor
                    mCurrentCell.wall[randomNeighbor].present = false;
                    // add random neighbor to temporary cell repository(z)
                    mTempCellRepository.add(mCurrentCell.neigh[randomNeighbor]);
                    // mark random neighbor as visited
                    if (mMaze.type == NORMAL)
                    	mNormalVisited[mCurrentCell.neigh[randomNeighbor].r][mCurrentCell.neigh[randomNeighbor].c] = true;
                    else
                    	mHexVisited.add(mCurrentCell.neigh[randomNeighbor]);

                } else {
                    // if current cell (b) has no neighbor, remove it from temporary cell repository (z)
                	mTempCellRepository.remove(mCurrentCell);
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
            mMazeCells = new ArrayList<>();
            for (int i = 0; i < mMaze.sizeR; i++) {
                for (int j = (i + 1) / 2; j < mMaze.sizeC + (i + 1) / 2; j++) {
                    if (!isCellInMazeAndNotVisited(mMaze.map[i][j]))
                        continue;
                    mMazeCells.add(mMaze.map[i][j]);
                }
            }
        	// select random cell
            mCurrentCell = mMazeCells.get(nRandom.nextInt(mMazeCells.size()));
            // mark starting cell as visited
            mHexVisited.add(mCurrentCell);
        } else {
            int randomRow = nRandom.nextInt(mMaze.sizeR);
            int randomCol = nRandom.nextInt(mMaze.sizeC);
            // select random cell
            mCurrentCell = mMaze.map[randomRow][randomCol];
            // mark starting cell as visited
            mNormalVisited[mCurrentCell.r][mCurrentCell.c] = true;
        }
 
    } // end of selectStartingCellAndMarkVisited()

     /**
     * Check whether the cell is in the maze and not yet visited.
     */
    private boolean isCellInMazeAndNotVisited(Cell cell) {
    	
        if (mMaze.type == HEX) {
            return cell != null && 
            		!mHexVisited.contains(cell) &&
            		cell.r >= 0 && 
            		cell.r < mMaze.sizeR && 
            		cell.c >= (cell.r + 1) / 2 && 
            		cell.c < mMaze.sizeC + (cell.r + 1) / 2;
        } else {
            return cell != null && 
            		!mNormalVisited[cell.r][cell.c] &&
            		cell.r >= 0 && 
            		cell.r < mMaze.sizeR && 
            		cell.c >= 0 && 
            		cell.c < mMaze.sizeC;
        }    	
    } // end of isCellInMazeAndNotVisited
  

}
