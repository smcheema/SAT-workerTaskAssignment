import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;

public class SATAssignmentAllocator {

    public int[] assignMyTasks(int[][] workerTaskTable) {
        Loader.loadNativeLibraries();
        final CpModel model = new CpModel();
        // workerTaskTable[r][c] --> rth worker and cth task
        final int workerCount = workerTaskTable.length;
        final int taskCount = workerTaskTable[0].length;
        // assignments[i][j] : if 1 --> ith worker  assigned jth task
        //                     if 0 --> ith worker !assigned jth task
        // this is what we pass into our solver and use to read back values.
        IntVar[][] assignments = new IntVar[workerCount][taskCount];
        for (int worker = 0; worker < workerCount; worker++) {
            for (int task = 0; task < taskCount; task++) {
                // make each assignment binary to distinguish between Y/N
                // in terms of assignment for said worker to a task.
                assignments[worker][task] = model.newIntVar(0, 1,
                        String.format("Flag for assignment of task %s to worker %s.", task, worker));
            }
        }

        // constrain each worker to be assigned either 1, or no task
        // we do this by specifying that the sum of worker assignments has to be <= 1
        // i.e, for some worker w, at-most only one column inside assignments[worker] can ever be 1
        for (int worker = 0; worker < workerCount; worker++) {
            model.addLessOrEqual(LinearExpr.sum(assignments[worker]), 1);
        }

        // constrain each task to be assigned 1 worker
        // we do this by specifying that the sum of task assignments has to be == 1
        // i.e, for some task t, at-least and at-most one row specifying worker assignments can ever be 1
        for (int task = 0; task < taskCount; task++) {
            IntVar[] columnForTask = new IntVar[workerCount];
            for (int worker = 0; worker < workerCount; worker++) {
                columnForTask[worker] = assignments[worker][task];
            }
            model.addEquality(LinearExpr.sum(columnForTask), 1);
        }

        // flatten the 2-d matrices, adhering to or-tools' input semantics.
        final IntVar[] flatAssignments = new IntVar[taskCount * workerCount];
        final int[] flatCosts = new int[taskCount * workerCount];
        int writeIndex = 0;
        for (int worker = 0; worker < workerCount; worker++) {
            for (int task = 0; task < taskCount; task++) {
                flatAssignments[writeIndex] = assignments[worker][task];
                flatCosts[writeIndex++] = workerTaskTable[worker][task];
            }
        }

        // scalProd is an element by element operation between an array of IntVars
        // and an array of co-efficients, to generate a linearExpression capturing the above.
        // i.e scalProd([V1, V2, V2], [1, 2, 3])
        // results in : 1 * V1 + 2 * V2 + 3 * V3
        // so here we ask our model :
        // we've defined intVars in flatAssignments, you have the row-wise (worker-specific) and column-wise (task-specific)
        // we now want to minimize the expression below. Find me the assignments (in terms of Y/N || 0/1) that minimize my cost (linearExpr).
        model.minimize(LinearExpr.scalProd(flatAssignments, flatCosts));

        CpSolver solver = new CpSolver();
        CpSolverStatus status = solver.solve(model);
        if (status == CpSolverStatus.FEASIBLE || status == CpSolverStatus.OPTIMAL) {
            // value w at index t represents task t was assigned to worker w
            int[] retVal = new int[taskCount];
            // read solver matrix worker-by-worker (i.e process a worker's entire list of tasks before moving onto the next worker).
            for (int worker = 0; worker < workerCount; worker++) {
                for (int task = 0; task < taskCount; task++) {
                    int tempHold = (int) solver.value(assignments[worker][task]);
                    if (tempHold == 1) {
                        retVal[task] = worker;
                    }
                }
            }
            return retVal;
        } else {
            return new int[] {};
        }
    }
}
