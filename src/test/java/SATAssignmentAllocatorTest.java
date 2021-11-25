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
    public void aTest(int[][] workerTaskTable, int[][] teams, int[] assignments) {
        int[] assignedTasks = SATAssignmentAllocator.assignMyTasks(workerTaskTable, teams);
        Assertions.assertArrayEquals(assignedTasks, assignments);
    }

    private static Stream<Arguments> workerAssignmentStreams() {
        return Stream.of(
                Arguments.arguments(
                        new int[][] {
                                    {90, 76, 75, 70},
                                    {35, 85, 55, 65},
                                    {125, 95, 90, 105},
                                    {45, 110, 95, 115},
                                    {60, 105, 80, 75},
                                    {45, 65, 110, 95}
                        },

                        new int[][] {
                                {1, 3, 5},
                                {0, 2, 4},
                        },

                        new int[] {
                                4, 5, 1, 0
                        }
                )
        );
    }
}

