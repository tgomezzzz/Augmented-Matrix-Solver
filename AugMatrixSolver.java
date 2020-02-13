import java.util.*;

public class AugMatrixSolver {

	public static void main(String[] bears) {

		Scanner s = new Scanner(System.in);
		int[][] matrix;

		matrix = loadMatrix(s);	
		
		while (true){
			if (isRREF(matrix)){
				break;
			}
			printMatrix(matrix);
		}

		System.out.println("The matrix in RREF form:");
		printMatrix(matrix);

		s.close();

	}

	/**
	 * Checks if the matrix is in RREF form, and if it isn't, executes steps to put it in RREF form 
	 * @param m the matrix
	 * @return true if the matrix if in RREF form, false if not
	 */
	private static boolean isRREF(int[][] m){
		//check indices of 1s, making sure they form a staircase
		//check that everything else in the col is 0

		//scan matrix row by row
		//at the first non-zero or non-one value, call method to eliminate it and put that row into RREF form
		//***edge case: array with no leading non-zeroes (they could all be one)

		int[] pivotCols = new int[m.length];
		int ind = 0;
		for (int i = 0; i < m.length; i++){
			for (int j = 0; j < m[0].length; j++){
				//if we see a one, mark it's column as a pivot column and continue to the next row
				//if we see a zero, ignore it
				//otherwise, reduce this row using row operations, and return false
				if (m[i][j] == 1){
					pivotCols[ind++] = j;
					makePivotCol(i, j, m);
					break;
				} else if (m[i][j] != 0){
					reduceRow(i, j, m);
					printMatrix(m);
					return false;
				}
			}
		}

		int prev = -1;
		for (int i = 0; i < pivotCols.length; i++){
			if (pivotCols[i] <= prev){
				orderByZeroes(m);
				return false;
			}
			if (!isPivotCol(i, pivotCols[i], m)){
				return false;
			}
			prev = pivotCols[i];
		}

		return true;
	}

	/**
	 * checks if a column is a pivot column or not
	 * @param pivRow the row of the pivot entry
	 * @param col the column to check
	 * @param m the matrix
	 * @return true if the column is a pivot column, false if not
	 */
	private static boolean isPivotCol(int pivRow, int col, int[][] m){
		for (int i = 0; i < m.length; i++){
			if (i != pivRow){
				if (m[i][col] != 0){
					return false;
				}
			} else if (m[i][col] != 1){
				return false;
			}
		}
		return true;
	}

	/**
	 * creates a pivot column after a row has been reduced by zeroing out all other values in the column
	 * @param row the row that has been reduced
	 * @param col the column of the pivot entry in row
	 * @param m the matrix
	 */
	private static void makePivotCol(int row, int col, int[][] m){
		for (int i = 0; i < m.length; i++){
			if (i != row){
				subtractRows(i, row, m[i][col], m);
			}
		}
	}

	/**
	 * swaps rows with the most leading zeroes to the bottom of the matrix
	 * @param m the matrix
	 */
	private static void orderByZeroes(int[][] m){
		HashMap<Integer, Integer> mostZeroes = new HashMap<>();
		PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
		for (int i = 0; i < m.length; i++){
			if (m[i][0] == 0){
				int zeroes = 1;
				for (int j = 1; j < m[i].length; j++){
					if (m[i][j] != 0){
						break;
					} else {
						zeroes++;
					}
				}
				mostZeroes.put(zeroes, i);
				maxHeap.offer(zeroes);
			}
		}

		int lastRow = m.length - 1;
		while (!maxHeap.isEmpty()){
			int rowToSwap = mostZeroes.get(maxHeap.poll());
			swapRows(rowToSwap, lastRow--, m);
			if (!maxHeap.isEmpty()){
				mostZeroes.replace(maxHeap.peek(), rowToSwap);
			}
		}
	}

	/**
	 * swaps two rows
	 * @param r1 the first row
	 * @param r2 the second row
	 * @param m the matrix
	 */
	private static void swapRows(int r1, int r2, int[][] m){
		if (r1 == r2){
			return;
		}
		int[] t = m[r1];
		m[r1] = m[r2];
		m[r2] = t;
	}

	/**
	 * "reduces" a row by setting m[row][col] to 1 via subtractRows(), and updates the row with its proper values
	 * @param row the row to reduce
	 * @param col the column of the value that is to be reduced to 1
	 * @param m the matrix
	 */
	private static void reduceRow(int row, int col, int[][] m){
		//to reduce this row, the value at m[i][j] needs to become 1
		//scan col for the best value to reduce by (we need a difference of 1)

		int toReduce = m[row][col];
		for (int i = 0; i < m.length; i++){
			if (col > 0 && m[i][col-1] != 0){
				continue;
			}
			if (toReduce == -1){
				multiplyRow(row, -1, m);
				break;
			} else if (toReduce == 2 && m[i][col] == 1){
				System.out.println("2 and 1");
				subtractRows(row, i, 1, m);
				break;
			} else if (toReduce == 2 && m[i][col] == -1){
				System.out.println("2 and -1");	
				subtractRows(row, i, -1, m);
				break;
			} else if (toReduce % m[i][col] == 1){
				System.out.println("=1");
				subtractRows(row, i, toReduce / m[i][col], m);
				break;
			} else if (toReduce % m[i][col] == -1){
				System.out.println("=-1");
				multiplyRow(row, -1, m);
				subtractRows(row, i, -toReduce / m[i][col], m);
				break;
			} else if (m[i][col] == 1){
				subtractRows(row, i, toReduce-1, m);
			}
		}

		makePivotCol(row, col, m);

	}
	/**
	 * multiplies an entire row by a constant
	 * @param r the target row
	 * @param k the constant
	 * @param m	the matrix
	 */
	private static void multiplyRow(int r, int k, int[][] m){
		for (int i = 0; i < m[0].length; i++){
			m[r][i] *= k;
		}
	}

	/**
	 * subtracts row subtractor from row target
	 * @param target the row to subtract from
	 * @param subtractor the row whose values are subtracted
	 * @param k a constant that multiplies values in subtractor
	 * @param m the matrix
	 */
	private static void subtractRows(int target, int subtractor, int k, int[][] m){
		for (int i = 0; i < m[0].length; i++){
			m[target][i] -= (k * m[subtractor][i]);
		}
	}

	/**
	 * loads the matrix by asking the user to input its values entry by entry, row by row
	 * @param s the scanner that gets user input
	 * @return the entered matrix
	 */
	private static int[][] loadMatrix(Scanner s){
		List<List<Integer>> rows;
		while (true){
			rows = new ArrayList<>();

			System.out.println("Enter the coefficients of the matrix, row by row.");
			System.out.println("Enter 'd' when you're done.");

			String input = s.nextLine();
			String[] inputs = input.split(" ");
			final int ROW_SIZE = inputs.length;
			while (!input.equals("d")) {
				try {
					if (inputs.length != ROW_SIZE){
						throw new Exception();
					}
					List<Integer> row = new LinkedList<>();
					for (String str : inputs){
						int t = Integer.parseInt(str);
						row.add(t);
					}
					rows.add(row);
				} catch (NumberFormatException e) {
					System.out.println("Error: matrix can only contain numerical values.");
				} catch (Exception e){
					System.out.println("Error: rows must all be the same length: " + ROW_SIZE);
				}
				input = s.nextLine();
				inputs = input.split(" ");
			}
	
			System.out.println("Is this the correct augmented matrix?");
			for (List<Integer> r : rows) {
				for (int i = 0; i < r.size(); i++) {
					System.out.print(r.get(i) + " ");
					if (i == r.size() - 2) {
						System.out.print("| ");
					}
				}
				System.out.println();
			}
	
			System.out.println("Enter 'y' to solve, or anything else to re-enter the matrix");
			input = s.nextLine();
			

			if (input.equals("y")){
				if (rows.size() > 0){
					break;
				} else {
					System.out.println("Error: no matrix entered.");
				}
			}
		}

		int[][] ret = new int[rows.size()][rows.get(0).size()];
		for (int i = 0; i < ret.length; i++){
			for  (int j  = 0; j < ret[0].length; j++){
				ret[i][j] = rows.get(i).get(j);
			}
		}
		return ret;
	}

	/**
	 * prints the matrix to standard output
	 * @param m the matrix
	 */
	public static void printMatrix(int[][] m){
		for (int i = 0; i < m.length; i++){
			for (int j = 0; j < m[0].length; j++){
				System.out.print(m[i][j] + " ");
				if (j == m[0].length - 2){
					System.out.print("| ");
				}
			}
			System.out.println();
		}
		System.out.println();
	}
}	
