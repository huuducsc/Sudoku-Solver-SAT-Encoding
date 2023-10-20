package sudoku_solver;

import java.util.ArrayList;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

public class BionomialSolver {
    public int matrixSize;
    public int blockSize;
    public ISolver solver = SolverFactory.newDefault();
    public ArrayList<int[]> clauses = new ArrayList<int[]>();
    public ArrayList<int[]> input;
    public int[][] result;
    BionomialSolver(int matrixSize, ArrayList<int[]> input) {
        this.matrixSize = matrixSize;
        this.blockSize = (int) Math.sqrt(matrixSize);
        this.input = input;
        this.generateFirstRuleClauses();
        this.generateSecondRuleClauses();
        this.generateThirdRuleClauses();
        if (this.matrixSize == this.blockSize * this.blockSize) {
            this.generateFourthRuleClauses();
        }

        result = new int[matrixSize][matrixSize];
        try {
            this.solve();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
    }

    private void generateFirstRuleClauses() {
        for (int i = 1; i <= matrixSize; i++) {
            for (int j = 1; j <= matrixSize; j++) {
                //at least one
                //each position has at least a value from 1 -> matrixSize
                int[] ALOClause = new int[matrixSize];
                for (int k = 1; k <= matrixSize; k++) {
                    ALOClause[k - 1] = index(i, j, k);
                }
                clauses.add(ALOClause);

                //at most one
                //if a position already has a value, it can't get any other value
                for (int val1 = 1; val1 <= matrixSize; val1++) {
                    for (int val2 = 1; val2 <= matrixSize; val2++) {
                        if (val1 != val2) {
                            ArrayList<Integer> AMOClause = new ArrayList<Integer>();
                            AMOClause.add(-index(i, j, val1));
                            AMOClause.add(-index(i, j, val2));
                            clauses.add(arrayListToList(AMOClause));
                        }
                    }
                }
            }
        }
    }

    private void generateSecondRuleClauses() {
        //ensure each value exists once each row
        for (int i = 1; i <= matrixSize; i++) {
            for (int k = 1; k <= matrixSize; k++) {
                int[] ALOColumn = new int[matrixSize];
                for (int j = 1; j <= matrixSize; j++) {
                    ALOColumn[j - 1] = index(i, j, k);
                }
                clauses.add(ALOColumn);

                for (int j1 = 1; j1 <= matrixSize; j1++) {
                    for (int j2 = 1; j2 <= matrixSize; j2++) {
                        if (j1 != j2) {
                            ArrayList<Integer> oneValuePerRow = new ArrayList<Integer>();
                            oneValuePerRow.add(-index(i, j1, k));
                            oneValuePerRow.add(-index(i, j2, k));
                            clauses.add(arrayListToList(oneValuePerRow));
                        }
                    }
                }
            }
        }
    }

    private void generateThirdRuleClauses() {
        //ensure each value exists once each column
        for (int j = 1; j <= matrixSize; j++) {
            for (int k = 1; k <= matrixSize; k++) {
                int[] ALORow = new int[matrixSize];
                for (int i = 1; i <= matrixSize; i++) {
                    ALORow[i - 1] = index(i, j, k);
                }
                clauses.add(ALORow);

                for (int i1 = 1; i1 <= matrixSize; i1++) {
                    for (int i2 = 1; i2 <= matrixSize; i2++) {
                        if (i1 != i2) {
                            ArrayList<Integer> OneValuePerColumn = new ArrayList<Integer>();
                            OneValuePerColumn.add(-index(i1, j, k));
                            OneValuePerColumn.add(-index(i2, j, k));
                            clauses.add(arrayListToList(OneValuePerColumn));
                        }
                    }
                }
            }
        }
    }

    private void generateFourthRuleClauses() {

        for (int k = 1; k <= matrixSize; k++) {
            for (int i = 1; i <= blockSize; i++) {
                for (int j = 1; j <= blockSize; j++) {
                    int startBlock_i = blockSize * i - (blockSize - 1);
                    int startBlock_j = blockSize * j - (blockSize - 1);
                    int[] ALOValuePerBlock = new int[matrixSize];
                    for (int b = 1; b <= matrixSize; b++) {
                        int b_i = startBlock_i + (b - 1) / blockSize;
                        int b_j = startBlock_j + (b - 1) % blockSize;
                        ALOValuePerBlock[b - 1] = index(b_i, b_j, k);

                        for (int bo = 1; bo <= matrixSize; bo++) {
                            int bo_i = startBlock_i + (bo - 1) / blockSize;
                            int bo_j = startBlock_j + (bo - 1) % blockSize;
                            if (bo_i != b_i || bo_j != b_j) {
                                ArrayList<Integer> AMOValuePerBlock = new ArrayList<Integer>();
                                AMOValuePerBlock.add(-index(b_i, b_j, k));
                                AMOValuePerBlock.add(-index(bo_i, bo_j, k));
                                clauses.add(arrayListToList(AMOValuePerBlock));
                            }
                        }
                    }
                    clauses.add(ALOValuePerBlock);
                }
            }
        }
    }
    private void solve() throws ContradictionException {

        try {
            solver.newVar(matrixSize * matrixSize * matrixSize); // Tạo biến với 2 biến

            for (int[] element : clauses) {
                solver.addClause(new VecInt(element));
            }

            for (int[] element : input) {
                solver.addClause(new VecInt(element));
            }

            long startTime = System.currentTimeMillis();
            if (solver.isSatisfiable()) {
                long endTime = System.currentTimeMillis(); // Record the end time
                long executionTime = endTime - startTime;
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
        int count = 1;
        int countRow = 0;
        for (int i = 1; i <= solver.nVars(); i++) {
            if (model[i - 1] > 0) {
                if (matrixSize == blockSize * blockSize) {
                    if (countRow == blockSize) {
                        System.out.println();
                        countRow = 0;
                    }
                }

                printValue(model[i - 1]);

                if (count == matrixSize) {
                    count = 0;
                    countRow++;
                    System.out.println();
                }
                if (matrixSize == blockSize * blockSize) {
                    if (count < matrixSize && (count % blockSize) == 0 && count > 0) {
                        System.out.print("|");
                    }
                }

                count++;
            }
        }
        if (checkResult(this.result) == 0) System.out.println("INCORRECT");
        else System.out.println("CORRECT");
        System.out.println();
    }
    private int index(int i, int j, int k) {
        return (i - 1) * matrixSize * matrixSize + (j - 1) * matrixSize + k;
    }
    public void printValue(int index) {
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
        this.result[i][j] = k;
    }
    public static void printArray(int[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i] + " ");
        }
        System.out.println();
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
