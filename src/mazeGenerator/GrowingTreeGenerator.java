package mazeGenerator;

import static maze.Maze.HEX;
import static maze.Maze.NUM_DIR;

import java.util.*;

import maze.Maze;
import maze.Cell;

public class GrowingTreeGenerator implements MazeGenerator {
	// Growing tree maze generator. As it is very general, here we implement as "usually pick the most recent cell, but occasionally pick a random cell"
	
	double threshold = 0.1;
	
    private Maze mMaze;
    private boolean mNormalVisited[][];
    private HashSet<Cell> mHexVisited;
    private int numUnvisitedCells;
    private Cell mCurrentCell;
    private ArrayList<Cell> mMazeCells;
    private Random nRandom = new Random(System.currentTimeMillis());
  
    @Override
    public void generateMaze(Maze maze) {
        mMaze = maze;
        mNormalVisited = new boolean[maze.sizeR][maze.sizeC];
        mHexVisited = new HashSet<>();
        
        boolean isThereUnvisitedNeighbors = true;
        int randomNeighbor;
        ArrayList<Cell> mZ = new ArrayList<>();

        // Start of Tunnel
        if ((maze.type == Maze.NORMAL) || (maze.type == Maze.HEX) ) { 

        	// Get the size of the maze
        	if (maze.type == Maze.NORMAL) {
        		numUnvisitedCells = maze.sizeR * maze.sizeC;
        	} else {
	            mMazeCells = new ArrayList<>();
	            for (int i = 0; i < maze.sizeR; i++) {
	                for (int j = (i + 1) / 2; j < maze.sizeC + (i + 1) / 2; j++) {
	                    if (!isCellInMazeAndNotVisited(mMaze.map[i][j]))
	                        continue;
	                    mMazeCells.add(mMaze.map[i][j]);
	                }
	            }
	            numUnvisitedCells = mMazeCells.size();
        	}
        	
            // (Step 1) 
            selectStartingCellAndMarkVisited();
            mZ.add(mCurrentCell);
            
            while (mZ.size() > 0) {
            		mCurrentCell = mZ.get(nRandom.nextInt(mZ.size()));
            		// get unvisited neighbors
            		ArrayList<Integer> unvisitedNeighbors = new ArrayList<>();
                    for (int i = 0; i < NUM_DIR; i++) {
                        Cell currentNeighbor = mCurrentCell.neigh[i];
                        if (isCellInMazeAndNotVisited(currentNeighbor)) {
                            unvisitedNeighbors.add(i);
                        }
                    }
                    
                    //choose random neighbor
                    if (unvisitedNeighbors.size() > 0) {
                        randomNeighbor = unvisitedNeighbors.get(nRandom.nextInt(unvisitedNeighbors.size()));
                        mCurrentCell.wall[randomNeighbor].present = false;
                        mZ.add(mCurrentCell.neigh[randomNeighbor]);
                        // (Step 2.1.4)
                        if (maze.type == Maze.NORMAL)
                        	mNormalVisited[mCurrentCell.neigh[randomNeighbor].r][mCurrentCell.neigh[randomNeighbor].c] = true;
                        else
                        	mHexVisited.add(mCurrentCell.neigh[randomNeighbor]);
                        
                        numUnvisitedCells--;
                    } else {
                        
                    	mZ.remove(mCurrentCell);
                    }
   

                // Step 2.2.3
                isThereUnvisitedNeighbors = true;
            }
        } // end of Normal and Hex
        
        
    } // end of generateMaze()

     /**
     * Randomly select a starting cell for the maze.
     */
    private void selectStartingCellAndMarkVisited() {
        if (mMaze.type == HEX) {
            mCurrentCell = mMazeCells.get(nRandom.nextInt(mMazeCells.size()));
            // Mark starting cell as visited
            mHexVisited.add(mCurrentCell);
            numUnvisitedCells--;
        } else {
            int randomRow = nRandom.nextInt(mMaze.sizeR);
            int randomCol = nRandom.nextInt(mMaze.sizeC);
            mCurrentCell = mMaze.map[randomRow][randomCol];
            // Mark starting cell as visited
            mNormalVisited[mCurrentCell.r][mCurrentCell.c] = true;
            numUnvisitedCells--;
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
