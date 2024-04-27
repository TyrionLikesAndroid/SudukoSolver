import java.awt.*;
import java.util.*;

public class SudokuGrid
{
    static public final int EMPTY_CELL = 0;
    static public final int GRID_LENGTH = 9;
    static public final Integer [] SOLUTION_SET = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    private final HashMap<Point, Integer> cellValues = new HashMap<>();
    private final Integer [] valueSummary = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

    public SudokuGrid()
    {
        // Initialize all the cells to empty
        for(int i = 1; i <= GRID_LENGTH; i++) {
            for (int j = 1; j <= GRID_LENGTH; j++)
                cellValues.put(new Point(i, j), EMPTY_CELL);
        }
    }

    public void setGridValue(Point aPoint, int value)
    {
        // Cache the old value so we can apply the right logic in the valueSummary below
        int oldValue = cellValues.get(aPoint);
        cellValues.put(aPoint,value);

        // Decrement the departing value if there is one to decrement
        if(oldValue != EMPTY_CELL)
            valueSummary[oldValue] = --valueSummary[oldValue];

        // Increment the incoming value if there is one to increment
        if(value != EMPTY_CELL)
            valueSummary[value] = ++valueSummary[value];
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

    public int getLeastConstrainedOption(LinkedList<Integer> options)
    {
        int leastConstrainedOption = EMPTY_CELL;
        int leastConstrainedValue = GRID_LENGTH;

        Iterator<Integer> iter = options.iterator();
        while(iter.hasNext())
        {
            int anOption = iter.next();
            if(valueSummary[anOption] < leastConstrainedValue)
            {
                leastConstrainedOption = anOption;
                leastConstrainedValue = valueSummary[anOption];
            }
        }

        //System.out.println(options);
        //printValueSummary();
        //System.out.println("Least constrained number is [" + leastConstrainedOption +
        //        "] usage[" + leastConstrainedValue + "]");

        return leastConstrainedOption;
    }

    public void printValueSummary()
    {
        for(int i = 1; i <= GRID_LENGTH; i++)
            System.out.println("Value Total[" + i + "] = " + valueSummary[i]);

        System.out.println();
    }
}