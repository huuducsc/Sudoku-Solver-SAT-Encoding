package sudoku_solver;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

public class SudokuSequentialCounterSolver {
    public int MatrixSize;
    public int BlockSize;
    public ArrayList<int[]> FirstRuleClause;
    public ArrayList<int[]> SecondRuleClause;
    public ArrayList<int[]> ThirdRuleClause;
    public ArrayList<int[]> FourthRuleClause;
    public ArrayList<int[]> Input;
    public Map<String, Integer> AddedVariableIdMap;
    public int AVCount = 0; // AddedVariable Count
    public String Status = "SAT";
    SudokuSequentialCounterSolver(int MatrixSize, ArrayList<int[]> Input) {
        this.MatrixSize = MatrixSize;
        this.BlockSize = (int) Math.sqrt(MatrixSize);
        this.Input = Input;
        this.FirstRuleClause = new ArrayList<int[]>();
        this.SecondRuleClause = new ArrayList<int[]>();
        this.ThirdRuleClause = new ArrayList<int[]>();
        this.FourthRuleClause = new ArrayList<int[]>();
        this.AddedVariableIdMap = new HashMap<String, Integer>();
        this.GenerateFirstRuleClauses();
        this.GenerateSecondRuleClauses();
        this.GenerateThirdRuleClauses();
        if (this.MatrixSize == 9 || this.MatrixSize == 16 || this.MatrixSize == 25 || this.MatrixSize == 36) {
            this.GenerateFourthRuleClauses();
        }
        // this.PrintClause();
        try {
            this.Solve();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
    }

    private void GenerateFirstRuleClauses() {
        for (int i = 1; i <= MatrixSize; i++) {
            for (int j = 1; j <= MatrixSize; j++) {
                ArrayList<Integer> ALO = new ArrayList<Integer>();

                int AVId = -1;
                String StringAV = "";
                for (int k = 1; k <= MatrixSize; k++) {
                    // make ALO clause
                    ALO.add(makeId(i, j, k));

                    // get or make idAV
                    int PreviousIdAV = AVId;
                    if (k != MatrixSize) {
                        StringAV = MakeStringAV(ALO); // make key for map AV
                        if (!AddedVariableIdMap.containsKey(StringAV)) {
                            AVCount++;
                            AVId = AVCount + MatrixSize * MatrixSize * MatrixSize;
                            AddedVariableIdMap.put(StringAV, AVId);
                        } else {
                            AVId = AddedVariableIdMap.get(StringAV);
                        }
                    }
                    // make AMO clause
                    ArrayList<Integer> ElementTrueSoAVTrueClause = new ArrayList<Integer>();
                    ArrayList<Integer> PreviousAVTrueSoAVTrueClause = new ArrayList<Integer>();
                    ArrayList<Integer> PreviousAVTrueSoElementFalseClause = new ArrayList<Integer>();
                    if (k == 1) {
                        ElementTrueSoAVTrueClause.add(-makeId(i, j, k));
                        ElementTrueSoAVTrueClause.add(AVId);
                        FirstRuleClause.add(ArrayListToList(ElementTrueSoAVTrueClause));

                    } else {
                        if (k != MatrixSize) {
                            ElementTrueSoAVTrueClause.add(-makeId(i, j, k));
                            ElementTrueSoAVTrueClause.add(AVId);
                            PreviousAVTrueSoAVTrueClause.add(-PreviousIdAV);
                            PreviousAVTrueSoAVTrueClause.add(AVId);
                            PreviousAVTrueSoElementFalseClause.add(-PreviousIdAV);
                            PreviousAVTrueSoElementFalseClause.add(-makeId(i, j, k));

                            FirstRuleClause.add(ArrayListToList(ElementTrueSoAVTrueClause));
                            FirstRuleClause.add(ArrayListToList(PreviousAVTrueSoAVTrueClause));
                            FirstRuleClause.add(ArrayListToList(PreviousAVTrueSoElementFalseClause));
                        } else {
                            PreviousAVTrueSoElementFalseClause.add(-PreviousIdAV);
                            PreviousAVTrueSoElementFalseClause.add(-makeId(i, j, k));
                            FirstRuleClause.add(ArrayListToList(PreviousAVTrueSoElementFalseClause));
                        }
                    }
                    // System.out.println(idAV);
                }
                FirstRuleClause.add(ArrayListToList(ALO));
            }
        }
    }

    private void GenerateSecondRuleClauses() {
        for (int i = 1; i <= MatrixSize; i++) {
            for (int k = 1; k <= MatrixSize; k++) {
                ArrayList<Integer> ALO = new ArrayList<Integer>();

                int AVId = -1;
                String StringAV = "";
                for (int j = 1; j <= MatrixSize; j++) {
                    // make ALO clause
                    ALO.add(makeId(i, j, k));

                    // get or make idAV
                    int PreviousIdAV = AVId;
                    if (j != MatrixSize) {
                        StringAV = MakeStringAV(ALO); // make key for map AV
                        if (!AddedVariableIdMap.containsKey(StringAV)) {
                            AVCount++;
                            AVId = AVCount + MatrixSize * MatrixSize * MatrixSize;
                            AddedVariableIdMap.put(StringAV, AVId);
                        } else {
                            AVId = AddedVariableIdMap.get(StringAV);
                        }
                    }
                    // make AMO clause
                    ArrayList<Integer> ElementTrueSoAVTrueClause = new ArrayList<Integer>();
                    ArrayList<Integer> PreviousAVTrueSoAVTrueClause = new ArrayList<Integer>();
                    ArrayList<Integer> PreviousAVTrueSoElementFalseClause = new ArrayList<Integer>();
                    if (j == 1) {
                        ElementTrueSoAVTrueClause.add(-makeId(i, j, k));
                        ElementTrueSoAVTrueClause.add(AVId);
                        SecondRuleClause.add(ArrayListToList(ElementTrueSoAVTrueClause));

                    } else {
                        if (j != MatrixSize) {
                            ElementTrueSoAVTrueClause.add(-makeId(i, j, k));
                            ElementTrueSoAVTrueClause.add(AVId);
                            PreviousAVTrueSoAVTrueClause.add(-PreviousIdAV);
                            PreviousAVTrueSoAVTrueClause.add(AVId);
                            PreviousAVTrueSoElementFalseClause.add(-PreviousIdAV);
                            PreviousAVTrueSoElementFalseClause.add(-makeId(i, j, k));

                            SecondRuleClause.add(ArrayListToList(ElementTrueSoAVTrueClause));
                            SecondRuleClause.add(ArrayListToList(PreviousAVTrueSoAVTrueClause));
                            SecondRuleClause.add(ArrayListToList(PreviousAVTrueSoElementFalseClause));
                        } else {
                            PreviousAVTrueSoElementFalseClause.add(-PreviousIdAV);
                            PreviousAVTrueSoElementFalseClause.add(-makeId(i, j, k));
                            SecondRuleClause.add(ArrayListToList(PreviousAVTrueSoElementFalseClause));
                        }
                    }
                    // PrintArray(ArrayListToList(AllowableRangeValue));
                }
                SecondRuleClause.add(ArrayListToList(ALO));
            }
        }
    }

    private void GenerateThirdRuleClauses() {
        for (int j = 1; j <= MatrixSize; j++) {
            for (int k = 1; k <= MatrixSize; k++) {
                ArrayList<Integer> ALO = new ArrayList<Integer>();

                int AVId = -1;
                String StringAV = "";
                for (int i = 1; i <= MatrixSize; i++) {
                    // make ALO clause
                    ALO.add(makeId(i, j, k));

                    // get or make idAV
                    int PreviousIdAV = AVId;
                    if (i != MatrixSize) {
                        StringAV = MakeStringAV(ALO); // make key for map AV
                        if (!AddedVariableIdMap.containsKey(StringAV)) {
                            AVCount++;
                            AVId = AVCount + MatrixSize * MatrixSize * MatrixSize;
                            AddedVariableIdMap.put(StringAV, AVId);
                        } else {
                            AVId = AddedVariableIdMap.get(StringAV);
                        }
                    }
                    // make AMO clause
                    ArrayList<Integer> ElementTrueSoAVTrueClause = new ArrayList<Integer>();
                    ArrayList<Integer> PreviousAVTrueSoAVTrueClause = new ArrayList<Integer>();
                    ArrayList<Integer> PreviousAVTrueSoElementFalseClause = new ArrayList<Integer>();
                    if (i == 1) {
                        ElementTrueSoAVTrueClause.add(-makeId(i, j, k));
                        ElementTrueSoAVTrueClause.add(AVId);
                        ThirdRuleClause.add(ArrayListToList(ElementTrueSoAVTrueClause));

                    } else {
                        if (i != MatrixSize) {
                            ElementTrueSoAVTrueClause.add(-makeId(i, j, k));
                            ElementTrueSoAVTrueClause.add(AVId);
                            PreviousAVTrueSoAVTrueClause.add(-PreviousIdAV);
                            PreviousAVTrueSoAVTrueClause.add(AVId);
                            PreviousAVTrueSoElementFalseClause.add(-PreviousIdAV);
                            PreviousAVTrueSoElementFalseClause.add(-makeId(i, j, k));

                            ThirdRuleClause.add(ArrayListToList(ElementTrueSoAVTrueClause));
                            ThirdRuleClause.add(ArrayListToList(PreviousAVTrueSoAVTrueClause));
                            ThirdRuleClause.add(ArrayListToList(PreviousAVTrueSoElementFalseClause));
                        } else {
                            PreviousAVTrueSoElementFalseClause.add(-PreviousIdAV);
                            PreviousAVTrueSoElementFalseClause.add(-makeId(i, j, k));
                            ThirdRuleClause.add(ArrayListToList(PreviousAVTrueSoElementFalseClause));
                        }
                    }
                    // System.out.println(idAV);
                }
                ThirdRuleClause.add(ArrayListToList(ALO));
            }
        }
    }

    private void GenerateFourthRuleClauses() {

        for (int k = 1; k <= MatrixSize; k++) {
            for (int i = 1; i <= BlockSize; i++) {
                for (int j = 1; j <= BlockSize; j++) {
                    int startBlock_i = BlockSize * i - (BlockSize - 1);
                    int startBlock_j = BlockSize * j - (BlockSize - 1);
                    ArrayList<Integer> ALO = new ArrayList<Integer>();
                    int AVId = -1;
                    String StringAV = "";
                    for (int b = 1; b <= MatrixSize; b++) {
                        int b_i = startBlock_i + (b - 1) / BlockSize;
                        int b_j = startBlock_j + (b - 1) % BlockSize;
                        ALO.add(makeId(b_i, b_j, k));
                        // get or make idAV
                        int PreviousIdAV = AVId;
                        if (b != MatrixSize) {
                            StringAV = MakeStringAV(ALO); // make key for map AV
                            if (!AddedVariableIdMap.containsKey(StringAV)) {
                                AVCount++;
                                AVId = AVCount + MatrixSize * MatrixSize * MatrixSize;
                                AddedVariableIdMap.put(StringAV, AVId);
                            } else {
                                AVId = AddedVariableIdMap.get(StringAV);
                            }
                        }
                        // make AMO clause
                        ArrayList<Integer> ElementTrueSoAVTrueClause = new ArrayList<Integer>();
                        ArrayList<Integer> PreviousAVTrueSoAVTrueClause = new ArrayList<Integer>();
                        ArrayList<Integer> PreviousAVTrueSoElementFalseClause = new ArrayList<Integer>();
                        if (b == 1) {
                            ElementTrueSoAVTrueClause.add(-makeId(b_i, b_j, k));
                            ElementTrueSoAVTrueClause.add(AVId);
                            ThirdRuleClause.add(ArrayListToList(ElementTrueSoAVTrueClause));

                        } else {
                            if (b != MatrixSize) {
                                ElementTrueSoAVTrueClause.add(-makeId(b_i, b_j, k));
                                ElementTrueSoAVTrueClause.add(AVId);
                                PreviousAVTrueSoAVTrueClause.add(-PreviousIdAV);
                                PreviousAVTrueSoAVTrueClause.add(AVId);
                                PreviousAVTrueSoElementFalseClause.add(-PreviousIdAV);
                                PreviousAVTrueSoElementFalseClause.add(-makeId(b_i, b_j, k));

                                ThirdRuleClause.add(ArrayListToList(ElementTrueSoAVTrueClause));
                                ThirdRuleClause.add(ArrayListToList(PreviousAVTrueSoAVTrueClause));
                                ThirdRuleClause.add(ArrayListToList(PreviousAVTrueSoElementFalseClause));
                            } else {
                                PreviousAVTrueSoElementFalseClause.add(-PreviousIdAV);
                                PreviousAVTrueSoElementFalseClause.add(-makeId(b_i, b_j, k));
                                ThirdRuleClause.add(ArrayListToList(PreviousAVTrueSoElementFalseClause));
                            }
                        }
                    }
                    FourthRuleClause.add(ArrayListToList(ALO));
                }
            }
        }
    }

    private void Solve() throws ContradictionException {
        ISolver solver = SolverFactory.newDefault();
        try {
            solver.newVar(MatrixSize * MatrixSize * MatrixSize + AVCount); // Tạo biến với 2 biến

            for (int[] element : FirstRuleClause) {
                solver.addClause(new VecInt(element));
            }
            for (int[] element : SecondRuleClause) {
                solver.addClause(new VecInt(element));
            }
            for (int[] element : ThirdRuleClause) {
                solver.addClause(new VecInt(element));
            }
            if (this.MatrixSize == 9 || this.MatrixSize == 16 || this.MatrixSize == 25 || this.MatrixSize == 36) {
                for (int[] element : FourthRuleClause) {
                    solver.addClause(new VecInt(element));
                }
            }
            for (int[] element : Input) {
                solver.addClause(new VecInt(element));
            }

            if (solver.isSatisfiable()) {
                int[] model = solver.model();
                int count = 1;
                int countRow = 0;
                for (int i = 1; i <= MatrixSize * MatrixSize * MatrixSize; i++) {
                    if (model[i - 1] > 0) {
                        if (MatrixSize == 9 || MatrixSize == 16 || MatrixSize == 25 || this.MatrixSize == 36) {
                            if (countRow == BlockSize) {
                                for (int j = 1; j <= BlockSize * 14; j++) {
                                    System.out.print("-");
                                }
                                System.out.println();
                                countRow = 0;
                            }
                        }

                        IdToPositon(model[i - 1]);

                        if (count == MatrixSize) {
                            count = 0;
                            countRow++;
                            System.out.println();
                        }
                        if (MatrixSize == 9 || MatrixSize == 16 || MatrixSize == 25 || this.MatrixSize == 36) {
                            if (count < MatrixSize && (count % BlockSize) == 0 && count > 0) {
                                System.out.print("|");
                            }
                        }

                        count++;
                    }
                    // System.out.println("Bien" + i + " = " + model[i - 1]);
                }
            } else {
                System.out.println("UNSAT");
                Status = "UNSAT";
            }
        } catch (TimeoutException e) {
            System.out.println("Timeout");
        }
    }

    private int makeId(int i, int j, int k) {
        return (i - 1) * MatrixSize * MatrixSize + (j - 1) * MatrixSize + k;
    }

    public String MakeStringAV(ArrayList<Integer> representedElements) {
        String result = "";
        for (int element : representedElements) {
            result += Integer.toString(element) + ",";
        }
        return result.substring(0, result.length() - 1);
    }

    public void PrintClause() {
        for (int[] element : FirstRuleClause) {
            PrintArray(element);
        }
        // System.out.println(
        // FirstRuleClause.size() + SecondRuleClause.size() + ThirdRuleClause.size() +
        // FourthRuleClause.size());
    }

    public void IdToPositon(int id) {
        int i = (id / (MatrixSize * MatrixSize)) + 1;
        int j = ((id % (MatrixSize * MatrixSize)) / MatrixSize) + 1;
        int k = (id % MatrixSize);
        if (k == 0) {
            j = j - 1;
            k = k + MatrixSize;
        }
        if (k <= 9) {
            System.out.print(" " + k + " ");
        } else {
            System.out.print(k + " ");
        }

    }

    public static void PrintArray(int[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i] + " ");
        }
        System.out.println();
    }

    public static int[] ArrayListToList(ArrayList<Integer> arrayList) {
        int[] array = new int[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            array[i] = arrayList.get(i);
        }
        return array;
    }
}
