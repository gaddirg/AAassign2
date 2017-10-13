package mazeSolver;


import maze.Cell;
import maze.Maze;
import static maze.Maze.HEX;
import static maze.Maze.NORMAL;
import static maze.Maze.TUNNEL;
import static maze.Maze.NORTH;
import static maze.Maze.NORTHEAST;
import static maze.Maze.NORTHWEST;
import static maze.Maze.EAST;
import static maze.Maze.SOUTH;
import static maze.Maze.SOUTHEAST;
import static maze.Maze.SOUTHWEST;
import static maze.Maze.WEST;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;


/**
 * Implements WallFollowerSolver
 */

public class WallFollowerSolver implements MazeSolver {

	private Maze mMaze;
	private boolean isExitReached = false;
	private HashSet<Cell> visitedCells = new HashSet<>();

	/**
	 * Solve a perfect maze using Wall Follower algorithm (left-hand rule)
	 */
	@Override
	public void solveMaze(Maze maze) {
		mMaze = maze;
		int leftNeighbor = 0;
		int left2Neighbor = 0;
		int rightNeighbor = 0;
		int frontNeighbor = EAST;

		// (Step 1) Start at entrance
		Cell currentCell = maze.entrance;
		maze.drawFtPrt(currentCell);
		visitedCells.add(currentCell);

		if ((mMaze.type == NORMAL) || (mMaze.type == TUNNEL)) {

			while (currentCell != maze.exit) {
				
				// set directions of front, left and right neighbors
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

				// check for tunnels
				if (currentCell.tunnelTo != null) {
					currentCell = currentCell.tunnelTo;
					maze.drawFtPrt(currentCell);
					if (!visitedCells.contains(currentCell)) visitedCells.add(currentCell);
					// go back to current cell if dead-end
					if (currentCell.wall[NORTH].present && currentCell.wall[EAST].present 
							&& currentCell.wall[SOUTH].present && currentCell.wall[WEST].present)
						currentCell = currentCell.tunnelTo;
				}
				
				// if left neighbor has no wall go to left neighbor
				if (!currentCell.wall[leftNeighbor].present) {
					// get out of loop
					if (visitedCells.contains(currentCell.neigh[leftNeighbor])
						&& (!visitedCells.contains(currentCell.neigh[frontNeighbor]) && (!currentCell.wall[frontNeighbor].present)))
						currentCell = currentCell.neigh[frontNeighbor];
						
					else {
						currentCell = currentCell.neigh[leftNeighbor];
						frontNeighbor = leftNeighbor;
					}
					maze.drawFtPrt(currentCell);
					if (!visitedCells.contains(currentCell)) visitedCells.add(currentCell);
					
				} 
				// if left neighbor has wall and front neighbor has no wall, go to front neighbor
				else if (!currentCell.wall[frontNeighbor].present) {
					// get out of loop
					if (visitedCells.contains(currentCell.neigh[frontNeighbor]) && visitedCells.contains(currentCell.neigh[leftNeighbor])
						&& (!visitedCells.contains(currentCell.neigh[rightNeighbor]) && (!currentCell.wall[rightNeighbor].present)))
						frontNeighbor = rightNeighbor;
					else {
						currentCell = currentCell.neigh[frontNeighbor];
						maze.drawFtPrt(currentCell);
						if (!visitedCells.contains(currentCell)) visitedCells.add(currentCell);
					}
				} 
				// left and front neighbor has walls change direction to right neighbor
				else {
					frontNeighbor = rightNeighbor;
				}

			}

		} // end of normal

		else if (mMaze.type == HEX) {

			while (currentCell != maze.exit) {
				// set directions of front, left2, left and right neighbors
				left2Neighbor = (frontNeighbor + 2) % 6;
				leftNeighbor = (frontNeighbor + 1) % 6;
				rightNeighbor = (frontNeighbor - 1) % 6;
				// if negative convert to positive
				if (rightNeighbor < 0) rightNeighbor += 6;
				
				// if left2 neighbor has no wall go to left neighbor
				if (!currentCell.wall[left2Neighbor].present) {
					currentCell = currentCell.neigh[left2Neighbor];
					maze.drawFtPrt(currentCell);
					if (!visitedCells.contains(currentCell)) visitedCells.add(currentCell);
					frontNeighbor = left2Neighbor;
				} 
				// if left2 neighbor has wall and left neighbor has no wall go to left neighbor
				else if (!currentCell.wall[leftNeighbor].present) {
						currentCell = currentCell.neigh[leftNeighbor];
						maze.drawFtPrt(currentCell);
						if (!visitedCells.contains(currentCell)) visitedCells.add(currentCell);
						frontNeighbor = leftNeighbor;
				} 
				// if left and left2 neighbor has wall and front neighbor has no wall go to front neighbor
				else if (!currentCell.wall[frontNeighbor].present) {
					currentCell = currentCell.neigh[frontNeighbor];
					maze.drawFtPrt(currentCell);
					if (!visitedCells.contains(currentCell)) visitedCells.add(currentCell);
				} 
				// if left, left2 and front neighbor has walls change direction to right neighbor
				else {
					frontNeighbor = rightNeighbor;
				}

			}
		} // end of HEX        

		isSolved();

	} // end of solveMaze()

	@Override
	public boolean isSolved() {
		return true;
	} // end if isSolved()

	@Override
	public int cellsExplored() {
		return visitedCells.size();
	} // end of cellsExplored()


} // end of class WallFollowerSolver
