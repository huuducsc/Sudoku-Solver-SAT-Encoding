package sudoku_solver;

import java.util.ArrayList;

import org.sat4j.specs.ContradictionException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class SudokuMain {
    public static void main(String[] args) throws ContradictionException {

        int[][] InputOption = readInput("src/main/java/sudoku_solver/input.txt");
        System.out.println(InputOption.length);
        BionomialSolver a = new BionomialSolver(InputOption.length, standardInput(InputOption));
        System.out.println(a.solver.nVars());
        System.out.println(a.solver.nConstraints());
        BinarySolver b = new BinarySolver(InputOption.length, standardInput(InputOption));
        System.out.println(b.solver.nVars());
        System.out.println(b.solver.nConstraints());
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