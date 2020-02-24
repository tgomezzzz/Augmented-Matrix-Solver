import java.util.*;

public class Matrix {


	/**** 
	 *  known bug:
	 * 
	 * 2 3 3 0
	 * 2 1 0 0
	 * 1 2 1 0
	 * 
	 * fails because one row ends up being
	 * -5 0 0 0, which results in no solution
	 * 
	 * */ 

	public Matrix() {
		loadMatrix();
	}

	private double[][] m;
	private int NUM_ROWS;
	private int NUM_COLS;

	// to save time, this HashMap maps a pivot entry's column to its row
	// this allows the use of containsKey() to look up pivot columns in constant
	// time
	private HashMap<Integer, Integer> pivotEntries = new HashMap<>();

	/**
	 * Checks if the matrix is in RREF form, and if it isn't, executes steps to put
	 * it in RREF form
	 * 
	 * @return true if the matrix if in RREF form, false if not
	 */
	public boolean isRREF() {
		// check indices of 1s, making sure they form a staircase
		// check that everything else in the col is 0

		// scan matrix row by row
		// at the first non-zero or non-one value, call method to eliminate it and put
		// that row into RREF form

		// detects a matrix with no solutions
		if (pivotEntries.containsKey(NUM_COLS - 1)) {
			return true;
		}

		List<Integer> pivotCols = new ArrayList<Integer>();
		for (int i = 0; i < NUM_ROWS; i++) {
			for (int j = 0; j < NUM_COLS; j++) {
				// if the current element is a 1...
				if (m[i][j] == 1) {
					// and we are not looking at a pivot column...
					if (!pivotEntries.containsKey(j)) {
						// make the column a pivot column, and return
						makePivotCol(i, j);
						return false;
					}
					// otherwise, the current element is a pivot entry, so remember it's column, and
					// continue looking in the next row
					pivotCols.add(j);
					break;
					// if the element is not a 1 or a 0...
				} else if (m[i][j] != 0) {
					// reduce the row, and return
					reduceRow(i, j);
					return false;
				}
			}
		}

		int prev = -1;
		for (int i = 0; i < pivotCols.size(); i++) {
			if (pivotCols.get(i) <= prev) {
				orderByZeroes();
				return false;
			}
			if (!isPivotCol(i, pivotCols.get(i))) {
				return false;
			}
			prev = pivotCols.get(i);
		}

		return true;
	}

	/**
	 * determines and prints the meaning of the matrix in RREF form
	 */
	public void interpretRREFMatrix() {
		if (pivotEntries.containsKey(NUM_COLS - 1)) {
			// since the augmented column is a pivot column, these matrices have no solution
			System.out.println("The matrix has no solution.");
		} else if (pivotEntries.size() == NUM_COLS - 1) {
			// since all non-augmented columns are pivot columns, these matrices have one
			// unique solution
			System.out.println("The matrix has a unique solution.");
			for (int i = 0; i < pivotEntries.size(); i++) {
				System.out.println("x" + i + " = " + fmt(m[i][NUM_COLS - 1]));
			}
		} else {
			// these matrices have free variables with infinite solutions
			System.out.println("The matrix has infinite solutions.");
			String[] freeVarNames = { "s", "t", "r", "u", "v", "w", "q", "a", "b", "c", "d", "l", "m", "n", "p", "y",
					"z", "f", "g", "h" };
			HashMap<Integer, String> freeVars = new HashMap<>();
			int currentName = 0;
			for (int i = 0; i < NUM_COLS - 1; i++) {
				if (!pivotEntries.containsKey(i)) {
					System.out.println("x" + (i + 1) + " is a free variable.");
					freeVars.put(i, freeVarNames[currentName++]);
				}
			}
			System.out.println();
			for (int i = 0; i < NUM_COLS - 1; i++) {
				if (pivotEntries.containsKey(i)) {
					boolean firstEntrySeen = false;
					double[] rowSol = m[pivotEntries.get(i)];
					StringBuilder sol = new StringBuilder("x" + (i + 1) + " = ");
					for (int j = i + 1; j < NUM_COLS - 1; j++) {
						if (rowSol[j] > 0) {
							if (firstEntrySeen) {
								sol.append(" - ");
							} else {
								firstEntrySeen = true;
								sol.append("-");
							}
							if (rowSol[j] != 1) {
								sol.append(fmt(rowSol[j]));
							}
						} else if (rowSol[j] < 0) {
							if (firstEntrySeen) {
								sol.append(" + ");
							} else {
								firstEntrySeen = true;
							}
							if (rowSol[j] != -1) {
								sol.append(fmt(-rowSol[j]));
							}
						}
						if (rowSol[j] != 0) {
							sol.append(freeVars.get(j));
						}
					}
					if (rowSol[NUM_COLS - 1] > 0) {
						if (firstEntrySeen) {
							sol.append(" + ");
						}
						sol.append(fmt(rowSol[NUM_COLS - 1]));
					} else if (rowSol[NUM_COLS - 1] < 0) {
						if (firstEntrySeen) {
							sol.append(" - ");
						} else {
							sol.append("-");
						}
						sol.append(fmt(-rowSol[NUM_COLS - 1]));
					} else {
						sol.append("0");
					}
					System.out.println(sol.toString());
				} else {
					System.out.println("x" + (i + 1) + " = " + freeVars.get(i));
				}
			}
		}
	}

	/**
	 * swaps rows with the most leading zeroes to the bottom of the matrix
	 */
	private void orderByZeroes() {
		HashMap<Integer, Integer> mostZeroes = new HashMap<>();
		PriorityQueue<Integer> minHeap = new PriorityQueue<>();
		int[] newRowPos = new int[NUM_ROWS];

		double[][] ordered = new double[NUM_ROWS][NUM_COLS];
		int nextIndex = 0;
		for (int i = 0; i < NUM_ROWS; i++) {
			if (m[i][0] == 0) {
				int zeroes = 1;
				for (int j = 1; j < NUM_COLS; j++) {
					if (m[i][j] != 0) {
						break;
					} else {
						zeroes++;
					}
				}
				mostZeroes.put(zeroes, i);
				minHeap.offer(zeroes);
			} else {
				newRowPos[i] = nextIndex;
				ordered[nextIndex++] = m[i];
			}
		}

		while (!minHeap.isEmpty()) {
			int oldRow = mostZeroes.get(minHeap.poll());
			newRowPos[oldRow] = nextIndex;
			ordered[nextIndex++] = m[oldRow];
		}

		for (int i : pivotEntries.keySet()) {
			pivotEntries.replace(i, newRowPos[pivotEntries.get(i)]);
		}

		m = ordered;
	}

	/**
	 * "reduces" a row by setting m[row][col] to 1 via subtractRows(), and updates
	 * the row with its proper values
	 * 
	 * @param row the index of the row to reduce
	 * @param col the index of the column of the value that is to be reduced to 1
	 */
	private void reduceRow(int row, int col) {
		System.out.println("Reducing " + m[row][col] + " in row " + row);
		// to reduce this row, the value at m[i][j] needs to become 1
		// scan col for the best value to reduce by (we need a difference of 1)

		double toReduce = m[row][col];
		for (int i = 0; i < NUM_ROWS; i++) {
			if (col > 0 && m[i][col - 1] != 0) {
				continue;
			}
			if (toReduce == -1) {
				multiplyRow(row, -1);
				break;
			} else if (toReduce == 2 && m[i][col] == 1) {
				subtractRows(row, i, 1);
				break;
			} else if (toReduce == 2 && m[i][col] == -1) {
				subtractRows(row, i, -1);
				break;
			} else if (toReduce % m[i][col] == 1) {
				subtractRows(row, i, Math.floor(toReduce / m[i][col]));
				break;
			} else if (toReduce % m[i][col] == -1) {
				multiplyRow(row, -1);
				subtractRows(row, i, Math.floor(-toReduce / m[i][col]));
				break;
			} else if (m[i][col] == 1) {
				subtractRows(row, i, toReduce - 1);
				break;
			}
		}

		makePivotCol(row, col);
	}


	/**
	 * multiplies an entire row by a constant
	 * 
	 * @param r the index of the target row
	 * @param k the constant
	 */
	private void multiplyRow(int r, double k) {
		for (int i = 0; i < NUM_COLS; i++) {
			m[r][i] *= k;
		}
	}

	/**
	 * subtracts row subtractor from row target
	 * 
	 * @param target     the index of the row to subtract from
	 * @param subtractor the index of the row whose values are subtracted
	 * @param k          a constant that multiplies values in subtractor
	 */
	private void subtractRows(int target, int subtractor, double k) {
		System.out.println("Subtracting row " + subtractor + " from row " + target + "with mulitplier " + k);
		for (int i = 0; i < NUM_COLS; i++) {
			m[target][i] -= (k * m[subtractor][i]);
		}
	}

	/**
	 * creates a pivot column after a row has been reduced by zeroing out all other
	 * values in the column
	 * 
	 * @param row the index of the row that has been reduced
	 * @param col the index of the column of the pivot entry in row
	 */
	private void makePivotCol(int row, int col) {
		System.out.println("running makePivotCol");
		if (isPivotCol(row, col)) {
			return;
		}
		if (m[row][col] != 1) {
			multiplyRow(row, 1 / m[row][col]);
		}
		for (int i = 0; i < NUM_ROWS; i++) {
			if (i != row) {
				subtractRows(i, row, m[i][col]);
			}
		}
		pivotEntries.put(col, row);
	}

	/**
	 * checks if a column is a pivot column or not
	 * 
	 * @param pivRow the index of the row of the pivot entry
	 * @param col    the index of the column to check
	 * @return true if the column is a pivot column, false if not
	 */
	private boolean isPivotCol(int pivRow, int col) {
		if (pivotEntries.containsKey(col)) {
			return true;
		}
		for (int i = 0; i < NUM_ROWS; i++) {
			if (i != pivRow) {
				if (m[i][col] != 0) {
					return false;
				}
			} else if (m[i][col] != 1) {
				return false;
			}
		}
		if (!pivotEntries.containsKey(col)) {
			pivotEntries.put(col, pivRow);
		}
		return true;
	}

	/**
	 * loads the matrix by asking the user to input its values entry by entry, row
	 * by row
	 */
	private void loadMatrix() {
		Scanner s = new Scanner(System.in);
		List<List<Double>> rows;
		while (true) {
			rows = new ArrayList<>();

			System.out.println("Enter the coefficients of the matrix, row by row.");
			System.out.println("Enter 'd' when you're done.");

			String input = s.nextLine();
			String[] inputs = input.split(" ");
			final int ROW_SIZE = inputs.length;
			double sum = 0;
			while (!input.equals("d")) {
				try {
					if (inputs.length != ROW_SIZE) {
						throw new Exception();
					}
					List<Double> row = new LinkedList<>();
					for (String str : inputs) {
						double t = Double.parseDouble(str);
						row.add(t);
						sum += t;
					}
					rows.add(row);
				} catch (NumberFormatException e) {
					System.out.println("Error: matrix can only contain numerical values.");
				} catch (Exception e) {
					System.out.println("Error: rows must all be the same length: " + ROW_SIZE);
				}
				input = s.nextLine();
				inputs = input.split(" ");
			}

			if (sum == 0) {
				System.out.println("You've entered a zero matrix! Please re-enter the matrix values.");
				continue;
			}

			System.out.println("Is this the correct augmented matrix?");
			for (List<Double> r : rows) {
				for (int i = 0; i < r.size(); i++) {
					System.out.print(fmt(r.get(i)) + " ");
					if (i == r.size() - 2) {
						System.out.print("| ");
					}
				}
				System.out.println();
			}

			System.out.println("Enter 'y' to solve, or anything else to re-enter the matrix");
			input = s.nextLine();

			if (input.equals("y")) {
				if (rows.size() > 0) {
					break;
				} else {
					System.out.println("Error: no matrix entered.");
				}
			}
		}

		NUM_ROWS = rows.size();
		NUM_COLS = rows.get(0).size();
		m = new double[NUM_ROWS][NUM_COLS];
		for (int i = 0; i < NUM_ROWS; i++) {
			for (int j = 0; j < NUM_COLS; j++) {
				m[i][j] = rows.get(i).get(j);
			}
		}
		s.close();
	}

	/**
	 * prints the matrix to standard output
	 */
	public void printMatrix() {
		for (int i = 0; i < NUM_ROWS; i++) {
			for (int j = 0; j < NUM_COLS; j++) {
				System.out.print(fmt(m[i][j]) + " ");
				if (j == NUM_COLS - 2) {
					System.out.print("| ");
				}
			}
			System.out.println();
		}
		System.out.println();
	}

	/**
	 * a method to print a double as in int if it's of the form x.0 taken from
	 * https://stackoverflow.com/questions/703396/how-to-nicely-format-floating-numbers-to-string-without-unnecessary-decimal-0
	 * 
	 * @param d the double to check
	 * @return either the int form of d, or d itself
	 */
	public static String fmt(double d) {
		if (d == (long) d)
			return String.format("%d", (long) d);
		else
			return String.format("%s", d);
	}

}