package ledge.muscleup.unit.model.workout;

import junit.framework.TestCase;

import org.joda.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

import java.util.Enumeration;

import ledge.muscleup.model.exercise.Exercise;
import ledge.muscleup.model.exercise.WorkoutExerciseSets;
import ledge.muscleup.model.exercise.enums.ExerciseIntensity;
import ledge.muscleup.model.exercise.ExerciseSets;
import ledge.muscleup.model.exercise.enums.ExerciseType;
import ledge.muscleup.model.exercise.WorkoutSessionExercise;
import ledge.muscleup.model.workout.Workout;
import ledge.muscleup.model.workout.WorkoutSession;

/**
 * WorkoutSessionTest.java used to test WorkoutSession.java
 *
 * @author Matthew Smidt
 * @version 1.0
 * @since 2017-06-07
 */
public class WorkoutSessionTest extends TestCase {
    private WorkoutSession workoutSession1, workoutSession2, workoutSession3;
    private Workout workout1, workout2, workout3;
    private WorkoutExerciseSets[] exerciseList1, exerciseList2, exerciseList3;
    private final int xpHighIntensity = (ExerciseIntensity.HIGH.ordinal() + 1) * 15;
    private final int xpMediumIntensity = (ExerciseIntensity.MEDIUM.ordinal() + 1) * 15;
    private final int xpLowIntensity = (ExerciseIntensity.LOW.ordinal() + 1) * 15;

    /**
     * Initializes several instances of WorkoutSession to setup testing
     */
    @Before
    public void setUp() {
        exerciseList1 = new WorkoutExerciseSets[]{
                new WorkoutExerciseSets(new Exercise("Crunches", ExerciseIntensity.HIGH, ExerciseType.FULL_BODY), xpHighIntensity,new ExerciseSets(3, 20)),
                new WorkoutExerciseSets(new Exercise("Russian Twists", ExerciseIntensity.HIGH, ExerciseType.FULL_BODY), xpHighIntensity,new ExerciseSets(3, 20)),
                new WorkoutExerciseSets(new Exercise("Sit-ups", ExerciseIntensity.HIGH, ExerciseType.FULL_BODY), xpHighIntensity, new ExerciseSets(3, 20))
        };
        workout1 = new Workout("Get Your 6 Pack Abs", exerciseList1);

        workoutSession1 = new WorkoutSession(workout1, LocalDate.now(), false);

        exerciseList2 = new WorkoutExerciseSets[]{
                new WorkoutExerciseSets(new Exercise("Crunches", ExerciseIntensity.HIGH, ExerciseType.FULL_BODY), xpHighIntensity,new ExerciseSets(3, 20)),
                new WorkoutExerciseSets(new Exercise("Russian Twists", ExerciseIntensity.HIGH, ExerciseType.FULL_BODY), xpHighIntensity,new ExerciseSets(3, 20))
        };
        workout2 = new Workout("Get Your 6 Pack Abs", exerciseList2);

        workoutSession2 = new WorkoutSession(workout2, LocalDate.now(), true);

        exerciseList3 = new WorkoutExerciseSets[]{
                new WorkoutExerciseSets(new Exercise("Crunches", ExerciseIntensity.HIGH, ExerciseType.FULL_BODY), xpHighIntensity,new ExerciseSets(3, 20)),
                new WorkoutExerciseSets(new Exercise("Russian Twists", ExerciseIntensity.HIGH, ExerciseType.FULL_BODY), xpHighIntensity,new ExerciseSets(3, 20)),
                new WorkoutExerciseSets(new Exercise("Sit-ups", ExerciseIntensity.HIGH, ExerciseType.FULL_BODY), xpHighIntensity,new ExerciseSets(3, 20))
        };
        workout3 = new Workout("Get Your 6 Pack Abs", exerciseList3);

        workoutSession3 = new WorkoutSession(workout3, LocalDate.now(), false);
    }

    /**
     * Tests various functionality of the WorkoutSession class, including proper return values of
     * getter methods, setter/toggle methods, toggleCompleted(), and equals checking
     */
    @Test
    public void testWorkoutSession() {
        System.out.println("\nStarting testWorkoutSession");

        assertFalse(workoutSession1.equals(workoutSession2));
        assertFalse(workoutSession2.equals(workoutSession1));
        assertFalse(workoutSession2.equals(workoutSession3));
        assertFalse(workoutSession3.equals(workoutSession2));

        assertTrue(workoutSession1.equals(workoutSession3));
        assertTrue(workoutSession3.equals(workoutSession1));

        assertEquals("Get Your 6 Pack Abs", workoutSession1.getName());

        assertEquals(LocalDate.now(), workoutSession1.getDate());
        assertEquals(LocalDate.now(), workoutSession2.getDate());

        workoutSession1.setDate(LocalDate.now().plusDays(1));
        assertEquals(LocalDate.now().plusDays(1), workoutSession1.getDate());
        workoutSession2.setDate(LocalDate.now().minusDays(1));
        assertEquals(LocalDate.now().minusDays(1), workoutSession2.getDate());

        assertFalse(workoutSession1.isComplete());
        assertTrue(workoutSession2.isComplete());

        workoutSession1.toggleCompleted();
        workoutSession2.toggleCompleted();

        assertTrue(workoutSession1.isComplete());
        assertFalse(workoutSession2.isComplete());

        assertEquals(3, workoutSession1.numExercises());
        assertEquals(2, workoutSession2.numExercises());

        assertNotNull(workoutSession1.getExerciseEnumeration());
        assertTrue(workoutSession1.getExerciseEnumeration() instanceof Enumeration);

        assertNotNull(workoutSession2.getExerciseEnumeration());
        assertTrue(workoutSession2.getExerciseEnumeration() instanceof Enumeration);

        assertNotNull(workoutSession3.getExerciseEnumeration());
        assertTrue(workoutSession3.getExerciseEnumeration() instanceof Enumeration);

        assertFalse(workoutSession1.completeExercise(
                new WorkoutSessionExercise(new WorkoutExerciseSets(new Exercise("Crunches", ExerciseIntensity.HIGH,
                        ExerciseType.FULL_BODY), xpHighIntensity, new ExerciseSets(3, 20)), true)));
        assertFalse(workoutSession1.completeExercise(
                new WorkoutSessionExercise(new WorkoutExerciseSets(new Exercise("Russian Twists", ExerciseIntensity.HIGH,
                        ExerciseType.FULL_BODY), xpHighIntensity, new ExerciseSets(3, 20)), true)));
        assertFalse(workoutSession1.completeExercise(
                new WorkoutSessionExercise(new WorkoutExerciseSets(new Exercise("Sit-ups", ExerciseIntensity.HIGH,
                        ExerciseType.FULL_BODY), xpHighIntensity, new ExerciseSets(3, 20)), true)));


        assertNotNull(workoutSession1);

        assertNotNull(workoutSession1.getWorkoutSessionExercises());
        assertNotNull(workoutSession2.getWorkoutSessionExercises());
        assertNotNull(workoutSession3.getWorkoutSessionExercises());

        assertEquals(3 * xpHighIntensity, workoutSession1.getExperienceValue());
        assertEquals(2 * xpHighIntensity, workoutSession2.getExperienceValue());
        assertEquals(3 * xpHighIntensity, workoutSession3.getExperienceValue());

        System.out.println("Finishing testWorkoutSession");
    }
}
