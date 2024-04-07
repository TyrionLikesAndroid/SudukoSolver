import java.awt.*;

public class SudokuTestHarness {

    // All Sudoku puzzles are loaded with the following format {column, row, value}.
    // Column 1 is the leftmost and row 1 is the top

    // New York Times Sudoku - EASY (4/6/24)
    private static final int[][] testSudoku_1 = {
            { 2, 1, 9 }, { 4, 1, 2 }, { 7, 1, 1 }, { 8, 1, 3 }, { 9, 1, 8 },
            { 1, 2, 8 }, { 2, 2, 7 }, { 3, 2, 6 }, { 5, 2, 9 },
            { 1, 3, 2 }, { 2, 3, 3 }, { 4, 3, 8 }, { 7, 3, 6 }, { 8, 3, 9 },
            { 2, 4, 2 }, { 3, 4, 9 }, { 4, 4, 3 }, { 6, 4, 6 }, { 8, 4, 1 },
            { 1, 5, 7 }, { 5, 5, 1 }, { 6, 5, 2 }, { 8, 5, 5 },
            { 1, 6, 6 }, { 3, 6, 3 }, { 4, 6, 5 }, { 5, 6, 7 },
            { 3, 7, 4 }, { 5, 7, 2 }, { 6, 7, 8 }, { 9, 7, 1 },
            { 6, 8, 1 }, { 7, 8, 2 }, { 9, 8, 3 },
            { 1, 9, 1 }, { 6, 9, 9 }, { 7, 9, 4 }, { 8, 9, 6 }};

    public static void main(String[] args)
    {
        SudokuGrid grid = new SudokuGrid();
        int [][] testSudoku = testSudoku_1;

        for(int i = 0; i < testSudoku.length; i++)
            grid.setGridValue(new Point(testSudoku[i][0], testSudoku[i][1]), testSudoku[i][2]);

        grid.printGrid();

        SudokuSolver solver = new SudokuSolver(grid);
        solver.printHeuristics();

        solver.solve();
    }
}