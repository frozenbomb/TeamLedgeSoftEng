package ledge.muscleup.model.exercise;

/**
 * The interface for an exercise, which consists of a name, an intensity, a type and a recommended
 * quantity of exercise
 *
 * @author Cole Kehler
 * @version 1.0
 * @since 2017-06-02.
 */

public interface InterfaceExercise {
    /**
     * Returns the name of the exercise
     * @return the name of the exercise
     */
    String getName();

    /**
     * Returns the intensity of the exercise
     * @return the intensity of the exercise
     */
    ExerciseIntensity getIntensity();

    /**
     * Returns the type of the exercise
     * @return the type of the exercise
     */
    ExerciseType getType();

    /**
     * Compares the current InterfaceExercise to another instance of InterfaceExercise
     * @param other the instance of InterfaceExercise to compare to
     * @return a boolean representing whether the two instances were equal
     */
    boolean equals(InterfaceExercise other);

    /**
     * Returns the InterfaceExercise as a String
     * @return the InterfaceExercise as a String
     */
    String toString();
}
