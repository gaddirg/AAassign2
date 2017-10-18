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
 * Implements the Recursive Backtracker maze generating algorithm.
 *
 * @author rommel gaddi
 */
public class RecursiveBacktrackerGenerator implements MazeGenerator {

	private Maze mMaze;
	private HashSet<Cell> visitedCells;


    /**
     * Generate a perfect maze inside the input maze object using Recursive Backtracker Algorithm:
     *
     * Input: Maze M, all walls built up, start and exit points marked.
     * Output: Maze M, appropriate walls knocked down to form a perfect maze from start to exit.
     *
     * 1. Randomly pick a starting cell.
     *
     * 2. Pick a random unvisited neighboring cell and move to that neighbor. In the process, carve a
	 *	  path (i.e, remove the wall) between the cells.
     *
     * 3. Continue this process until we reach a cell that has no unvisited neighbors. In that case,
	 *	  backtrack one cell at a time, until we backtracked to a cell that has unvisited neighbors.
	 *	  Repeat step 2.
     *
     * 4. When there are no more unvisited neighbors for all cells, then every cell would have been visited
	 *	  and we have generated a perfect maze.
     *
     * @param maze The reference of Maze object to generate
     */
	@Override
	public void generateMaze(Maze maze) {
		mMaze = maze;
		visitedCells = new HashSet<>();
		ArrayList<Cell> mazeCells = new ArrayList<>();
		ArrayList<Cell> tunnelCells = new ArrayList<>();
		Stack<Cell> previousCell = new Stack<>();
		Cell currentCell = null;
		int randomNeighbor = 0;
		boolean isThereUnvisitedCells = true;
		Random randomInt = new Random(System.currentTimeMillis());


		// Start of Tunnel
		if (maze.type == TUNNEL) { 

			// select a random starting cell
			currentCell = mMaze.map[randomInt.nextInt(mMaze.sizeR)][randomInt.nextInt(mMaze.sizeC)];
			// mark starting cell as visited
			visitedCells.add(currentCell);            

			// loop until all cells are visited
			while (isThereUnvisitedCells) {

				// get all unvisited neighbors
				ArrayList<Integer> unvisitedNeighbors = new ArrayList<>();
				for (int i = 0; i < NUM_DIR; i++) {
					Cell currentNeighbor = currentCell.neigh[i];
					if (isCellInMazeAndNotVisited(currentNeighbor) && !tunnelCells.contains(currentNeighbor)) {
						unvisitedNeighbors.add(i);
					}
				}

				// check for tunnels and add it as additional neighbor
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
					// if stack is not empty, there are cells that still not have been visited
					if (previousCell.size() > 0) 
						// back track one cell from the stack if there are no unvisited neighbors
						currentCell = previousCell.pop();
					else
						// all cells have been visited
						isThereUnvisitedCells = false;
				}
			}
		} 
		//end of Tunnel

		// Start of Normal and Hex
		else if (maze.type == NORMAL) {
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
			// select a random starting cell
			currentCell = mazeCells.get(randomInt.nextInt(mazeCells.size()));
		}

		// mark starting cell as visited
		visitedCells.add(currentCell);

		// loop until all cells are visited
		while (isThereUnvisitedCells) {

			// get all unvisited neighbors
			ArrayList<Integer> unvisitedNeighbors = new ArrayList<>();
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
				// if stack is not empty, there are cells that still not have been visited
				if (previousCell.size() > 0) 
					// back track one cell from the stack if there are no unvisited neighbors
					currentCell = previousCell.pop();
				else
					// no more cells to visit
					isThereUnvisitedCells = false;
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

