import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Main {

    private static int nodesVisited = 0; // Static variable to count nodes visited

    public static boolean recursiveBacktracking(int magicSum, Integer[][] magicSquare, Set<Integer> unassignedNumbers,
            Set<Integer>[][] domains) {

        nodesVisited++; // Increment the count each time the function is called

        // First, find an empty cell. If no empty cell is found,
        // the current assignment is complete.
        int[] emptyCell = getRandomEmptyCell(magicSquare);
        if (emptyCell == null) {
            printSquare(magicSquare);
            return true;
        }

        int x = emptyCell[0];
        int y = emptyCell[1];

        // Intersection of unassigned numbers and the domain of the current cell
        Set<Integer> possibleValues = new HashSet<>(domains[x][y]);
        possibleValues.retainAll(unassignedNumbers);

        for (Integer value : possibleValues) {
            if (isConsistentWithConstraints(magicSum, magicSquare, x, y, value)) {

                magicSquare[x][y] = value;
                unassignedNumbers.remove(value); // Remove the number from the global set

                // Update domains and recurse
                Set<Integer>[][] newDomains = deepCopyDomains(domains); // Deep copy the domains for backtracking
                if (updateDomains(x, y, value, newDomains, magicSquare, magicSum)) {
                    if (recursiveBacktracking(magicSum, magicSquare, unassignedNumbers, newDomains)) {
                        return true;
                    }
                }

                // If the recursion did not lead to a solution, undo the move
                magicSquare[x][y] = null;
                unassignedNumbers.add(value); // Add the number back to the set
                // No need to restore domains, as we work with a copy when recursing

            }
        }

        // If no value leads to a solution from this state, return false to backtrack
        return false;
    }

    private static boolean updateDomains(int x, int y, int value, Set<Integer>[][] domains, Integer[][] magicSquare,
            int magicSum) {
        int n = magicSquare.length;

        // Remove value from all domains in the same row, column, and diagonals
        for (int i = 0; i < n; i++) {
            if (i != y)
                domains[x][i].remove(value);
            if (i != x)
                domains[i][y].remove(value);
            if (x == y && i != x)
                domains[i][i].remove(value);
            if (x + y == n - 1 && i != x)
                domains[i][n - 1 - i].remove(value);
        }

        // Check and prune based on required sums
        if (!pruneBasedOnSums(domains, magicSquare, magicSum, x, y, n)) {
            return false;
        }

        return true;
    }

    private static boolean pruneBasedOnSums(Set<Integer>[][] domains, Integer[][] magicSquare, int magicSum, int x,
            int y, int n) {
        // Example for rows, similar logic needed for columns and diagonals
        for (int i = 0; i < n; i++) {
            if (!pruneForRow(domains, magicSquare, magicSum, i, n))
                return false;
            if (!pruneForColumn(domains, magicSquare, magicSum, i, n))
                return false;
        }
        return true;
    }

    private static boolean pruneForRow(Set<Integer>[][] domains, Integer[][] magicSquare, int magicSum, int row,
            int n) {
        int currentSum = 0;
        List<Integer> nullIndices = new ArrayList<>();
        for (int j = 0; j < n; j++) {
            if (magicSquare[row][j] == null) {
                nullIndices.add(j);
            } else {
                currentSum += magicSquare[row][j];
            }
        }

        if (nullIndices.size() == 1) { // Only one value can complete the sum
            int neededValue = magicSum - currentSum;
            int lastIdx = nullIndices.get(0);
            domains[row][lastIdx].clear();
            domains[row][lastIdx].add(neededValue);
            if (!domains[row][lastIdx].contains(neededValue) || neededValue < 1 || neededValue > n * n) {
                return false; // Infeasible or invalid value
            }
        }

        return true;
    }

    private static boolean pruneForColumn(Set<Integer>[][] domains, Integer[][] magicSquare, int magicSum, int column,
            int n) {
        int currentSum = 0;
        List<Integer> nullIndices = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (magicSquare[i][column] == null) {
                nullIndices.add(i);
            } else {
                currentSum += magicSquare[i][column];
            }
        }

        if (nullIndices.size() == 1) { // Only one value can complete the sum
            int neededValue = magicSum - currentSum;
            int lastIdx = nullIndices.get(0);
            domains[lastIdx][column].clear();
            domains[lastIdx][column].add(neededValue);
            if (!domains[lastIdx][column].contains(neededValue) || neededValue < 1 || neededValue > n * n) {
                return false; // Infeasible or invalid value
            }
        }

        return true;
    }

    private static Set<Integer>[][] deepCopyDomains(Set<Integer>[][] original) {
        int size = original.length;
        @SuppressWarnings("unchecked")
        Set<Integer>[][] copy = new HashSet[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                copy[i][j] = new HashSet<>(original[i][j]);
            }
        }
        return copy;
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
        // Initializing general domain
        for (int i = 1; i <= n * n; i++) {
            unassignedNumbers.add(i);
        }

        Integer[][] magicSquare = new Integer[n][n];

        @SuppressWarnings("unchecked")
        Set<Integer>[][] domains = new HashSet[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                domains[i][j] = new HashSet<>(unassignedNumbers); // Initially, all numbers are possible
            }
        }

        long startTime = System.currentTimeMillis(); // Start time measurement
        recursiveBacktracking(magicSum, magicSquare, unassignedNumbers, domains);
        long endTime = System.currentTimeMillis(); // End time measurement

        // Output the results
        System.out.println("Nodes visited: " + nodesVisited);
        System.out.println("Time taken: " + (endTime - startTime) + " ms");

    }

}
