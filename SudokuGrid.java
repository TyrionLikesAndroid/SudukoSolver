import java.awt.*;
import java.util.HashMap;

public class SudokuGrid
{
    static public final int EMPTY_CELL = 0;
    static public final int GRID_LENGTH = 9;
    static public final Integer [] SOLUTION_SET = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    private final HashMap<Point, Integer> cellValues;

    public SudokuGrid()
    {
        cellValues = new HashMap<>();

        // Initialize all of the cells to empty
        for(int i = 1; i <= GRID_LENGTH; i++) {
            for (int j = 1; j <= GRID_LENGTH; j++)
                cellValues.put(new Point(i, j), EMPTY_CELL);
        }
    }

    public void setGridValue(Point aPoint, int value)
    {
        cellValues.put(aPoint,value);
    }

    public int getGridValue(Point aPoint)
    {
        return cellValues.get(aPoint);
    }

    public void printGrid()
    {
        Point testPoint = new Point();

        for(int i = 1; i <= GRID_LENGTH; i++) {
            for (int j = 1; j <= GRID_LENGTH; j++) {
                testPoint.setLocation(j,i);
                System.out.print(" " + cellValues.get(testPoint) + "");
            }
            System.out.println();
        }
    }
}