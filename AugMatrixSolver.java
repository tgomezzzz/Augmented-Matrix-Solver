import java.util.*;

public class AugMatrixSolver {

	public static void main(String[] bears) {

		Matrix m = new Matrix();
		
		int steps = 0;
		while (true && steps++ < 10){
			if (m.isRREF()){
				break;
			}
			m.printMatrix();
		}

		m.interpretRREFMatrix();

	}

}	
