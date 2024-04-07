import java.awt.*;
import java.util.*;

public class SudokuSolver {

    private final SudokuGrid grid;
    private final HashMap<Point, LinkedList<SudokuCellConstraint>> constraints;
    private final TreeSet<AbstractMap.SimpleEntry<Point, Integer>> heuristics;

    static class SudokuHeuristicCompare implements Comparator<AbstractMap.SimpleEntry<Point,Integer>> {

        public int compare(AbstractMap.SimpleEntry<Point,Integer> a, AbstractMap.SimpleEntry<Point,Integer> b)
        {
            if(Objects.equals(a.getValue(), b.getValue()))
            {
                int aTiebreaker = (a.getKey().y * 10) + (a.getKey().x);
                int bTiebreaker = (b.getKey().y * 10) + (b.getKey().x);

                return (aTiebreaker > bTiebreaker) ? -1 : 1;
            }
            // Sort the highest cost heuristics to the top
            return (a.getValue() > b.getValue()) ? -1 : 1;
        }
    }

    public SudokuSolver(SudokuGrid grid)
    {
        this.grid = grid;
        this.constraints = new HashMap<>();
        this.heuristics = new TreeSet<>(new SudokuHeuristicCompare());

        // Construct the lists to hold the constraints in the constraint map.  We need this
        // so we can look up the constraints for a given point
        for(int i = 1; i <= SudokuGrid.GRID_LENGTH; i++)
        {
            for (int j = 1; j <= SudokuGrid.GRID_LENGTH; j++)
                constraints.put(new Point(j, i), new LinkedList<>());
        }

        // Construct constraints for the row
        for(int i = 1; i <= SudokuGrid.GRID_LENGTH; i++)
        {
            SudokuCellConstraint row = new SudokuCellConstraint(grid);
            for (int j = 1; j <= SudokuGrid.GRID_LENGTH; j++)
            {
                Point aPoint = new Point(j, i);
                row.addCell(aPoint);
                constraints.get(aPoint).add(row);
            }
        }

        // Construct constraints for the columns
        for(int i = 1; i <= SudokuGrid.GRID_LENGTH; i++)
        {
            SudokuCellConstraint column = new SudokuCellConstraint(grid);
            for (int j = 1; j <= SudokuGrid.GRID_LENGTH; j++)
            {
                Point aPoint = new Point(i, j);
                column.addCell(aPoint);
                constraints.get(aPoint).add(column);
            }
        }

        // Construct constraints for the 9 subgrids
        for(int h = 0; h < 3; h++) {
            for (int k = 0; k < 3; k++) {
                SudokuCellConstraint column = new SudokuCellConstraint(grid);
                for (int i = 1 + (3 * k); i <= 3 + (3 * k); i++) {
                    for (int j = 1 + (3 * h); j <= 3 + (3 * h); j++) {
                        Point aPoint = new Point(i, j);
                        column.addCell(aPoint);
                        constraints.get(aPoint).add(column);
                    }
                }
            }
        }

        // Initialize the heuristics table
        for(int i = 1; i <= SudokuGrid.GRID_LENGTH; i++)
            for (int j = 1; j <= SudokuGrid.GRID_LENGTH; j++)
                updateCellHeuristic(new Point(i, j));
    }

    public void solve()
    {
        System.out.println("Available for [4,9] = " + getAvailableValues(new Point(4,9)));
        System.out.println("Available for [8,7] = " + getAvailableValues(new Point(8,7)));
        System.out.println("Available for [6,6] = " + getAvailableValues(new Point(6,6)));
        System.out.println("Available for [7,8] = " + getAvailableValues(new Point(7,8)));
    }

    private HashSet<Integer> getUsedValues(Point aPoint)
    {
        HashSet<Integer> heuristicSet = new HashSet<>();

        Iterator<SudokuCellConstraint> iter = constraints.get(aPoint).iterator();
        while(iter.hasNext())
        {
            SudokuCellConstraint constraint = iter.next();
            heuristicSet.addAll(constraint.getUsedValues());
        }

        return heuristicSet;
    }

    private void updateCellHeuristic(Point aPoint)
    {
        HashSet<Integer> heuristicSet = getUsedValues(aPoint);
        heuristics.add(new AbstractMap.SimpleEntry<>(aPoint, heuristicSet.size()));
    }

    private HashSet<Integer> getAvailableValues(Point aPoint)
    {
        HashSet<Integer> out = new HashSet<>(Arrays.asList(SudokuGrid.SOLUTION_SET));
        out.removeAll(getUsedValues(aPoint));

        return out;
    }

    public void printHeuristics()
    {
        Iterator<AbstractMap.SimpleEntry<Point,Integer>> iter = heuristics.iterator();
        while(iter.hasNext())
            System.out.println(iter.next());
    }
}