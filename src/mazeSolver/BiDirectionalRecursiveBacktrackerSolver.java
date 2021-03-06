package mazeSolver;

import static maze.Maze.HEX;
import static maze.Maze.NORMAL;
import static maze.Maze.NUM_DIR;
import static maze.Maze.TUNNEL;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Stack;

import maze.Cell;
import maze.Maze;

/**
 * Implements the Bidirectional Recursive Backtracking maze solving algorithm.
 * 
 * @author rommel gaddi
 */
public class BiDirectionalRecursiveBacktrackerSolver implements MazeSolver {

    private Maze mMaze;
	private HashSet<Cell> entranceVisitedCells = new HashSet<>();
	private HashSet<Cell> exitVisitedCells = new HashSet<>();
	private HashSet<Cell> visitedCells = new HashSet<>();
	boolean entranceMeetsExit = false;

	
    /**
     * Solve a perfect maze using Bidirectional Recursive Backtracking Algorithm:
     *
     * Input: Maze M, all walls built up, start and exit points marked.
     * Output: Maze M, appropriate walls knocked down to form a perfect maze from start to exit.
     *
     * 1. Mark entrance and exit as starting cells.
     *
     * 2. For both cells, pick a random unvisited neighboring cell and move to that neighbor. In the process, carve a
	 *	  path (i.e, remove the wall) between the cells.
     *
     * 3. Continue this process until a cell that has no unvisited neighbors is reached. In that case,
	 *	  backtrack one cell at a time, until we backtracked to a cell that has unvisited neighbors.
	 *	  Repeat step 2.
     *
     * 4. When both entrance and exit cell has reached a common visited cell, the the maze is solved 
	 *	  by combining their paths.
     *
     * @param maze The reference of Maze object to generate
     */
	@Override
	public void solveMaze(Maze maze) {

        mMaze = maze;
       	Stack<Cell> entrancePreviousCell = new Stack<>();
       	Stack<Cell> exitPreviousCell = new Stack<>();
       	
        int randomNeighbor = 0;
        Random randomInt = new Random(System.currentTimeMillis());

		// start at entrance
		Cell currentEntranceCell = maze.entrance;
		Cell currentExitCell = maze.exit;
		
		maze.drawFtPrt(currentEntranceCell);
		entranceVisitedCells.add(currentEntranceCell);        
		visitedCells.add(currentEntranceCell);
		entrancePreviousCell.add(currentEntranceCell);
		
		maze.drawFtPrt(currentExitCell);
		exitVisitedCells.add(currentExitCell); 
		//visitedCells.add(currentExitCell);
		exitPreviousCell.add(currentExitCell);
               
        while (!entranceMeetsExit) {

        	//ENTRANCE CELLS
        	
        	ArrayList<Integer> unvisitedNeighbors = new ArrayList<>();
            // get all unvisited neighbors
            for (int i = 0; i < NUM_DIR; i++) {
                Cell currentNeighbor = currentEntranceCell.neigh[i];
                if (isCellInMazeAndNotVisited(currentNeighbor) && (!entranceVisitedCells.contains(currentNeighbor))
                		&& (!currentEntranceCell.wall[i].present) ) {
                    unvisitedNeighbors.add(i);
                }
            }
            
            // check for tunnels and add it as extra neighbor
            if ((currentEntranceCell.tunnelTo != null)
                    && (!entranceVisitedCells.contains(currentEntranceCell.tunnelTo))) {
                unvisitedNeighbors.add(6);
            }
            
            if (unvisitedNeighbors.size() > 0) {
            	// select cell from random neighbors
                randomNeighbor = unvisitedNeighbors.get(randomInt.nextInt(unvisitedNeighbors.size()));
                // add the current cell to the stack
                entrancePreviousCell.add(currentEntranceCell);
                
                // set the random neighbor as the current cell
                if (randomNeighbor == 6)
                	currentEntranceCell = currentEntranceCell.tunnelTo;
                else
                	currentEntranceCell = currentEntranceCell.neigh[randomNeighbor];
                
                // check if entrance and exit path have met
                if((exitPreviousCell.contains(currentEntranceCell))){
                	entranceMeetsExit = true;
                	isSolved();
                 }
                // draw footprint
                maze.drawFtPrt(currentEntranceCell);
                // mark the current cell as visited
                entranceVisitedCells.add(currentEntranceCell);
                visitedCells.add(currentEntranceCell);
            } else {
            	if (entrancePreviousCell.size() > 0)
            		currentEntranceCell = entrancePreviousCell.pop();
            	else
            		// exit loop if all cells have been visited
            		break;
            }
        
        
        	//EXIT CELLS
        	
        	unvisitedNeighbors = new ArrayList<>();
            // get all unvisited neighbors
            for (int i = 0; i < NUM_DIR; i++) {
                Cell currentNeighbor = currentExitCell.neigh[i];
                if (isCellInMazeAndNotVisited(currentNeighbor) && (!exitVisitedCells.contains(currentNeighbor))
                		&& (!currentExitCell.wall[i].present) ) {
                    unvisitedNeighbors.add(i);
                }
            }

            // check for tunnels and add it as extra neighbor
            if ((currentExitCell.tunnelTo != null)
                    && (!exitVisitedCells.contains(currentExitCell.tunnelTo))) {
                unvisitedNeighbors.add(6);
            }
            
            if (unvisitedNeighbors.size() > 0) {
            	// select cell from random neighbors
                randomNeighbor = unvisitedNeighbors.get(randomInt.nextInt(unvisitedNeighbors.size()));
                // add the current cell to the stack
                exitPreviousCell.add(currentExitCell);
                // set the random neighbor as the current cell
                if (randomNeighbor == 6)
                	currentExitCell = currentExitCell.tunnelTo;
                else
                	currentExitCell = currentExitCell.neigh[randomNeighbor];
                
                // check if entrance and exit path have met
                if((entrancePreviousCell.contains(currentExitCell))){
                	entranceMeetsExit = true;
                	isSolved();
                 }
                // draw footprint
                maze.drawFtPrt(currentExitCell);
                // mark the current cell as visited
                exitVisitedCells.add(currentExitCell);
                visitedCells.add(currentExitCell);
            } else {
                // exit if no more unvisited neighbors
            	if (exitPreviousCell.size() > 0)
            		currentExitCell = exitPreviousCell.pop();
            	else
            		break;
            }
        
        }
 		
	} // end of solveMaze()


	@Override
	public boolean isSolved() {
		return true;
	} // end if isSolved()

	@Override
	public int cellsExplored() {
		return entranceVisitedCells.size() + exitVisitedCells.size();
	} // end of cellsExplored()

	
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

	
} // end of class BiDirectionalRecursiveBackTrackerSolver
