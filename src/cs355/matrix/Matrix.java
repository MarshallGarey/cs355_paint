package cs355.matrix;

/**
 * Created by Marshall Garey
 * This class supports square matrices only.
 */
public class Matrix {

    // The size of the matrix is NxN
    private final int N;

    // The matrix data.
    private double matrix[][];

    public Matrix(int size) {
        N = size;
        matrix = new double[N][N];
        for (int i = 0; i < N; i++) {
            for (int j= 0; j < N; j++) {
                matrix[i][j] = 0;
            }
        }
    }

    /**
     * Multiply this matrix by another matrix.
     * @param other The matrix to multiply by.
     * @return A new matrix, or null of param other is illegal.
     */
    public Matrix matrixMultiply(Matrix other) {
        if ((other == null) || (other.N != this.N)) {
            return null;
        }

        Matrix result = new Matrix(N);

        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                for (int k = 0; k < N; k++)
                    result.matrix[i][j] += this.matrix[i][k] * other.matrix[k][j];

        return result;
    }

    /**
     * Multiply this matrix by a vector.
     * @param vector The vector (must be an N-element double array) to multiply by.
     * @return An N-element integer array, or null if param vector is illegal.
     */
    public double[] vectorMultiply(double vector[]) {
        if (vector.length != N) {
            return null;
        }

        double result[] = new double[N];
        java.util.Arrays.fill(result, 0);

        for (int i = 0; i < N; i++)
                for (int k = 0; k < N; k++)
                    result[i] += this.matrix[i][k] * vector[k];

        return result;
    }

    public double[][] getMatrix() {
        return this.matrix;
    }

}
