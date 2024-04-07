import java.awt.*;
import java.util.*;

public class SudokuSolver {

    private final SudokuGrid grid;
    private final HashMap<Point, LinkedList<SudokuCellConstraint>> constraints;
    private final TreeSet<AbstractMap.SimpleEntry<Point, Integer>> heuristics;
    private final Stack<SudokuMove> solution;

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
        this.solution = new Stack<>();

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
        while(true)
        {
            // Evaluate our most constrained cells and confirm we don't have a broken puzzle. By
            // checking this, we are confirming that every open cell is not full constrained and
            // all have available moves
            if(! confirmHealthyHeuristics())
            {
                // Go back up the tree until we get healthy
                System.out.println("ERROR - Constraints are broken, go code this");
                return;
            }

            // We are healthy, so grab the most constrained cell from the list and continue solving
            AbstractMap.SimpleEntry<Point, Integer> constrainedCell = heuristics.pollFirst();
            if(constrainedCell == null)
            {
                System.out.println("Sudoku solved in [" + solution.size() + "] moves");
                grid.printGrid();
                return;
            }

            System.out.println("Most constrained cell = " + constrainedCell);

            // Create a move for this constrained cell based on the point and available values
            Point constrainedPoint = constrainedCell.getKey();
            HashSet<Integer> available = getAvailableValues(constrainedPoint);
            SudokuMove nextMove = new SudokuMove(constrainedPoint);
            nextMove.availableValues.addAll(available);

            // Assign an available value from our move set and record the move
            grid.setGridValue(constrainedCell.getKey(), nextMove.popAvailableValue());
            solution.push(nextMove);
            System.out.println("Next move = " + constrainedPoint + " value = " + grid.getGridValue(constrainedPoint));

            // Update all of the dirty heuristics in the subnode, column, row for the point we just changed
            // SMJ NOTE - GO BACK AND OPTIMIZE THIS, THIS IS BRUTE FORCE AND SORT OF TERRIBLE
            heuristics.clear();
            for(int i = 1; i <= SudokuGrid.GRID_LENGTH; i++)
                for (int j = 1; j <= SudokuGrid.GRID_LENGTH; j++)
                    updateCellHeuristic(new Point(i, j));
        }
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
        // Only add empty cells to the heuristic.  Once they have been assigned, we don't
        // need to evaluate them any more unless we revert some moves
        if(grid.getGridValue(aPoint) == SudokuGrid.EMPTY_CELL)
        {
            HashSet<Integer> heuristicSet = getUsedValues(aPoint);
            heuristics.add(new AbstractMap.SimpleEntry<>(aPoint, heuristicSet.size()));
        }
    }

    private HashSet<Integer> getAvailableValues(Point aPoint)
    {
        HashSet<Integer> out = new HashSet<>(Arrays.asList(SudokuGrid.SOLUTION_SET));
        out.removeAll(getUsedValues(aPoint));

        return out;
    }

    private boolean confirmHealthyHeuristics()
    {
        Iterator<AbstractMap.SimpleEntry<Point,Integer>> iter = heuristics.iterator();
        while(iter.hasNext())
        {
            AbstractMap.SimpleEntry<Point, Integer> hNode = iter.next();
            if((hNode.getValue() == SudokuGrid.GRID_LENGTH) && (grid.getGridValue(hNode.getKey()) == SudokuGrid.EMPTY_CELL))
            {
                // We have a fully constrained cell that is empty, so the model is broken
                return false;
            }
            else if((hNode.getValue() < SudokuGrid.GRID_LENGTH))
                return true;    // The heuristics are sorted, so no reason to look through the whole list
        }
        return true;
    }

    private void updateConstraintHeuristics(Point aPoint)
    {
        HashSet<Point> dirtyCells = new HashSet<>();

        Iterator<SudokuCellConstraint> iter = constraints.get(aPoint).iterator();
        while(iter.hasNext())
            dirtyCells.addAll(iter.next().getConstraintCells());

        Iterator<Point> dIter = dirtyCells.iterator();
        while(dIter.hasNext())
            updateCellHeuristic(dIter.next());
    }

    public void printHeuristics()
    {
        Iterator<AbstractMap.SimpleEntry<Point,Integer>> iter = heuristics.iterator();
        while(iter.hasNext())
            System.out.println(iter.next());
    }
}