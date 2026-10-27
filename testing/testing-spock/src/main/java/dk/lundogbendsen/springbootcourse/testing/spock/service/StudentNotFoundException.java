package dk.lundogbendsen.springbootcourse.testing.spock.service;

public class StudentNotFoundException extends EducationException {
    public StudentNotFoundException(Long studentId) {
        super("Student not found with ID: " + studentId);
    }
}
