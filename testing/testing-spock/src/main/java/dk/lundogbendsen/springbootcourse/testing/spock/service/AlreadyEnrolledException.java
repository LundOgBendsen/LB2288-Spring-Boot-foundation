package dk.lundogbendsen.springbootcourse.testing.spock.service;

public class AlreadyEnrolledException extends EducationException {
    public AlreadyEnrolledException(String studentName, String courseSubject) {
        super("Student '" + studentName + "' is already enrolled in course '" + courseSubject + "'");
    }
}
