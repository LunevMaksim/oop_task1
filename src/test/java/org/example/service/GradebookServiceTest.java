package org.example.service;

import org.example.exception.InvalidScoreException;
import org.example.exception.RetakeLimitExceededException;
import org.example.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Тесты
class GradebookServiceTest {

    private GradebookService service;
    private Student student1;
    private Student student2;
    private Discipline oop;
    private ControlPoint cp1;
    private ControlPoint cp2;
    private Group group;

    @BeforeEach
    void setUp() {
        service = new GradebookService();
        student1 = new Student("S101", "Иван Иванов", "G1");
        student2 = new Student("S102", "Пётр Петров", "G1");
        group = new Group("G1", "ПИ-1", List.of(student1, student2));

        oop = new Discipline("D1", "ООП");
        cp1 = new ControlPoint("CP1", "КТ-1", 0.4, 100, oop);
        cp2 = new ControlPoint("CP2", "КТ-2", 0.6, 100, oop);
    }

    @Test void testAddAssessmentSuccess() {
        Assessment a = service.addAssessment(student1, cp1, 85, LocalDate.now());
        assertNotNull(a);
        assertEquals(85, a.getScore());
    }

    @Test void testAddAssessmentNegativeScoreThrowsException() {
        assertThrows(InvalidScoreException.class, () -> service.addAssessment(student1, cp1, -5, LocalDate.now()));
    }

    @Test void testAddAssessmentScoreExceedsMaxThrowsException() {
        assertThrows(InvalidScoreException.class, () -> service.addAssessment(student1, cp1, 105, LocalDate.now()));
    }

    @Test void testFirstAttemptNumberIsOne() {
        Assessment a = service.addAssessment(student1, cp1, 70, LocalDate.now());
        assertEquals(1, a.getAttemptNumber());
    }

    @Test void testSecondAttemptIncrementsAttemptNumber() {
        service.addAssessment(student1, cp1, 40, LocalDate.now());
        Assessment a2 = service.addAssessment(student1, cp1, 60, LocalDate.now());
        assertEquals(2, a2.getAttemptNumber());
    }

    @Test void testRetakeLimitExceededThrowsException() {
        service.addAssessment(student1, cp1, 40, LocalDate.now());
        service.addAssessment(student1, cp1, 45, LocalDate.now());
        service.addAssessment(student1, cp1, 48, LocalDate.now());
        assertThrows(RetakeLimitExceededException.class, () -> service.addAssessment(student1, cp1, 60, LocalDate.now()));
    }

    @Test void testStudentAverageScoreWithSingleAssessment() {
        service.addAssessment(student1, cp1, 80, LocalDate.now());
        assertEquals(80.0, service.getStudentAverageScore(student1));
    }

    @Test void testStudentAverageScoreConsidersOnlyLatestAttempt() {
        service.addAssessment(student1, cp1, 40, LocalDate.now());
        service.addAssessment(student1, cp1, 90, LocalDate.now());
        assertEquals(90.0, service.getStudentAverageScore(student1));
    }

    @Test void testStudentAverageScoreForStudentWithoutAssessments() {
        assertEquals(0.0, service.getStudentAverageScore(student1));
    }

    @Test void testGetExcellentStudentsReturnsCorrectStudent() {
        service.addAssessment(student1, cp1, 90, LocalDate.now());
        service.addAssessment(student1, cp2, 88, LocalDate.now());
        List<Student> excellent = service.getExcellentStudents(group);
        assertEquals(1, excellent.size());
        assertTrue(excellent.contains(student1));
    }

    @Test void testGetDebtorsReturnsStudentWithLowScore() {
        service.addAssessment(student2, cp1, 45, LocalDate.now());
        List<Student> debtors = service.getDebtors(group);
        assertEquals(1, debtors.size());
        assertTrue(debtors.contains(student2));
    }

    @Test void testGetDisciplineStatementFiltersByDisciplineAndDate() {
        LocalDate today = LocalDate.now();
        service.addAssessment(student1, cp1, 80, today);
        service.addAssessment(student1, cp1, 90, today.plusDays(5));
        List<Assessment> statement = service.getDisciplineStatement(oop, today);
        assertEquals(1, statement.size());
    }

    @Test void testGetGroupAverageScore() {
        service.addAssessment(student1, cp1, 80, LocalDate.now());
        service.addAssessment(student2, cp1, 60, LocalDate.now());
        assertEquals(70.0, service.getGroupAverageScore(group, oop));
    }

    @Test void testGetStudentRatingWeightedCalculation() {
        service.addAssessment(student1, cp1, 80, LocalDate.now()); // 80 * 0.4 = 32
        service.addAssessment(student1, cp2, 100, LocalDate.now()); // 100 * 0.6 = 60
        assertEquals(92.0, service.getStudentRating(student1), 0.001);
    }

    @Test void testGradeEnumFromScore() {
        assertEquals(Grade.EXCELLENT, Grade.fromScore(95));
        assertEquals(Grade.UNSATISFACTORY, Grade.fromScore(30));
    }
}