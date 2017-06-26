package ledge.muscleup.business;

import org.joda.time.LocalDate;
import java.util.List;

import ledge.muscleup.application.Services;
import ledge.muscleup.model.exercise.WorkoutSessionExercise;
import ledge.muscleup.model.workout.WorkoutSession;
import ledge.muscleup.persistence.DataAccessStub;

/**
 * This class contains methods for retrieving, adding, and removing workout sessions from the
 * database, by calling the methods defined in the InterfaceDataAccess interface.
 *
 * @author Ryan Koop
 * @version 1.0
 * @since 2017-06-07
 */
public class AccessWorkoutSessions implements InterfaceAccessWorkoutSessions {
    private DataAccessStub dataAccess;

    /**
     * Constructor for AccessWorkoutSessions, which initializes the dataAccess variable to the stub database
     */
    public AccessWorkoutSessions() {
        dataAccess = (DataAccessStub) Services.getDataAccess();
    }

    /**
     * This method gets a workout session from the database with the given date
     * @param dateOfSession the date of the workout session
     * @return a workout session from the database scheduled on the given date
     */
    public WorkoutSession getWorkoutSession(LocalDate dateOfSession) {
        return dataAccess.getWorkoutSession(dateOfSession);
    }

    /**
     * This method gets a list of workout sessions in the database
     * @return a list of the workout sessions stored in the database
     */
    public List<WorkoutSession> getWorkoutSessionsList() {
        return dataAccess.getWorkoutSessionsList();
    }

    /**
     * A method that returns a list of workout sessions scheduled in a date range
     * @param startDate the first date of the date range
     * @param endDate the last date of the date range
     * @return a list of all workout sessions scheduled between startDate and endDate, inclusive
     */
    public List<WorkoutSession> getSessionsInDateRange(LocalDate startDate,
                                                                LocalDate endDate) {
        return dataAccess.getSessionsInDateRange(startDate, endDate);
    }

    /**
     * Adds a new workout session to the database
     * @param workoutSession the workout session to be added to the database
     */
    public void insertWorkoutSession(WorkoutSession workoutSession) {
        dataAccess.insertWorkoutSession(workoutSession);
    }

    /**
     * Removes a workout session from the database, if it exists
     * @param workoutSession the workout session to be removed
     */
    public void removeWorkoutSession(WorkoutSession workoutSession) {
        dataAccess.removeWorkoutSession(workoutSession);
    }

    /**
     * Sets the scheduled date of a workout
     *
     * @param workoutSession the workout to change the date for
     * @param newDate        the new date of the workout
     * @throws IllegalArgumentException if passed a {@code null} parameter
     */
    public void setDate(WorkoutSession workoutSession, LocalDate newDate) throws IllegalArgumentException {
        workoutSession.setDate(newDate);
        dataAccess.updateWorkoutDate(workoutSession, newDate);
    }

    /**
     * Toggles the completed state of a workout
     *
     * @param workoutSession the workout to change the state of
     */
    public void toggleCompleted(WorkoutSession workoutSession) {
        workoutSession.toggleCompleted();
        dataAccess.toggleWorkoutComplete(workoutSession);
    }

    /**
     * Log an exercise in a workout as complete
     *
     * @param workoutSession the workout which contains the exercise
     * @param exercise       the exercise to complete
     * @return a boolean representing whether the exercise was marked as completed or not
     * @throws IllegalArgumentException if passed a {@code null} parameter
     */
    public boolean completeExercise(WorkoutSession workoutSession, WorkoutSessionExercise exercise) throws IllegalArgumentException {
        return workoutSession.completeExercise(exercise) && dataAccess.toggleExerciseComplete(workoutSession, exercise);
    }
}
