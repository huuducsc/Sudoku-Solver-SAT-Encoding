package sudoku_solver;

import java.util.ArrayList;

import org.sat4j.specs.ContradictionException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SudokuMain {
    public static void main(String[] args) throws ContradictionException, IOException {
        int numTest = 10;
        File summaryOutput = new File("src/main/java/sudoku_solver/output/SummaryResult.csv");
        BufferedWriter summaryWriter = new BufferedWriter(new FileWriter(summaryOutput));
        summaryWriter.write("Test, Size, Bionomial, , , Binary, , , Sequential" + "\n");
        summaryWriter.write(", , Variables, Clauses, Time, Variables, Clauses, Time, Variables, Clauses, Time" + "\n");
        for (int i = 1; i <= numTest; i++) {
            int[][] inputMatrix = readInput("src/main/java/sudoku_solver/input/input" + i + ".txt");
            File bionomialOutput = new File("src/main/java/sudoku_solver/output/BionomialOutput" + i + ".txt");
            File binaryOutput = new File("src/main/java/sudoku_solver/output/BinaryOutput" + i + ".txt");
            File sequentialOutput = new File("src/main/java/sudoku_solver/output/SequentialOutput" + i + ".txt");

            summaryWriter.write(i + "," + inputMatrix.length + ",");

            try {
                BionomialSolver bionomialSolver = new BionomialSolver(inputMatrix.length, standardInput(inputMatrix));
                writeOutput(
                        bionomialOutput,
                        bionomialSolver.executionTime,
                        bionomialSolver.solver.nVars(),
                        bionomialSolver.solver.nConstraints(),
                        bionomialSolver.result
                );
                summaryWriter.write(bionomialSolver.solver.nVars() + ","
                        + bionomialSolver.solver.nConstraints() + "," + bionomialSolver.executionTime + ",");

                        
                SequentialCounterSolver sc = new SequentialCounterSolver(inputMatrix.length, standardInput(inputMatrix));
               
            } catch (OutOfMemoryError e) {
                summaryWriter.write("out of memory, out of memory, out of memory, ");
            }
            BinarySolver binarySolver = new BinarySolver(inputMatrix.length, standardInput(inputMatrix));
            writeOutput(
                    binaryOutput,
                    binarySolver.executionTime,
                    binarySolver.solver.nVars(),
                    binarySolver.solver.nConstraints(),
                    binarySolver.result
            );
            summaryWriter.write(binarySolver.solver.nVars() + ","
                    + binarySolver.solver.nConstraints() + "," + binarySolver.executionTime + ",");
            try {
                SequentialCounterSolver sc = new SequentialCounterSolver(inputMatrix.length, standardInput(inputMatrix));
                writeOutput(
                        sequentialOutput,
                        sc.time,
                        sc.variables,
                        sc.clauses,
                        sc.OutputMatrix
                );
                summaryWriter.write(sc.variables + ","+ sc.clauses + "," + sc.time + "\n");
            } catch (OutOfMemoryError e) {
                summaryWriter.write("out of memory, out of memory, out of memory\n");
            }
        }
        summaryWriter.close();
    }
    public static int[][] readInput(String path) {
        int matrixSize;
        try {
            File file = new File(path);
            Scanner scanner = new Scanner(file);
            String firstElement = scanner.next();
            matrixSize = Integer.parseInt(firstElement);

            int[][] array = new int[matrixSize][matrixSize];
            int num = 0;
            while (scanner.hasNext()) {
                String element = scanner.next();
                if (element.equals(".")) array[num / matrixSize][num % matrixSize] = 0;
                else array[num / matrixSize][num % matrixSize] = Integer.parseInt(element);
                num++;
            }
            scanner.close();

            return array;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new int[0][0];
    }
    public static void writeOutput(
            File output,
            long executionTime,
            int nVars,
            int nConstraints,
            int[][] resultMatrix
    ) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(output));
        writer.write("execute time: " + executionTime + "\n");
        writer.write("vars number: " + nVars + "\n");
        writer.write("constraints number: " + nConstraints + "\n");
        writer.write("matrix result: " + "\n");
        System.out.println(resultMatrix.length);
        for (int i = 0; i < resultMatrix.length; i++) {
            for (int j = 0; j < resultMatrix[i].length; j++) {
                if (resultMatrix[i][j] <= 9)
                    writer.write(" " + resultMatrix[i][j] + " ");
                else
                    writer.write(resultMatrix[i][j] + " ");
            }
            writer.write("\n");
        }
        writer.close();
    }

    public static ArrayList<int[]> standardInput(int[][] input) {
        int matrixSize = input.length;
        ArrayList<int[]> RealInput = new ArrayList<int[]>();
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (input[i][j] > 0) {
                    int val = input[i][j];
                    RealInput.add(new int[]{i * matrixSize * matrixSize + j * matrixSize + val});
                }
            }
        } 
        return RealInput;
    }
}