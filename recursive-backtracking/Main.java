import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Main {

    private static int nodesVisited = 0; // Static variable to count nodes visited

    public static boolean recursiveBacktracking(int magicSum, Integer[][] magicSquare, Set<Integer> unassignedNumbers) {

        nodesVisited++; // Increment the count each time the function is called

        // First, find an empty cell. If no empty cell is found,
        // the current assignment is complete.
        int[] emptyCell = getRandomEmptyCell(magicSquare);
        if (emptyCell == null) {
            printSquare(magicSquare);
            return true;
        }

        // Try every number in the set of unassigned numbers
        Integer[] tempUnassigned = unassignedNumbers.toArray(new Integer[0]);
        for (Integer value : tempUnassigned) {
            if (isConsistentWithConstraints(magicSum, magicSquare, emptyCell[0], emptyCell[1], value)) {
                magicSquare[emptyCell[0]][emptyCell[1]] = value;
                unassignedNumbers.remove(value); // Remove the number from the set

                // Recurse with the new state
                if (recursiveBacktracking(magicSum, magicSquare, unassignedNumbers)) {
                    return true;
                }

                // If the recursion did not lead to a solution, undo the move
                magicSquare[emptyCell[0]][emptyCell[1]] = null;
                unassignedNumbers.add(value); // Add the number back to the set
            }
        }

        // If no value leads to a solution from this state, return false to backtrack
        return false;
    }

    public static boolean isConsistentWithConstraints(int magicSum, Integer[][] magicSquare, int x, int y, int value) {

        if (!checkSum(magicSquare[x], y, value, magicSum) || !checkSum(getColumn(magicSquare, y), x, value, magicSum)) {
            return false;
        }

        if (x == y && !checkSum(getMainDiagonal(magicSquare), x, value, magicSum)) {
            return false;
        }

        if (x + y == magicSquare.length - 1 && !checkSum(getSecondaryDiagonal(magicSquare), x, value, magicSum)) {
            return false;
        }

        return true;
    }

    private static boolean checkSum(Integer[] line, int skipIndex, int value, int magicSum) {
        int sum = value;
        boolean complete = true;
        for (int i = 0; i < line.length; i++) {
            if (i != skipIndex) {
                if (line[i] == null) {
                    complete = false;
                } else {
                    sum += line[i];
                }
            }
        }
        return !complete || sum == magicSum;
    }

    public static Integer[] getColumn(Integer[][] array, int index) {
        Integer[] column = new Integer[array[0].length]; // Here I assume a rectangular 2D array!
        for (int i = 0; i < column.length; i++) {
            column[i] = array[i][index];
        }
        return column;
    }

    public static Integer[] getMainDiagonal(Integer[][] magicSquare) {
        Integer[] mainDiagonal = new Integer[magicSquare.length];
        for (int i = 0; i < magicSquare.length; i++) {
            mainDiagonal[i] = magicSquare[i][i];
        }
        return mainDiagonal;
    }

    public static Integer[] getSecondaryDiagonal(Integer[][] magicSquare) {
        Integer[] secondaryDiagonal = new Integer[magicSquare.length];
        for (int i = 0; i < magicSquare.length; i++) {
            secondaryDiagonal[i] = magicSquare[i][magicSquare.length - 1 - i];
        }
        return secondaryDiagonal;
    }

    public static int[] getRandomEmptyCell(Integer[][] magicSquare) {
        List<int[]> emptyPositions = new ArrayList<>();
        // Iterate over all positions in the square to collect empty positions
        for (int i = 0; i < magicSquare.length; i++) {
            for (int j = 0; j < magicSquare[i].length; j++) {
                if (magicSquare[i][j] == null) {
                    emptyPositions.add(new int[] { i, j }); // Add the indices of the empty cell
                }
            }
        }

        // Check if there are any empty positions
        if (emptyPositions.isEmpty()) {
            return null; // Return null if no empty positions are found
        }

        // Randomly select an empty position from the list
        Random random = new Random();
        return emptyPositions.get(random.nextInt(emptyPositions.size()));
    }

    public static void printSquare(Integer[][] magicSquare) {
        for (Integer[] row : magicSquare) {
            for (Integer value : row) {
                if (value == null) {
                    System.out.print("_ ");
                } else {
                    System.out.print(value + " ");
                }
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {

        int n = Integer.parseInt(args[0]);
        int magicSum = (n * ((int) Math.pow(n, 2) + 1)) / 2;
        Set<Integer> unassignedNumbers = new HashSet<>();
        Integer[][] magicSquare = new Integer[n][n];

        // Initializing general domain
        for (int i = 1; i <= n * n; i++) {
            unassignedNumbers.add(i);
        }

        long startTime = System.currentTimeMillis(); // Start time measurement
        recursiveBacktracking(magicSum, magicSquare, unassignedNumbers);
        long endTime = System.currentTimeMillis(); // End time measurement

        // Output the results
        System.out.println("Nodes visited: " + nodesVisited);
        System.out.println("Time taken: " + (endTime - startTime) + " ms");

    }

}
