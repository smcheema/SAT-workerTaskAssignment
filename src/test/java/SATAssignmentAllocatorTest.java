import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class SATAssignmentAllocatorTest {

    private static SATAssignmentAllocator SATAssignmentAllocator;

    @BeforeAll
    static void setup() {
        SATAssignmentAllocator = new SATAssignmentAllocator();
    }

    @ParameterizedTest
    @MethodSource("workerAssignmentStreams")
    public void aTest(int[][] workerTaskTable, int[] assignments) {
        int[] assignedTasks = SATAssignmentAllocator.assignMyTasks(workerTaskTable);
        Assertions.assertArrayEquals(assignedTasks, assignments);
    }

    private static Stream<Arguments> workerAssignmentStreams() {
        return Stream.of(
                Arguments.arguments(
                        new int[][]  {
                                        {90, 80, 75, 70},
                                        {35, 85, 55, 65},
                                        {125, 95, 90, 95},
                                        {45, 110, 95, 115},
                                        {50, 100, 90, 100},
                                     },

                        new int[]
                                        {3, 2, 1, 0}
                ), Arguments.arguments(
                        new int[][] {
                                        {45, 40, 80, 120},
                                        {10, 35, 65, 60},
                                        {30, 70, 30, 40},
                                        {10, 10, 15, 5},
                                        {30, 5, 60, 35},
                                        {5, 85, 90, 100},
                                        {70, 30, 80, 15},
                                        {150, 5, 500, 10},
                                     },
                        new int[]
                                        {5, 4, 3, 7}
                ), Arguments.arguments(
                        new int[][] {
                                        {45, 40, 50, 55, 60, 40, 35, 40},
                                        {25, 20, 60, 70, 80, 50, 30, 80},
                                        {90, 100, 100, 50, 40, 30, 35, 45},
                                        {60, 65, 25, 30, 35, 45, 50, 70},
                                        {35, 35, 30, 30, 40, 40, 30, 30},
                                        {60, 60, 5, 90, 100, 80, 75, 80},
                                        {45, 45, 45, 45, 45, 45, 45, 45},
                                        {100, 100, 100, 100, 100, 100, 100, 100},
                                        {35, 20, 55, 45, 35, 65, 20, 150},
                                        {20, 80, 95, 20, 45, 65, 30, 40},
                                        {70, 40, 60, 75, 15, 45, 35, 200},
                        },
                        new int[]
                                        {9, 1, 5, 3, 10, 2, 8, 4}
                )
        );
    }
}

