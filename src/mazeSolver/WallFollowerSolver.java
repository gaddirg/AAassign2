package mazeSolver;


import maze.Cell;
import maze.Maze;
import static maze.Maze.HEX;
import static maze.Maze.NORMAL;
import static maze.Maze.NUM_DIR;
import static maze.Maze.NORTH;
import static maze.Maze.NORTHEAST;
import static maze.Maze.NORTHWEST;
import static maze.Maze.EAST;
import static maze.Maze.SOUTH;
import static maze.Maze.SOUTHEAST;
import static maze.Maze.SOUTHWEST;
import static maze.Maze.WEST;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Stack;


/**
 * Implements WallFollowerSolver
 */

public class WallFollowerSolver implements MazeSolver {

    private Maze mMaze;
    private Random mRandGen = new Random(System.currentTimeMillis());
    private int numberOfCellsVisited = 0;
    private boolean mExitReached = false;
    private HashSet<Cell> visitedCells = new HashSet<>();

    /**
     * Solve a perfect maze using the following recursive backtracker algorithm:

     */
    @Override
    public void solveMaze(Maze maze) {
        mMaze = maze;
        boolean normalVisited[][] = new boolean[maze.sizeR][maze.sizeC];
        
        int numNormalCellsUnvisited = maze.sizeR * maze.sizeC;
        int numHexCellsUnvisited = 0;
        int randomNeighbor;
 
        Cell currentNeighbor = null;
        int leftNeighbor = 0;
        int left2Neighbor = 0;
        int rightNeighbor = 0;
        int frontNeighbor;
        
        if ((mMaze.type == NORMAL) || (mMaze.type == HEX)) {

        	frontNeighbor = EAST;
        	
            // (Step 1) Start at entrance
            Cell currentCell = maze.entrance;
            maze.drawFtPrt(currentCell);
            visitedCells.add(currentCell);
            
            while (currentCell != maze.exit) {

            	
            if (mMaze.type == NORMAL) {	
            	if (frontNeighbor == NORTH) {
            		leftNeighbor = WEST;
            		rightNeighbor = EAST;
            	} else if (frontNeighbor == EAST) {
            		leftNeighbor = NORTH;
            		rightNeighbor = SOUTH;
            	} else if (frontNeighbor == SOUTH) {
            		leftNeighbor = EAST;
            		rightNeighbor = WEST;
            	}  else if (frontNeighbor == WEST) {
            		leftNeighbor = SOUTH;
            		rightNeighbor = NORTH;
            	}
            	
            	if (!currentCell.wall[leftNeighbor].present) {
            		currentCell = currentCell.neigh[leftNeighbor];
            		maze.drawFtPrt(currentCell);
            		if (!visitedCells.contains(currentCell)) visitedCells.add(currentCell);
            		frontNeighbor = leftNeighbor;
            	} else {
            		if (!currentCell.wall[frontNeighbor].present) {
            			currentCell = currentCell.neigh[frontNeighbor];
            			maze.drawFtPrt(currentCell);
            			if (!visitedCells.contains(currentCell)) visitedCells.add(currentCell);
            		} else {
            			frontNeighbor = rightNeighbor;
            		}
            	}
            	
            } else {
            	if (frontNeighbor == NORTHWEST) {
            		leftNeighbor = WEST;
            		left2Neighbor = SOUTHWEST;
            		rightNeighbor = NORTHEAST;
            	} else if (frontNeighbor == WEST) {
            		leftNeighbor = SOUTHWEST;
            		left2Neighbor = SOUTHEAST;
            		rightNeighbor = NORTHWEST;
            	} else if (frontNeighbor == SOUTHWEST) {
            		leftNeighbor = SOUTHEAST;
            		left2Neighbor = EAST;
            		rightNeighbor = WEST;
            	} else if (frontNeighbor == SOUTHEAST) {
            		leftNeighbor = EAST;
            		left2Neighbor = NORTHEAST;
            		rightNeighbor = SOUTHWEST;
            	} else if (frontNeighbor == EAST) {
            		leftNeighbor = NORTHEAST;
            		left2Neighbor = NORTHWEST;
            		rightNeighbor = SOUTHEAST;
            	}else if (frontNeighbor == NORTHEAST) {
            		leftNeighbor = NORTHWEST;
            		left2Neighbor = WEST;
            		rightNeighbor = EAST;
            	}

            	if (!currentCell.wall[left2Neighbor].present) {
            		currentCell = currentCell.neigh[left2Neighbor];
            		maze.drawFtPrt(currentCell);
            		if (!visitedCells.contains(currentCell)) visitedCells.add(currentCell);
            		frontNeighbor = left2Neighbor;
            	} else { 
            		if (!currentCell.wall[leftNeighbor].present) {
	            		currentCell = currentCell.neigh[leftNeighbor];
	            		maze.drawFtPrt(currentCell);
	            		if (!visitedCells.contains(currentCell)) visitedCells.add(currentCell);
	            		frontNeighbor = leftNeighbor;
            		} else if (!currentCell.wall[frontNeighbor].present) {
            			currentCell = currentCell.neigh[frontNeighbor];
            			maze.drawFtPrt(currentCell);
            			if (!visitedCells.contains(currentCell)) visitedCells.add(currentCell);
            		} else {
            			frontNeighbor = rightNeighbor;
            		}
            	}
            }



            }
                isSolved();
     } // end of normal
        
        
        
        
        
    } // end of solveMaze()

    @Override
    public boolean isSolved() {
        return true;
    } // end if isSolved()

    @Override
    public int cellsExplored() {
        return visitedCells.size();
    } // end of cellsExplored()

    /**
     * Check if a cell is in the maze
     *
     * @param row the row of the cell to check
     * @param column the column of the cell to check
     * @return weather the cell is in the maze
     */
    private boolean isIn(int row, int column) {
        if (mMaze.type == HEX) {
            return row >= 0 && row < mMaze.sizeR && column >= (row + 1) / 2 && column < mMaze.sizeC + (row + 1) / 2;
        } else {
            return row >= 0 && row < mMaze.sizeR && column >= 0 && column < mMaze.sizeC;
        }
    }

    /**
     * Check whether the cell is in the maze.
     *
     * @param cell The cell being checked.
     * @return True if in the maze. Otherwise false.
     */
    private boolean isIn(Cell cell) {
        return cell != null && isIn(cell.r, cell.c);
    }	
	
	


} // end of class WallFollowerSolver
