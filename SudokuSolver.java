import java.awt.*;
import java.util.*;

public class SudokuSolver {

    private final SudokuGrid grid;
    private final HashMap<Point, LinkedList<SudokuCellConstraint>> constraints;
    private final TreeSet<AbstractMap.SimpleEntry<Point, Integer>> heuristics;
    private final Stack<SudokuMove> solution;
    private int forwardMoves = 0;
    private int backwardMoves = 0;
    private int lateralMoves = 0;

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
        resetHeuristics();
    }

    public void solve()
    {
        while(true)
        {
            // Evaluate our most constrained cells and confirm we don't have a broken puzzle. By
            // checking this, we are confirming that every open cell is not fully constrained and
            // all have available moves
            if(! confirmHealthyHeuristics())
            {
                // Go back up the tree until we get healthy
                //System.out.println("Constraints are broken, recursing the stack");

                // Recurse until we can find a previous move with another option
                while(solution.peek().peekAvailableValue() == null)
                {
                    // There are no other choices for the previous move.  We need to pop it
                    // off the list and clear the puzzle cell while we look upstream for
                    // another option
                    SudokuMove dropMove = solution.pop();
                    grid.setGridValue(dropMove.getPoint(), SudokuGrid.EMPTY_CELL);
                    backwardMoves++;
                    //System.out.println("Clearing cell " + dropMove.getPoint());
                }

                // If we got here, we finally peeked a move that has some other value options.  Take another
                // choice for this cell and hope it goes better
                SudokuMove modifyMove = solution.peek();
                int nextValueChoice = modifyMove.popAvailableValue();
                grid.setGridValue(modifyMove.getPoint(), nextValueChoice);
                lateralMoves++;
                //System.out.println("Modifying cell " + modifyMove.getPoint() + " to value "+ nextValueChoice);

                resetHeuristics();
                continue;
            }

            // We are healthy, so grab the most constrained cell from the list and continue solving
            AbstractMap.SimpleEntry<Point, Integer> constrainedCell = heuristics.pollFirst();
            if(constrainedCell == null)
            {
                System.out.println("Sudoku solution moves: forward[" + forwardMoves + "] backward[" + backwardMoves +
                        "] lateral[" + lateralMoves + "]");
                grid.printGrid();
                return;
            }

            //System.out.println("Most constrained cell = " + constrainedCell);

            // Create a move for this constrained cell based on the point and available values
            Point constrainedPoint = constrainedCell.getKey();
            HashSet<Integer> available = getAvailableValues(constrainedPoint);
            SudokuMove nextMove = new SudokuMove(constrainedPoint);
            nextMove.availableValues.addAll(available);

            // Assign an available value from our move set and record the move
            grid.setGridValue(constrainedCell.getKey(), nextMove.popAvailableValue());
            solution.push(nextMove);
            forwardMoves++;
            //System.out.println("Next move = " + constrainedPoint + " value = " + grid.getGridValue(constrainedPoint));

            // Update all the heuristics because our latest move made many cells invalid
            resetHeuristics();
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

    private void resetHeuristics()
    {
        heuristics.clear();
        for(int i = 1; i <= SudokuGrid.GRID_LENGTH; i++)
            for (int j = 1; j <= SudokuGrid.GRID_LENGTH; j++)
                updateCellHeuristic(new Point(i, j));
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

    public void printHeuristics()
    {
        Iterator<AbstractMap.SimpleEntry<Point,Integer>> iter = heuristics.iterator();
        while(iter.hasNext())
            System.out.println(iter.next());
    }
}