package mazeGenerator;


import java.util.ArrayList;
import java.util.Random;
import maze.Cell;
import maze.Maze;
import static maze.Maze.HEX;
import static maze.Maze.NORMAL;
import static maze.Maze.NUM_DIR;

/**
 * Generate maze with Growing Tree Algorithm
 *
 * @author rommel gaddi
 */
public class ModifiedPrimsGenerator implements MazeGenerator {

	private Maze mMaze;

	/**
	 * Generate a perfect maze inside the input maze object, using the following modified prim's algorithm:
	 */
	@Override
	public void generateMaze(Maze maze) {
		Random randomInt = new Random(System.currentTimeMillis());
		mMaze = maze;
		int mazeSize = 0;

		ArrayList<Cell> adjacentCells = new ArrayList<>();
		ArrayList<Cell> cellRepositoryZ = new ArrayList<>();
		ArrayList<Cell> frontierCells = new ArrayList<>();
		Cell cellB = null;
		Cell cellC = null;
		Cell currentCell = null;

		
        // Accepts only Normal or Hex Maze Type
        if ((mMaze.type != NORMAL) && (mMaze.type != HEX) ) { 
        	System.out.println("Error! This generator only supports Normal and Hex maze");
        	return;
        }		
		
		if (mMaze.type == HEX) {
			ArrayList<Cell> mazeCells = new ArrayList<>();
			for (int i = 0; i < mMaze.sizeR; i++) {
				for (int j = (i + 1) / 2; j < mMaze.sizeC + (i + 1) / 2; j++) {
					if (!isCellInMaze(mMaze.map[i][j]))
						continue;
					mazeCells.add(mMaze.map[i][j]);
				}
			}
			// select random cell
			currentCell = mazeCells.get(randomInt.nextInt(mazeCells.size()));
			// get the size of the maze
			mazeSize = mazeCells.size();

		} else if (mMaze.type == NORMAL) {
			// select random cell
			currentCell = mMaze.map[randomInt.nextInt(mMaze.sizeR)][randomInt.nextInt(mMaze.sizeC)];
			// get the size of the maze
			mazeSize = maze.sizeR * maze.sizeC;
		}

		// Add the current cell to Z
		cellRepositoryZ.add(currentCell);

		// Loop until Z includes every cell in the maze
		while (cellRepositoryZ.size() < mazeSize) {

			// Put all neighboring cells of the current cell into the frontier set F
			for (int i = 0; i < NUM_DIR; i++) {
				Cell neighborCell = currentCell.neigh[i];
				if ((isCellInMaze(neighborCell)) 
						&& (!frontierCells.contains(neighborCell)) 
						&& (!cellRepositoryZ.contains(neighborCell))) {
					frontierCells.add(neighborCell);
				}
			}      

			// Randomly select a cell c from frontier set F 
			cellC = frontierCells.get(randomInt.nextInt(frontierCells.size()));
			// Remove cell c from frontier set F
			frontierCells.remove(cellC);

			// Get all cell in Z that are adjacent to c
			adjacentCells.clear();
			for (int i = 0; i < NUM_DIR; i++) {
				Cell adjacentNeighborCell = cellC.neigh[i];
				if ((isCellInMaze(adjacentNeighborCell)) 
						&& (cellRepositoryZ.contains(adjacentNeighborCell))) {
					adjacentCells.add(adjacentNeighborCell);
				}
			}             

			// Randomly select a cell b that is in Z and adjacent to the cell c
			cellB = adjacentCells.get(randomInt.nextInt(adjacentCells.size()));

			// Carve a path between cell c and cell b
			for (int i = 0; i < NUM_DIR; i++) {
				Cell neighborCell = cellB.neigh[i];
				if ((isCellInMaze(neighborCell)) && (neighborCell == cellC)) {
					cellB.wall[i].present = false;
				}
			}

			// Add cell c to set Z
			cellRepositoryZ.add(cellC);

			// Change current cell to cell c
			currentCell = cellC;
		}
	} // end of generateMaze()




	/**
	 * Check whether the cell is in the maze.
	 */
	private boolean isCellInMaze(Cell cell) {

		if (mMaze.type == HEX) {
			return cell != null && 
					cell.r >= 0 && 
					cell.r < mMaze.sizeR && 
					cell.c >= (cell.r + 1) / 2 && 
					cell.c < mMaze.sizeC + (cell.r + 1) / 2;
		} else {
			return cell != null && 
					cell.r >= 0 && 
					cell.r < mMaze.sizeR && 
					cell.c >= 0 && 
					cell.c < mMaze.sizeC;
		}    	
	} // end of isCellInMaze()   


} // end of class ModifiedPrimsGenerator
