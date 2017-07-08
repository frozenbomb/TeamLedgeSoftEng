package ledge.muscleup.model;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ledge.muscleup.business.InterfaceAccessExercises;
import ledge.muscleup.business.InterfaceAccessWorkoutSessions;
import ledge.muscleup.business.InterfaceAccessWorkouts;
import ledge.muscleup.model.exercise.WorkoutExerciseDistance;
import ledge.muscleup.model.exercise.WorkoutExerciseDuration;
import ledge.muscleup.model.exercise.WorkoutExerciseSets;
import ledge.muscleup.model.exercise.WorkoutExerciseSetsAndWeight;
import ledge.muscleup.model.exercise.enums.*;
import ledge.muscleup.model.exercise.Exercise;
import ledge.muscleup.model.exercise.ExerciseDistance;
import ledge.muscleup.model.exercise.ExerciseDuration;
import ledge.muscleup.model.exercise.ExerciseSets;
import ledge.muscleup.model.exercise.ExerciseSetsAndWeight;
import ledge.muscleup.model.exercise.InterfaceExerciseQuantity;

import ledge.muscleup.model.exercise.WorkoutExercise;
import ledge.muscleup.model.exercise.WorkoutSessionExercise;
import ledge.muscleup.model.schedule.ScheduleWeek;
import ledge.muscleup.model.workout.Workout;
import ledge.muscleup.model.workout.WorkoutSession;
import ledge.muscleup.persistence.InterfaceDataAccess;
import ledge.muscleup.persistence.InterfaceExerciseDataAccess;
import ledge.muscleup.persistence.InterfaceWorkoutDataAccess;
import ledge.muscleup.persistence.InterfaceWorkoutSessionDataAccess;

/**
 * Tests for the ScheduleWeek
 *
 * @author Cole Kehler
 * @version 1.0
 * @since 2017-06-06
 */

public class ScheduleWeekTest {
    private ScheduleWeek scheduleWeek;
    InterfaceAccessWorkoutSessions dataAccess;

    /**
     * Constructor for the ScheduleWeekTest
     */
    public ScheduleWeekTest() { super(); }

    /**
     * Initializes the ScheduleWeek to be used in the test
     */
    @Before
    public void testInit(){
        dataAccess = new TemplateAccessWorkoutSessions();
        scheduleWeek = new ScheduleWeek(dataAccess.getCurrentWeekSessions());
    }

    /**
     * Tests that getting a day of the current week works properly
     */
    @Test
    public void getWeekdayTest(){
        Assert.assertTrue("Returned unexpected weekday value",
                scheduleWeek.getWeekday(DateTimeConstants.MONDAY).isEqual(new LocalDate().withDayOfWeek(DateTimeConstants.MONDAY)));
        Assert.assertTrue("Returned unexpected weekday value",
                scheduleWeek.getWeekday(DateTimeConstants.SUNDAY).isEqual(new LocalDate().withDayOfWeek(DateTimeConstants.SUNDAY)));
    }

    /**
     * Tests that checking if a workout exists on a day and getting a workout on a day of the
     * current week works properly
     */
    @Test
    public void scheduledWorkoutsTest(){
        Assert.assertFalse("Returned that day is empty when it isn't",
                scheduleWeek.isDayEmpty(DateTimeConstants.TUESDAY));
        Assert.assertTrue("Returned that day isn't empty when it is",
                scheduleWeek.isDayEmpty(DateTimeConstants.THURSDAY));

        Assert.assertEquals("Returned a workout for a day where there shouldn't be one",
                scheduleWeek.getScheduledWorkout(DateTimeConstants.THURSDAY).getName(), null);
        Assert.assertEquals("Didn't return the workout scheduled for a day",
                scheduleWeek.getScheduledWorkout(DateTimeConstants.TUESDAY).getName(),
                "Never Skip Leg Day");
    }

    /**
     * Tests that changing the current week to the next and previous weeks works properly
     */
    @Test
    public void changeWeekTest(){
        LocalDate currWeekStart = scheduleWeek.getWeekday(DateTimeConstants.MONDAY);
        List<WorkoutSession> workoutList;

        dataAccess.setToNextWeek(scheduleWeek);

        Assert.assertEquals("Improperly incremented week",
                scheduleWeek.getWeekday(DateTimeConstants.MONDAY), currWeekStart.plusWeeks(1));
        Assert.assertEquals("Returned unexpected workout",
                scheduleWeek.getScheduledWorkout(DateTimeConstants.TUESDAY).getName(),
                "Marathon Training Starts Here");

        dataAccess.setToCurrentWeek(scheduleWeek);
        Assert.assertEquals("Improperly Set to Current week",
                scheduleWeek.getWeekday(DateTimeConstants.MONDAY), LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY));
        Assert.assertEquals("Returned unexpected workout",
                scheduleWeek.getScheduledWorkout(DateTimeConstants.TUESDAY).getName(), "Never Skip Leg Day");
        Assert.assertEquals("Returned unexpected workout",
                scheduleWeek.getScheduledWorkout(DateTimeConstants.WEDNESDAY).getName(), "Work that Core, Get that Score!");
        Assert.assertEquals("Returned unexpected workout",
                scheduleWeek.getScheduledWorkout(DateTimeConstants.FRIDAY).getName(), "Never Skip Leg Day");


        dataAccess.setToLastWeek(scheduleWeek);
        Assert.assertEquals("Improperly decremented week",
                scheduleWeek.getWeekday(DateTimeConstants.MONDAY), LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY).minusWeeks(1));
        Assert.assertEquals("Returned unexpected workout",
                scheduleWeek.getScheduledWorkout(DateTimeConstants.THURSDAY).getName(),
                "Welcome to the Gun Show");


        dataAccess.setToLastWeek(scheduleWeek);

        Assert.assertEquals("Improperly decremented week",
                scheduleWeek.getWeekday(DateTimeConstants.MONDAY), LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY).minusWeeks(2));
        Assert.assertEquals("Returned unexpected workout",
                scheduleWeek.getScheduledWorkout(DateTimeConstants.MONDAY).getName(), null);
        Assert.assertEquals("Returned unexpected workout",
                scheduleWeek.getScheduledWorkout(DateTimeConstants.SUNDAY).getName(), null);
    }

    /**
     * Tests that a workout can be added to and removed from the week
     */
    @Test
    public void addRemoveWorkoutTest(){
        Assert.assertFalse("Removed a workout when there isn't one",
                scheduleWeek.removeWorkoutSession(DateTimeConstants.THURSDAY));
        Assert.assertTrue("Didn't remove workout",
                scheduleWeek.removeWorkoutSession(DateTimeConstants.WEDNESDAY));

        Assert.assertEquals("Improperly deleted a workout",
                scheduleWeek.getScheduledWorkout(DateTimeConstants.WEDNESDAY), null);
    }
}

/**
 * A template WorkoutSession accessor that creates a template database stub for use in testing
 */
class TemplateAccessWorkoutSessions implements InterfaceAccessWorkoutSessions {
    TemplateDataAccessStub dataAccess;

    /**
     * The default constructor for the TemplateAccessWorkoutSessions
     */
    TemplateAccessWorkoutSessions() {
        dataAccess = new TemplateDataAccessStub("testDB");
        dataAccess.open("testDB");
    }

    /**
     * This method gets a workout session from the database with the given date
     *
     * @param dateOfSession the date of the workout session
     * @return a workout session from the database scheduled on the given date
     */
    @Override
    public WorkoutSession getWorkoutSession(LocalDate dateOfSession) {
        return dataAccess.getWorkoutSession(dateOfSession);
    }

    /**
     * A method that returns a list of workout sessions scheduled in a date range
     *
     * @param startDate the first date of the date range
     * @param endDate   the last date of the date range
     * @return a list of all workout sessions scheduled between startDate and endDate, inclusive
     */
    @Override
    public List<WorkoutSession> getSessionsInDateRange(LocalDate startDate, LocalDate endDate) {
        return dataAccess.getSessionsInDateRange(startDate, endDate);
    }

    /**
     * A method that returns a list of workout sessions scheduled in the current week
     * @return a list of all workout sessions scheduled in the current week
     */
    @Override
    public List<WorkoutSession> getCurrentWeekSessions() {
        LocalDate firstOfThisWeek = new LocalDate().withDayOfWeek(DateTimeConstants.MONDAY);
        return dataAccess.getSessionsInDateRange(firstOfThisWeek, firstOfThisWeek.plusDays(DateTimeConstants.DAYS_PER_WEEK - 1));
    }

    /**
     * Adds a new workout session to the database
     *
     * @param workoutSession the workout session to be added to the database
     */
    @Override
    public void insertWorkoutSession(WorkoutSession workoutSession) {
        dataAccess.insertWorkoutSession(workoutSession);
    }

    /**
     * Removes a workout session from the database, if it exists
     *
     * @param workoutSession the workout session to be removed
     */
    @Override
    public void removeWorkoutSession(WorkoutSession workoutSession) {
        dataAccess.removeWorkoutSession(workoutSession);
    }

    /**
     * Toggles the completed state of a workout
     *
     * @param workoutSession the workout to change the state of
     */
    @Override
    public void toggleWorkoutCompleted(WorkoutSession workoutSession) {
        workoutSession.toggleCompleted();
        dataAccess.toggleWorkoutComplete(workoutSession);
    }

    /**
     * Creates a new ScheduleWeek based on the given date
     *
     * @param dayInWeek a day in the week to created a ScheduleWeek for
     * @return a ScheduleWeek, which contains all WorkoutSessions for the given week
     */
    @Override
    public ScheduleWeek newScheduledWeek(LocalDate dayInWeek) {
        LocalDate firstDayOfWeek = dayInWeek.withDayOfWeek(DateTimeConstants.MONDAY);
        return new ScheduleWeek(getSessionsInDateRange(firstDayOfWeek, firstDayOfWeek.plusDays(DateTimeConstants.DAYS_PER_WEEK - 1)));
    }

    /**
     * Sets the manager to contain the scheduled workouts for the previous week
     *
     * @param scheduleWeek the week to change
     */
    @Override
    public void setToLastWeek(ScheduleWeek scheduleWeek) {
        LocalDate firstDayOfWeek;
        List<WorkoutSession> weekWorkouts;

        firstDayOfWeek = scheduleWeek.getWeekday(DateTimeConstants.MONDAY).minusWeeks(1);
        weekWorkouts = getSessionsInDateRange(firstDayOfWeek, firstDayOfWeek.plusDays(DateTimeConstants.DAYS_PER_WEEK - 1));
        scheduleWeek.lastWeek(weekWorkouts);
    }

    /**
     * Sets the manager to contain the scheduled workouts for the following week
     *
     * @param scheduleWeek the week to change
     */
    @Override
    public void setToNextWeek(ScheduleWeek scheduleWeek) {
        LocalDate firstDayOfWeek;
        List<WorkoutSession> weekWorkouts;

        firstDayOfWeek = scheduleWeek.getWeekday(DateTimeConstants.MONDAY).plusWeeks(1);
        weekWorkouts = getSessionsInDateRange(firstDayOfWeek, firstDayOfWeek.plusDays(DateTimeConstants.DAYS_PER_WEEK - 1));
        scheduleWeek.nextWeek(weekWorkouts);
    }

    /**
     * Sets the manager to contain the scheduled workouts for the current week
     * @param scheduleWeek the week to change
     */
    @Override
    public void setToCurrentWeek(ScheduleWeek scheduleWeek) {
        LocalDate firstDayOfWeek;
        List<WorkoutSession> weekWorkouts;

        firstDayOfWeek = LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY);
        weekWorkouts = getSessionsInDateRange(firstDayOfWeek, firstDayOfWeek.plusDays(DateTimeConstants.DAYS_PER_WEEK - 1));
        scheduleWeek.currentWeek(weekWorkouts);
    }
}

/**
 * A template database stub for use in testing the ScheduleWeek that needs an accessor, which in
 * turn needs a database stub
 * constructor parameter
 *
 * @author Cole Kehler
 * @version 1.0
 * @since 2017-06-07
 */

class TemplateDataAccessStub implements InterfaceExerciseDataAccess, InterfaceWorkoutDataAccess, InterfaceWorkoutSessionDataAccess {
    private String dbName;
    private String dbType = "testing template";

    private Map<String, Workout> workoutsByName;
    private Map<String, Exercise> exercisesByName;
    private Map<LocalDate, WorkoutSession> workoutSessionsByDate;

    /**
     * Constructor for DataAccessStub
     * @param dbName the name of the database
     */
    public TemplateDataAccessStub (String dbName) {
        this.dbName = dbName;
    }

    /**
     * Opens the stub database and populates it with some default values
     */
    public void open(String dbPath) {

        Exercise exercise;
        WorkoutExercise workoutExercise;
        Workout workout;
        WorkoutSession workoutSession;

        exercisesByName = new HashMap<>();
        exercise = new Exercise("Bicep Curls", ExerciseIntensity.LOW, ExerciseType.ARM);
        exercisesByName.put(exercise.getName(), exercise);
        exercise = new Exercise("Push-Ups", ExerciseIntensity.HIGH, ExerciseType.ARM);
        exercisesByName.put(exercise.getName(), exercise);
        exercise = new Exercise("Running", ExerciseIntensity.HIGH, ExerciseType.CARDIO);
        exercisesByName.put(exercise.getName(), exercise);
        exercise = new Exercise("Exercise Bike", ExerciseIntensity.MEDIUM,
                ExerciseType.CARDIO);
        exercisesByName.put(exercise.getName(), exercise);
        exercise = new Exercise("Crunches", ExerciseIntensity.LOW, ExerciseType.CORE);
        exercisesByName.put(exercise.getName(), exercise);
        exercise = new Exercise("Bicycle Kicks", ExerciseIntensity.HIGH, ExerciseType.CORE);
        exercisesByName.put(exercise.getName(), exercise);
        exercise = new Exercise("Squats", ExerciseIntensity.MEDIUM, ExerciseType.LEG);
        exercisesByName.put(exercise.getName(), exercise);
        exercise = new Exercise("Lunges", ExerciseIntensity.MEDIUM, ExerciseType.LEG);
        exercisesByName.put(exercise.getName(), exercise);

        workoutsByName = new HashMap<>();
        final int xpPerIntensityLevel = 15;

        workout = new Workout("Welcome to the Gun Show");
        workoutsByName.put(workout.getName(), workout);
        exercise = exercisesByName.get("Bicep Curls");
        int exerciseExperience = (exercise.getIntensity().ordinal() + 1) * xpPerIntensityLevel;
        workoutExercise = new WorkoutExerciseSetsAndWeight(exercise, exerciseExperience, new ExerciseSetsAndWeight(3, 10, 15, WeightUnit.LBS));
        workout.addExercise(workoutExercise);
        exercise = exercisesByName.get("Push-Ups");
        exerciseExperience = (exercise.getIntensity().ordinal() + 1) * xpPerIntensityLevel;
        workoutExercise = new WorkoutExerciseSets(exercise, exerciseExperience, new ExerciseSets(2, 15));
        workout.addExercise(workoutExercise);

        workout = new Workout("Never Skip Leg Day");
        workoutsByName.put(workout.getName(), workout);
        exercise = exercisesByName.get("Squats");
        exerciseExperience = (exercise.getIntensity().ordinal() + 1) * xpPerIntensityLevel;
        workoutExercise = new WorkoutExerciseSets(exercise, exerciseExperience, new ExerciseSets(4, 15));
        workout.addExercise(workoutExercise);
        exercise = exercisesByName.get("Lunges");
        exerciseExperience = (exercise.getIntensity().ordinal() + 1) * xpPerIntensityLevel;
        workoutExercise = new WorkoutExerciseSets(exercise, exerciseExperience, new ExerciseSets(3, 10));
        workout.addExercise(workoutExercise);

        workout = new Workout("Marathon Training Starts Here");
        workoutsByName.put(workout.getName(), workout);
        exercise = exercisesByName.get("Running");
        exerciseExperience = (exercise.getIntensity().ordinal() + 1) * xpPerIntensityLevel;
        workoutExercise = new WorkoutExerciseDistance(exercise, exerciseExperience, new ExerciseDistance(2.5, DistanceUnit.MILES));
        workout.addExercise(workoutExercise);
        exercise = exercisesByName.get("Exercise Bike");
        exerciseExperience = (exercise.getIntensity().ordinal() + 1) * xpPerIntensityLevel;
        workoutExercise = new WorkoutExerciseDuration(exercise, exerciseExperience, new ExerciseDuration(45, TimeUnit.MINUTES));
        workout.addExercise(workoutExercise);

        workout = new Workout("Work that Core, Get that Score!");
        workoutsByName.put(workout.getName(), workout);
        exercise = exercisesByName.get("Crunches");
        exerciseExperience = (exercise.getIntensity().ordinal() + 1) * xpPerIntensityLevel;
        workoutExercise = new WorkoutExerciseSets(exercise, exerciseExperience, new ExerciseSets(2, 25));
        workout.addExercise(workoutExercise);
        exercise = exercisesByName.get("Bicycle Kicks");
        exerciseExperience = (exercise.getIntensity().ordinal() + 1) * xpPerIntensityLevel;
        workoutExercise = new WorkoutExerciseSets(exercise, exerciseExperience, new ExerciseSets(2, 15));
        workout.addExercise(workoutExercise);

        workoutSessionsByDate = new TreeMap<>();
        workoutSession = new WorkoutSession(
                (workoutsByName.get("Welcome to the Gun Show")),
                new LocalDate().minusWeeks(1).withDayOfWeek(DateTimeConstants.THURSDAY),
                false);
        workoutSessionsByDate.put(workoutSession.getDate(), workoutSession);

        workoutSession = new WorkoutSession(
                (workoutsByName.get("Never Skip Leg Day")),
                new LocalDate().withDayOfWeek(DateTimeConstants.TUESDAY),
                false);
        workoutSessionsByDate.put(workoutSession.getDate(), workoutSession);

        workoutSession = new WorkoutSession(
                (workoutsByName.get("Work that Core, Get that Score!")),
                new LocalDate().withDayOfWeek(DateTimeConstants.WEDNESDAY),
                false);
        workoutSessionsByDate.put(workoutSession.getDate(), workoutSession);

        workoutSession = new WorkoutSession(
                (workoutsByName.get("Never Skip Leg Day")),
                new LocalDate().withDayOfWeek(DateTimeConstants.FRIDAY),
                false);
        workoutSessionsByDate.put(workoutSession.getDate(), workoutSession);

        workoutSession = new WorkoutSession(
                (workoutsByName.get("Marathon Training Starts Here")),
                new LocalDate().plusWeeks(1).withDayOfWeek(DateTimeConstants.TUESDAY),
                false);
        workoutSessionsByDate.put(workoutSession.getDate(), workoutSession);

        System.out.println("Opened " + dbType + " database " + dbName);
    }

    /**
     * Close the stub database
     */
    public void close() {
        System.out.println("Closed " + dbType + " database " + dbName);
    }

    /**
     * Gets a list of all exercises in the database
     * @return a list of all exercises in the database
     */
    public List<Exercise> getExercisesList() {
        return new ArrayList<>(exercisesByName.values());
    }

    /**
     * Gets a list of all workouts in the database
     * @return a list of all workouts in the database
     */
    public List<Workout> getWorkoutsList() {
        return new ArrayList<>(workoutsByName.values());
    }

    /**
     * Gets a list of names of all exercises in the database
     * @return a list of names of all workouts in the database
     */
    public List<String> getWorkoutNamesList() {
        return new ArrayList<>(workoutsByName.keySet());
    }

    /**
     * Retrieves a workout from the database with the name given as parameter
     * @param workoutName the name of the workout to retrieve from the database
     * @return The workout with name workoutName, or null if no workout exists with that name
     */
    public Workout getWorkout(String workoutName) {
        return workoutsByName.get(workoutName);
    }

    /**
     * A method that returns a list of all workout sessions in the database
     * @return a list of all workout sessions in the database
     */
    public List<WorkoutSession> getWorkoutSessionsList() {
        return new ArrayList<>(workoutSessionsByDate.values());
    }

    /**
     * A method that returns a list of workout sessions scheduled in a date range
     * @param startDate the first date of the date range
     * @param endDate the last date of the date range
     * @return a list of all workout sessions scheduled between startDate and endDate, inclusive
     */
    public List<WorkoutSession> getSessionsInDateRange(LocalDate startDate,
                                                       LocalDate endDate) {
        List<WorkoutSession> sessionsInDateRange = new ArrayList<>();

        LocalDate currDate = startDate;
        while (!currDate.isAfter(endDate)) {
            if (workoutSessionsByDate.containsKey(currDate)) {
                sessionsInDateRange.add(workoutSessionsByDate.get(currDate));
            }
            currDate = currDate.plusDays(1);
        }

        return sessionsInDateRange;
    }

    /**
     * Retrieves a workout session scheduled on the given date from the database, if it exists. If
     * no workout session is found for that date, returns null.
     * @param dateOfSession the date to get the workout session for
     * @return the workout session scheduled on the given date
     */
    public WorkoutSession getWorkoutSession(LocalDate dateOfSession) {
        return workoutSessionsByDate.get(dateOfSession);
    }

    /**
     * Inserts a new workout session into the database
     * @param workoutSession the new workout session to insert into the database
     */
    public void insertWorkoutSession(WorkoutSession workoutSession) {
        workoutSessionsByDate.put(workoutSession.getDate(), workoutSession);
    }

    /**
     * Removes a workout session from the database, if it exists
     * @param workoutSession the workout session to remove from the database
     */
    public void removeWorkoutSession(WorkoutSession workoutSession) {
        workoutSessionsByDate.remove(workoutSession.getDate());
    }

    /**
     * Toggles the completed state of a workout in the database
     *
     * @param workoutSession the workout to change the state of
     */
    @Override
    public void toggleWorkoutComplete(WorkoutSession workoutSession) {
        workoutSessionsByDate.get(workoutSession.getDate()).toggleCompleted();
    }
}