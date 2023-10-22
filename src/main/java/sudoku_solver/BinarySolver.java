package sudoku_solver;

import java.util.ArrayList;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

public class BinarySolver {
    public int matrixSize;
    public int blockSize;
    public int bitSize;
    public ISolver solver = SolverFactory.newDefault();
    public ArrayList<int[]> clauses = new ArrayList<int[]>();
    public ArrayList<int[]> input;
    public long executionTime;
    public int[][] result; ;
    BinarySolver(int matrixSize, ArrayList<int[]> input) throws ContradictionException {
        this.matrixSize = matrixSize;
        this.blockSize = (int) Math.sqrt(matrixSize);
        this.bitSize = log2(matrixSize - 1);
        this.input = input;

        result = new int[matrixSize][matrixSize];
        for (int[] element : input) {
            solver.addClause(new VecInt(element));
        }

        this.generateFirstRuleClauses();
        this.generateSecondRuleClauses();
        this.generateThirdRuleClauses();
        if (this.matrixSize == this.blockSize * this.blockSize) {
            this.generateFourthRuleClauses();
        }

        this.solve();
    }

    private void generateFirstRuleClauses() throws ContradictionException {
        //AMO encoding: ensure each cell in matrix has at most one value
        for (int i = 1; i <= matrixSize; i++) {
            for (int j = 1; j <= matrixSize; j++) {
                for (int k = 1; k <= matrixSize; k++) {
                    int[] AMOClause = new int[2];
                    for (int y = 0; y < bitSize; y++) {
                        AMOClause[0] = -index0(i, j, k);
                        if (getbit(k - 1, y) == 1) AMOClause[1] = index1(i, j, y);
                        else AMOClause[1] = -index1(i, j, y);
                        solver.addClause(new VecInt(AMOClause));
                    }
                }
            }
        }

        //ALO encoding: ensure each cell in matrix has at least one value
        for (int i = 1; i <= matrixSize; i++) {
            for (int j = 1; j <= matrixSize; j++) {
                int[] ALOClause = new int[matrixSize];
                for (int k = 1; k <= matrixSize; k++) {
                    ALOClause[k - 1] = index0(i, j, k);
                }
                solver.addClause(new VecInt(ALOClause));
            }
        }
    }

    private void generateSecondRuleClauses() throws ContradictionException {
        //ensure each value exists at most one each row
        for (int i = 1; i <= matrixSize; i++) {
            for (int k = 1; k <= matrixSize; k++) {
                for (int j = 1; j <= matrixSize; j++) {
                    int[] AMOClause = new int[2];
                    AMOClause[0] = -index0(i, j, k);
                    for (int y = 0; y < bitSize; y++) {
                        if (getbit(j - 1, y) == 1) AMOClause[1] = index2(i, k, y);
                        else AMOClause[1] = -index2(i, k, y);
                        solver.addClause(new VecInt(AMOClause));
                    }
                }
            }
        }

        //ensure each value exists at least one each row
        for (int i = 1; i <= matrixSize; i++) {
            for (int k = 1; k <= matrixSize; k++) {
                int[] ALOClause = new int[matrixSize];
                for (int j = 1; j <= matrixSize; j++) {
                    ALOClause[j - 1] = index0(i, j, k);
                }
                solver.addClause(new VecInt(ALOClause));
            }
        }
    }

    private void generateThirdRuleClauses() throws ContradictionException {
        //ensure each value exists at most one each column
        for (int j = 1; j <= matrixSize; j++) {
            for (int k = 1; k <= matrixSize; k++) {
                for (int i = 1; i <= matrixSize; i++) {
                    int[] AMOClause = new int[2];
                    for (int y = 0; y < bitSize; y++) {
                        AMOClause[0] = -index0(i, j, k);
                        if (getbit(i - 1, y) == 1) AMOClause[1] = index3(j, k, y);
                        else AMOClause[1] = -index3(j, k, y);
                        solver.addClause(new VecInt(AMOClause));
                    }
                }
            }
        }

        //ensure each value exists at least one each column
        for (int j = 1; j <= matrixSize; j++) {
            for (int k = 1; k <= matrixSize; k++) {
                int[] ALOClause = new int[matrixSize];
                for (int i = 1; i <= matrixSize; i++) {
                    ALOClause[i - 1] = index0(i, j, k);
                }
                solver.addClause(new VecInt(ALOClause));
            }
        }
    }

    private void generateFourthRuleClauses() throws ContradictionException {
        //ensure each value exists at least one each block
        for (int k = 1; k <= matrixSize; k++) {
            for (int bi = 1; bi <= blockSize; bi++) {
                for (int bj = 1; bj <= blockSize; bj++) {
                    for (int i = (bi - 1) * blockSize + 1; i <= bi * blockSize; i++) {
                        for (int j = (bj - 1) * blockSize + 1; j <= bj * blockSize; j++) {
                            int blockId = (bi - 1) * blockSize + bj;
                            int positionId = (i - 1 - (bi - 1) * blockSize) * blockSize + (j - 1 - (bj - 1) * blockSize);
                            int[] AMOClause = new int[2];
                            for (int y = 0; y < bitSize; y++) {
                                AMOClause[0] = -index0(i, j, k);
                                if (getbit(positionId, y) == 1) AMOClause[1] = index4(k, blockId, y);
                                else AMOClause[1] = -index4(k, blockId, y);
                                solver.addClause(new VecInt(AMOClause));
                            }
                        }
                    }
                }
            }
        }

        //ensure each value exists at least one each block
        for (int k = 1; k <= matrixSize; k++) {
            for (int bi = 1; bi <= blockSize; bi++) {
                for (int bj = 1; bj <= blockSize; bj++) {
                    int[] ALOClause = new int[matrixSize];
                    int num = 0;
                    for (int i = (bi - 1) * blockSize + 1; i <= bi * blockSize; i++) {
                        for (int j = (bj - 1) * blockSize + 1; j <= bj * blockSize; j++) {
                            ALOClause[num] = index0(i, j, k);
                            num++;
                        }
                    }
                    solver.addClause(new VecInt(ALOClause));
                }
            }
        }
    }

    private int getbit(int n, int i) {
        return (n >> i) & 1;
    }

    private int index0(int i, int j, int k) {
        return (i - 1) * matrixSize * matrixSize + (j - 1) * matrixSize + k;
    }

    private int index1(int i, int j, int y) {
        return matrixSize * matrixSize * matrixSize + (i - 1) * matrixSize * bitSize + (j - 1) * bitSize + y + 1;
    }

    private int index2(int i, int j, int y) {
        return matrixSize * matrixSize * (matrixSize + bitSize) + (i - 1) * matrixSize * bitSize + (j - 1) * bitSize + y + 1;
    }

    private int index3(int i, int j, int y) {
        return matrixSize * matrixSize * (matrixSize + 2 * bitSize) + (i - 1) * matrixSize * bitSize + (j - 1) * bitSize + y + 1;
    }

    private int index4(int i, int j, int y) {
        return matrixSize * matrixSize * (matrixSize + 3 * bitSize) + (i - 1) * matrixSize * bitSize + (j - 1) * bitSize + y + 1;
    }

    private int log2(int n) {
        int res = 0;
        while (n > 0) {
            res++;
            n = n / 2;
        }
        return res;
    }

    private void solve() throws ContradictionException {
        try {
            solver.newVar(matrixSize * matrixSize * matrixSize + 4 * matrixSize * matrixSize * bitSize);

            long startTime = System.currentTimeMillis();
            if (solver.isSatisfiable()) {
                long endTime = System.currentTimeMillis(); // Record the end time
                executionTime = endTime - startTime;
                System.out.print("Time: ");
                System.out.println(executionTime);
                logResults();
            } else {
                System.out.println("UNSAT");
            }
        } catch (TimeoutException e) {
            System.out.println("Timeout");
        }
    }

    public void logResults() {
        int[] model = solver.model();
        System.out.println(model.length);
        System.out.println(solver.nVars());
        int count = 1;
        int countRow = 0;
        for (int i = 1; i <= matrixSize; i++) {
            for (int j = 1; j <= matrixSize; j++) {
                for (int k = 1; k <= matrixSize; k++) {
                    if (model[index0(i, j, k) - 1] > 0) {
                        result[i - 1][j - 1] = printValue(model[index0(i, j, k) - 1]);
                        if (j < matrixSize && matrixSize == blockSize * blockSize && j % blockSize == 0) {
                            System.out.print("|");
                        }
                        break;
                    }
                }

            }
            System.out.println();
            if (i % blockSize == 0) System.out.println();
        }
        System.out.println();
        if (checkResult(result) == 0) System.out.println("INCORRECT");
        else System.out.println("CORRECT");
    }

    public static void printArray(int[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i] + " ");
        }
        System.out.println();
    }

    public int printValue(int index) {
        int k = (index % matrixSize);
        if (k == 0) k = matrixSize;
        index = (index - k) / matrixSize;
        int i = index / matrixSize;
        int j = index % matrixSize;

        if (k <= 9) {
            System.out.print(" " + k + " ");
        } else  {
            System.out.print(k + " ");
        }
        return k;
    }

    public static int[] arrayListToList(ArrayList<Integer> arrayList) {
        int[] array = new int[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            array[i] = arrayList.get(i);
        }
        return array;
    }

    private int checkResult(int[][] values) {
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (values[i][j] < 1 || values[i][j] > matrixSize) {
                    System.out.println("invalid value");
                    System.out.println(i + " " + j + " " + values[i][j]);
                    return 0;
                }
            }
        }

        for (int i = 0; i < matrixSize; i++) {
            for (int j1 = 0; j1 < matrixSize; j1++) {
                for (int j2 = 0; j2 < matrixSize; j2++) {
                    if (j1 != j2 && (values[i][j1] == values[i][j2] || values[j1][i] == values[j2][i])) {
                        System.out.println("duplicate value");
                        if (values[i][j1] == values[i][j2]) System.out.println("row" + i + " " + j1 + " " + j2);
                        if (values[j1][i] == values[j2][i]) System.out.println("column" + i + " " + j1 + " " + j2);
                        return 0;
                    }
                }
            }
        }

        if (matrixSize != blockSize * blockSize) return 1;
        for (int i1 = 0; i1 < matrixSize; i1++) {
            for (int i2 = 0; i2 < matrixSize; i2++) {
                for (int j1 = 0; j1 < matrixSize; j1++) {
                    for (int j2 = 0; j2 < matrixSize; j2++) {
                        if (i1 != i2 || j1 != j2) {
                            if (i1 / blockSize == i2 / blockSize && j1 / blockSize == j2 / blockSize)
                                if (values[i1][j1] == values[i2][j2]) return 0;
                        }
                    }
                }
            }
        }
        return 1;
    }

}
