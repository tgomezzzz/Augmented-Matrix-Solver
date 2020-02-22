import java.util.*;

public class AugMatrixSolver {

	public static void main(String[] bears) {

		Matrix m = new Matrix();
		
		int steps = 0;
		while (!m.isRREF() && steps++ < 10){
			m.printMatrix();
		}

		m.interpretRREFMatrix();

	}
}	
