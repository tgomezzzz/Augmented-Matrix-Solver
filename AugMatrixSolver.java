import java.util.*;

public class AugMatrixSolver {

	public static void main(String[] bears) {

		System.out.println( 1.0 == 1 );

		Matrix m = new Matrix();
		
		int steps = 0;
		while (true && steps++ < 10){
			if (m.isRREF()){
				break;
			}
			m.printMatrix();
		}

		System.out.println("The matrix in RREF form:");
		//printMatrix(ma);

		m.interpretRREFMatrix();

	}

}	
