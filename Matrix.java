import java.util.*;

public class Matrix {

    public Matrix(){
        loadMatrix();
    }

    private double[][] m;
    private int NUM_ROWS;
    private int NUM_COLS;
    private HashSet<Integer> pivotColIndeces = new HashSet<>();


	/**
	 * Checks if the matrix is in RREF form, and if it isn't, executes steps to put it in RREF form 
	 * @param m the matrix
	 * @return true if the matrix if in RREF form, false if not
	 */
	public boolean isRREF(){
		//check indices of 1s, making sure they form a staircase
		//check that everything else in the col is 0

		//scan matrix row by row
		//at the first non-zero or non-one value, call method to eliminate it and put that row into RREF form
		//***edge case: array with no leading non-zeroes (they could all be one)

		int[] pivotCols = new int[NUM_ROWS];
		int ind = 0;
		for (int i = 0; i < NUM_ROWS; i++){
			for (int j = 0; j < NUM_COLS; j++){
				//if we see a one, mark it's column as a pivot column and continue to the next row
				//if we see a zero, ignore it
				//otherwise, reduce this row using row operations, and return false
				if (m[i][j] == 1){
                    pivotCols[ind++] = j;
                    if (!pivotColIndeces.contains(j)){
                        makePivotCol(i, j);
                    }
					break;
				} else if (m[i][j] != 0){
					reduceRow(i, j);
					//printMatrix(m);
					return false;
				}
			}
		}

		int prev = -1;
		for (int i = 0; i < pivotCols.length; i++){
			if (pivotCols[i] <= prev){
				orderByZeroes();
				return false;
			}
			if (!isPivotCol(i, pivotCols[i])){
				return false;
			}
			prev = pivotCols[i];
		}

		return true;
    }
    

    /**
	 * determines and prints the meaning of the matrix in RREF form
	 */
	public static void interpretRREFMatrix(){

	}


    /**
	 * swaps rows with the most leading zeroes to the bottom of the matrix
	 */
    private void orderByZeroes(){
		HashMap<Integer, Integer> mostZeroes = new HashMap<>();
		PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
		for (int i = 0; i < NUM_ROWS; i++){
			if (m[i][0] == 0){
				int zeroes = 1;
				for (int j = 1; j < NUM_COLS; j++){
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
			swapRows(rowToSwap, lastRow--);
			if (!maxHeap.isEmpty()){
				mostZeroes.replace(maxHeap.peek(), rowToSwap);
			}
		}
    }
    

    /**
	 * "reduces" a row by setting m[row][col] to 1 via subtractRows(), and updates the row with its proper values
	 * @param row the index of the row to reduce
	 * @param col the index of the column of the value that is to be reduced to 1
	 */
	private void reduceRow(int row, int col){
        System.out.println("Reducing " + m[row][col] + " in row " + row);
		//to reduce this row, the value at m[i][j] needs to become 1
		//scan col for the best value to reduce by (we need a difference of 1)

        double toReduce = m[row][col];
        if (isEmptyCol(row, col)){
            multiplyRow(row, 1 / toReduce);
            return;
        }
		for (int i = 0; i < NUM_ROWS; i++){
			if (!isValidReducerRow(i, col) || i == row){
				continue;
			}
			if (toReduce == -1){
				multiplyRow(row, -1);
				break;
			} else if (toReduce == 2 && m[i][col] == 1){
				System.out.println("2 and 1");
				subtractRows(row, i, 1);
				break;
			} else if (toReduce == 2 && m[i][col] == -1){
				System.out.println("2 and -1");	
				subtractRows(row, i, -1);
				break;
			} else if (toReduce % m[i][col] == 1){
                //buggy
				System.out.println("=1");
				subtractRows(row, i, Math.floor(toReduce / m[i][col]));
				break;
			} else if (toReduce % m[i][col] == -1){
                //buggy
				System.out.println("=-1");
				multiplyRow(row, -1);
				subtractRows(row, i, Math.floor(-toReduce / m[i][col]));
				break;
			} else if (m[i][col] == 1){
                subtractRows(row, i, toReduce-1);
                break;
			} 
        }
        
		makePivotCol(row, col);

    }
    
    /**
     * validates a row as a potential reducer by ensuring all values left of c are 0
     * @param r the index of the row
     * @param c the index of the starting column
     * @return true if all elements left of c are 0, false otherwise
     */
    public boolean isValidReducerRow(int r, int c){
        for (int i = c - 1; i > -1; i--){
            if (m[r][i] != 0){
                return false;
            }
        }
        return true;
    }


	/**
	 * swaps two rows
	 * @param r1 the index of the first row
	 * @param r2 the index of the second row
	 */
	private void swapRows(int r1, int r2){
		if (r1 == r2){
			return;
		}
		double[] t = m[r1];
		m[r1] = m[r2];
		m[r2] = t;
    }


	/**
	 * multiplies an entire row by a constant
	 * @param r the index of the target row
	 * @param k the constant
	 * @param m	the matrix
	 */
	private void multiplyRow(int r, double k){
		for (int i = 0; i < NUM_COLS; i++){
			m[r][i] *= k;
		}
    }
    

    /**
	 * subtracts row subtractor from row target
	 * @param target the index of the row to subtract from
	 * @param subtractor the index of the row whose values are subtracted
	 * @param k a constant that multiplies values in subtractor
	 */
	private void subtractRows(int target, int subtractor, double k){
        System.out.println("Subtracting row " + subtractor + " from row " + target + " with mulitplier " + k);
		for (int i = 0; i < NUM_COLS; i++){
			m[target][i] -= (k * m[subtractor][i]);
		}
    }
    

    /**
	 * creates a pivot column after a row has been reduced by zeroing out all other values in the column
	 * @param row the index of the row that has been reduced
	 * @param col the index of the column of the pivot entry in row
     * 	 */
	private void makePivotCol(int row, int col){
        System.out.println("running makePivotCol");
        if (pivotColIndeces.contains(col)){
            return;
        }
        if (m[row][col] != 1){
            multiplyRow(row, 1 / m[row][col]);
        }
		for (int i = 0; i < NUM_ROWS; i++){
			if (i != row){
				subtractRows(i, row, m[i][col]);
			}
        }
        pivotColIndeces.add(col);
	}


    /**
	 * checks if a column is a pivot column or not
	 * @param pivRow the index of the row of the pivot entry
	 * @param col the index of the column to check
	 * @return true if the column is a pivot column, false if not
	 */
	private boolean isPivotCol(int pivRow, int col){
        if (pivotColIndeces.contains(col)){
            return true;
        }
		for (int i = 0; i < NUM_ROWS; i++){
			if (i != pivRow){
				if (m[i][col] != 0){
					return false;
				}
			} else if (m[i][col] != 1){
				return false;
			}
        }
        if (!pivotColIndeces.contains(col)){
            pivotColIndeces.add(col);
        }
		return true;
	}


    /**
     * checks if a column is empty except for m[r][c]
     * @param r the index of the row of the sole entry
     * @param c the index of the column
     * @return true if m[r][c] is the only non-zero entry in the column, false if not 
     */
    public boolean isEmptyCol(int r, int c){
        for (int i = 0; i < NUM_ROWS; i++){
            if (i != r){
                if (m[i][c] != 0){
                    return false;
                }
            }
        }
        return true;
    }


    /**
	 * loads the matrix by asking the user to input its values entry by entry, row by row
	 * @param s the scanner that gets user input
	 * @return the entered matrix
	 */
	private void loadMatrix(){
        Scanner s = new Scanner(System.in);
		List<List<Double>> rows;
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
					List<Double> row = new LinkedList<>();
					for (String str : inputs){
						double t = Double.parseDouble(str);
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
			

			if (input.equals("y")){
				if (rows.size() > 0){
					break;
				} else {
					System.out.println("Error: no matrix entered.");
				}
			}
		}

        NUM_ROWS = rows.size();
        NUM_COLS = rows.get(0).size();
		m = new double[NUM_ROWS][NUM_COLS];
		for (int i = 0; i < NUM_ROWS; i++){
			for  (int j  = 0; j < NUM_COLS; j++){
				m[i][j] = rows.get(i).get(j);
			}
        }
        s.close();
    }
    

    /**
	 * prints the matrix to standard output
	 * @param m the matrix
	 */
	public void printMatrix(){
		for (int i = 0; i < NUM_ROWS; i++){
			for (int j = 0; j < NUM_COLS; j++){
				System.out.print(fmt(m[i][j]) + " ");
				if (j == NUM_COLS - 2){
					System.out.print("| ");
				}
			}
			System.out.println();
		}
		System.out.println();
    }
    

    /**
     * a method to print a double as in int if it's of the form x.0
     * taken from https://stackoverflow.com/questions/703396/how-to-nicely-format-floating-numbers-to-string-without-unnecessary-decimal-0
     * @param d the double to check
     * @return either the int form of d, or d itself
     */
    public static String fmt(double d){
        if(d == (long) d)
            return String.format("%d",(long)d);
        else
            return String.format("%s",d);
    }

}