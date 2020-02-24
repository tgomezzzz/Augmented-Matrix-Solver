import java.util.*;

public class AugMatrixSolver {

	public static void main(String[] bears) {

		Matrix m = new Matrix();

		while (!m.isRREF()) {
			m.printMatrix();
		}

		m.interpretRREFMatrix();
		System.out.println();

	}
}
