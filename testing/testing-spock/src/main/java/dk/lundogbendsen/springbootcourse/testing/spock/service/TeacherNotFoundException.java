package dk.lundogbendsen.springbootcourse.testing.spock.service;

public class TeacherNotFoundException extends EducationException {
    public TeacherNotFoundException(Long teacherId) {
        super("Teacher not found with ID: " + teacherId);
    }
}
