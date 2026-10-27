package dk.lundogbendsen.springbootcourse.testing.spock.service;

public class MaxEnrollmentExceededException extends EducationException {
    public MaxEnrollmentExceededException(String studentName, int maxLimit) {
        super("Student '" + studentName + "' has exceeded maximum allowed enrollments of " + maxLimit);
    }
}
