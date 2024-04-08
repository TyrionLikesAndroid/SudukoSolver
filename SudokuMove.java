import java.awt.*;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class SudokuMove {

    public final Point point;
    public final LinkedList<Integer> availableValues;

    public SudokuMove(Point point) {
        this.point = point;
        this.availableValues = new LinkedList<>();
    }

    public Point getPoint() { return point; }

    public Integer peekAvailableValue()
    {
        return availableValues.peek();
    }

    public int popAvailableValue()
    {
        int out = SudokuGrid.EMPTY_CELL;
        try
        {
            out = availableValues.remove();
        }
        catch (NoSuchElementException e)
        {
            System.out.println("List is empty, continue with EMPTY_CELL");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return out;
    }
}