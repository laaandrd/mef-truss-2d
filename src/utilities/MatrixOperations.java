package utilities;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MatrixOperations {

	/*
	 * Metodo responsavel por multiplicar duas matrizes e alocar o resultado em uma terceira matriz
	 */
	public static void multiplyDoubleMatrix(double[][] matrix1, double[][] matrix2, double[][] result) {

		if (matrix1[0].length == matrix2.length) {
			for (int i = 0; i < matrix1.length; i++) {
				for (int j = 0; j < matrix2[0].length; j++) {
					double soma = 0;
					for (int k = 0; k < matrix2.length; k++) {
						soma += matrix1[i][k] * matrix2[k][j];
					}
					result[i][j] = soma;
				}
			}
		} else {
			System.out.println("You can not multiply these matrices.");
		}
	}

	/*
	 * Metodo responsavel por imprimir as matrizes em um formato numerico conveniente
	 */
	public static void printDoubleMatrixWithFormat(double[][] matrix, NumberFormat nf) {
		if (nf == null) {
			nf = new DecimalFormat("0.000E0");
		}
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				if (matrix[i][j] >= 0.000000001) {
					System.out.printf(" " + nf.format(matrix[i][j]) + " ");
				} else if (matrix[i][j] <= -0.000000001) {
					System.out.printf(nf.format(matrix[i][j]) + " ");
				} else {
					System.out.printf(" " + nf.format(0) + " ");
				}
			}
			System.out.println("");
		}
	}

	/*
	 * Metodo responsavel por transpor uma matriz e alocar o resultado em uma segunda matriz
	 */
	public static void transposeDoubleMatrix(double[][] matrix, double[][] transposed) {
		if (matrix.length == transposed[0].length && matrix[0].length == transposed.length) {
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[0].length; j++) {
					transposed[j][i] = matrix[i][j];
				}
			}
		}
	}

	// THE METHODS BELOW WERE NOT WRITTEN BY ME
	// SOURCE: https://www.sanfoundry.com/java-program-find-inverse-matrix/

	private static double determinant(double[][] matrix) {
		if (matrix.length != matrix[0].length)
			throw new IllegalStateException("invalid dimensions");

		if (matrix.length == 2)
			return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];

		double det = 0;
		for (int i = 0; i < matrix[0].length; i++)
			det += Math.pow(-1, i) * matrix[0][i] * determinant(submatrix(matrix, 0, i));
		return det;
	}

	public static double[][] inverse(double[][] matrix) {
		double[][] inverse = new double[matrix.length][matrix.length];

		// minors and cofactors
		for (int i = 0; i < matrix.length; i++)
			for (int j = 0; j < matrix[i].length; j++)
				inverse[i][j] = Math.pow(-1, i + j) * determinant(submatrix(matrix, i, j));

		// adjugate and determinant
		double det = 1.0 / determinant(matrix);
		for (int i = 0; i < inverse.length; i++) {
			for (int j = 0; j <= i; j++) {
				double temp = inverse[i][j];
				inverse[i][j] = inverse[j][i] * det;
				inverse[j][i] = temp * det;
			}
		}

		return inverse;
	}

	private static double[][] submatrix(double[][] matrix, int row, int column) {
		double[][] submatrix = new double[matrix.length - 1][matrix.length - 1];

		for (int i = 0; i < matrix.length; i++)
			for (int j = 0; i != row && j < matrix[i].length; j++)
				if (j != column)
					submatrix[i < row ? i : i - 1][j < column ? j : j - 1] = matrix[i][j];
		return submatrix;
	}

}
