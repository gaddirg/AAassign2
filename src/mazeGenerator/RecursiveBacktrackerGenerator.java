package mazeGenerator;

import static maze.Maze.HEX;
import static maze.Maze.NORMAL;
import static maze.Maze.TUNNEL;
import static maze.Maze.NUM_DIR;
import maze.Cell;
import maze.Maze;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Stack;


/**
 * Generate maze with Recursive Back Tracker Algorithm
 *
 * @author rommel gaddi
 */
public class RecursiveBacktrackerGenerator implements MazeGenerator {

    private Maze mMaze;
    private boolean mNormalVisited[][];
    private HashSet<Cell> mHexVisited;
    private int numUnvisitedCells;
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
        
        boolean isThereUnvisitedNeighbors = true;
        int randomNeighbor;
        Stack<Cell> mPreviousCell = new Stack<>();
        ArrayList<Cell> mLockedCells = new ArrayList<>();

        // Start of Tunnel
        if (maze.type == TUNNEL) { 
            numUnvisitedCells = maze.sizeR * maze.sizeC;

            selectStartingCellAndMarkVisited();

            while (numUnvisitedCells > 0) {

                while (isThereUnvisitedNeighbors) {

                    ArrayList<Integer> unvisitedNeighbors = new ArrayList<>();
                    for (int i = 0; i < NUM_DIR; i++) {
                        Cell currentNeighbor = mCurrentCell.neigh[i];
                        if (isCellInMazeAndNotVisited(currentNeighbor) && !mLockedCells.contains(currentNeighbor)) {
                            unvisitedNeighbors.add(i);
                        }
                    }

                    if ((mCurrentCell.tunnelTo != null)
                            && (!mNormalVisited[mCurrentCell.tunnelTo.r][mCurrentCell.tunnelTo.c])) {
                        // Add an extra neighbor position for the tunnel neighbor
                        unvisitedNeighbors.add(6);
                    }

                    if (unvisitedNeighbors.size() > 0) {
                        int randomNeighborIndex = nRandom.nextInt(unvisitedNeighbors.size());
                        randomNeighbor = unvisitedNeighbors.get(randomNeighborIndex);

                        if (randomNeighbor != 6) {

                            // Don't go through the tunnel if there is one

                            // Lock the other end of the tunnel if there is one
                            if (mCurrentCell.tunnelTo != null) {
                                mLockedCells.add(mCurrentCell.tunnelTo);
                            }

                            // Carve path and move
                            mCurrentCell.wall[randomNeighbor].present = false;
                            mPreviousCell.add(mCurrentCell);
                            mCurrentCell = mCurrentCell.neigh[randomNeighbor];
                        } else {

                            // Go through the tunnel, no need to carve a path
                            mPreviousCell.add(mCurrentCell);
                            mCurrentCell = mCurrentCell.tunnelTo;
                        }

                        // Mark the new current cell as visited
                        mNormalVisited[mCurrentCell.r][mCurrentCell.c] = true;
                        numUnvisitedCells--;
                    } else {
                        isThereUnvisitedNeighbors = false;
                    }
                }

                if (mPreviousCell.size() > 0) {
                    mCurrentCell = mPreviousCell.pop();
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
        	
            // select a random cell and mark it as visited 
            selectStartingCellAndMarkVisited();

            while (numUnvisitedCells > 0) {
                
                
                while (isThereUnvisitedNeighbors) {
                    
                    ArrayList<Integer> unvisitedNeighbors = new ArrayList<>();
                    // get all unvisited neighbors
                    for (int i = 0; i < NUM_DIR; i++) {
                        Cell currentNeighbor = mCurrentCell.neigh[i];
                        if (isCellInMazeAndNotVisited(currentNeighbor)) {
                            unvisitedNeighbors.add(i);
                        }
                    }
                    
                    if (unvisitedNeighbors.size() > 0) {
                    	// select cell from random neighbors
                        randomNeighbor = unvisitedNeighbors.get(nRandom.nextInt(unvisitedNeighbors.size()));
                        // carve a path to the selected random neighbor
                        mCurrentCell.wall[randomNeighbor].present = false;
                        // add the current cell to the stack
                        mPreviousCell.add(mCurrentCell);
                        // set the random neighbor as the current cell
                        mCurrentCell = mCurrentCell.neigh[randomNeighbor];

                        // mark the current cell as visited
                        if (maze.type == NORMAL)
                        	mNormalVisited[mCurrentCell.r][mCurrentCell.c] = true;
                        else
                        	mHexVisited.add(mCurrentCell);
                        
                        numUnvisitedCells--;
                    } else {
                        // Step 2.2
                    	isThereUnvisitedNeighbors = false;
                    }
                }

                // back track one cell from the stack
                if (mPreviousCell.size() > 0) {
                    mCurrentCell = mPreviousCell.pop();
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
            mCurrentCell = mMazeCells.get(nRandom.nextInt(mMazeCells.size()));
            // mark starting cell as visited
            mHexVisited.add(mCurrentCell);
            numUnvisitedCells--;
        } else {
            int randomRow = nRandom.nextInt(mMaze.sizeR);
            int randomCol = nRandom.nextInt(mMaze.sizeC);
            // select random cell
            mCurrentCell = mMaze.map[randomRow][randomCol];
            // mark starting cell as visited
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
    
} // end of class RecursiveBacktrackerGenerator()

