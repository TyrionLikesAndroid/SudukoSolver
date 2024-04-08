import java.awt.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class SudokuCellConstraint {

    private final SudokuGrid grid;
    private final HashSet<Point> cells;

    public SudokuCellConstraint(SudokuGrid grid)
    {
        this.grid = grid;
        cells = new HashSet<>();
    }

    public void addCell(Point aPoint)
    {
        cells.add(aPoint);
    }

    public LinkedList<Integer> getUsedValues()
    {
        LinkedList<Integer> out = new LinkedList<>();

        Iterator<Point> iter = cells.iterator();
        while(iter.hasNext())
        {
            int cellValue = grid.getGridValue(iter.next());
            if(cellValue != SudokuGrid.EMPTY_CELL)
                out.add(cellValue);
        }

        return out;
    }
}