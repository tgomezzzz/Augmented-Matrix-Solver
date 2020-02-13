import java.util.*;

public class Matrix {

    public Matrix(){
        loadMatrix();
    }

    /**
	 * loads the matrix by asking the user to input its values entry by entry, row by row
	 * @param s the scanner that gets user input
	 * @return the entered matrix
	 */
	private void loadMatrix(){
        Scanner s = new Scanner(System.in);
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

        NUM_ROWS = rows.size();
        NUM_COLS = rows.get(0).size();
		m = new int[NUM_ROWS][NUM_COLS];
		for (int i = 0; i < m.length; i++){
			for  (int j  = 0; j < m[0].length; j++){
				m[i][j] = rows.get(i).get(j);
			}
        }
        s.close();
	}


    private int[][] m;
    private int NUM_ROWS;
    private int NUM_COLS;

}