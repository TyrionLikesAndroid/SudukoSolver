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

    // New York Times Sudoku - MEDIUM (4/7/24)
    private static final int[][] testSudoku_2 = {
            { 5, 1, 3 }, { 9, 1, 7 },
            { 4, 2, 4 }, { 6, 2, 6 }, { 8, 2, 1 },
            { 5, 3, 5 }, { 6, 3, 1 }, { 9, 3, 6 },
            { 2, 4, 4 }, { 3, 4, 1 }, { 9, 4, 8 },
            { 1, 5, 7 }, { 3, 5, 9 },
            { 1, 6, 6 }, { 5, 6, 9 }, { 7, 6, 2 },
            { 1, 7, 4 }, { 3, 7, 7 }, { 4, 7, 8 },
            { 3, 8, 2 }, { 5, 8, 1 }, { 6, 8, 7 }, { 7, 8, 3 },
            { 6, 9, 4 } };

    // New York Times Sudoku - HARD (4/7/24)
    private static final int[][] testSudoku_3 = {
            { 3, 1, 2 },
            { 1, 2, 5 }, { 6, 2, 3 }, { 8, 2, 2 },
            { 3, 3, 9 }, { 4, 3, 4 }, { 8, 3, 5 },
            { 4, 4, 5 }, { 6, 4, 6 }, { 7, 4, 4 },
            { 4, 5, 3 }, { 7, 5, 7 },
            { 1, 6, 1 }, { 2, 6, 7 }, { 3, 6, 8 },
            { 2, 7, 5 }, { 4, 7, 1 }, { 5, 7, 4 }, { 9, 7, 6 },
            { 2, 8, 8 }, { 8, 8, 4 },
            { 5, 9, 5 }, { 7, 9, 8 } };

    // https://app.crackingthecryptic.com/7gJb9G8fRt - VERY HARD (4/7/24)
    private static final int[][] testSudoku_4 = {
            { 4, 1, 1 }, { 6, 1, 2 },
            { 2, 2, 6 }, { 8, 2, 7 },
            { 3, 3, 8 }, { 7, 3, 9 },
            { 1, 4, 4 }, { 9, 4, 3 },
            { 2, 5, 5 }, { 6, 5, 7 },
            { 1, 6, 2 }, { 5, 6, 8 }, { 9, 6, 1 },
            { 3, 7, 9 }, { 7, 7, 8 }, { 9, 7, 5 },
            { 2, 8, 7 }, { 8, 8, 6 },
            { 4, 9, 3 }, { 6, 9, 4 } };

    // https://sudoku2.com/play-the-hardest-sudoku-in-the-world - VERY HARD (4/7/24)
    private static final int[][] testSudoku_5 = {
            { 1, 1, 8 },
            { 3, 2, 3 }, { 4, 2, 6 },
            { 2, 3, 7 }, { 5, 3, 9 }, { 7, 3, 2 },
            { 2, 4, 5 }, { 6, 4, 7 },
            { 5, 5, 4 }, { 6, 5, 5 }, { 7, 5, 7 },
            { 4, 6, 1 }, { 8, 6, 3 },
            { 3, 7, 1 }, { 8, 7, 6 }, { 9, 7, 8 },
            { 3, 8, 8 }, { 4, 8, 5 }, { 8, 8, 1 },
            { 2, 9, 9 }, { 7, 9, 4 } };

    // https://en.wikipedia.org/wiki/Mathematics_of_Sudoku# - VERY HARD (4/7/24)
    private static final int[][] testSudoku_6 = {
            { 8, 1, 1 },
            { 6, 2, 2 }, { 9, 2, 3 },
            { 4, 3, 4 },
            { 7, 4, 5 },
            { 1, 5, 4 }, { 3, 5, 1 }, { 4, 5, 6 },
            { 3, 6, 7 }, { 4, 6, 1 },
            { 2, 7, 5 }, { 7, 7, 2 },
            { 5, 8, 8 }, { 8, 8, 4 },
            { 2, 9, 3 }, { 4, 9, 9 }, { 5, 9, 1 } };

    public static void main(String[] args)
    {
        SudokuGrid grid = new SudokuGrid();
        int [][] testSudoku = testSudoku_6;

        for(int i = 0; i < testSudoku.length; i++)
            grid.setGridValue(new Point(testSudoku[i][0], testSudoku[i][1]), testSudoku[i][2]);

        grid.printGrid();

        SudokuSolver solver = new SudokuSolver(grid);
        //solver.printHeuristics();

        solver.solve();
    }
}