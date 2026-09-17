package org.example.service;

import org.example.exception.InvalidScoreException;
import org.example.exception.RetakeLimitExceededException;
import org.example.model.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GradebookService {

    private List<Assessment> magazine;
    private final static int MAX_ATTEMPTS = 3;

    public GradebookService() {
        magazine = new ArrayList<>();
    }

    public Assessment addAssessment(Student student, ControlPoint controlPoint, int score, LocalDate date) {
        if (score < 0 || score > controlPoint.getMaxScore()) {
            throw new InvalidScoreException("Балл должен быть от 0 до " + controlPoint.getMaxScore());
        }

        int attemptsCount = 0;
        for (Assessment a : magazine) {
            if (a.getStudent().equals(student) && a.getControlPoint().equals(controlPoint)) {
                attemptsCount++;
            }
        }

        if (attemptsCount >= MAX_ATTEMPTS) {
            throw new RetakeLimitExceededException("Превышен лимит попыток!");
        }

        Assessment newAssessment = new Assessment(student, controlPoint, score, attemptsCount + 1, date);
        magazine.add(newAssessment);
        return newAssessment;
    }

    private List<Assessment> getLatestAssessmentsForStudent(Student student) {
        List<Assessment> latest = new ArrayList<>();

        for (Assessment a : magazine) {
            if (a.getStudent().equals(student)) {
                Assessment existing = null;
                for (Assessment l : latest) {
                    if (l.getControlPoint().equals(a.getControlPoint())) {
                        existing = l;
                        break;
                    }
                }
                if (existing == null) {
                    latest.add(a);
                }
                else if (a.getAttemptNumber() > existing.getAttemptNumber()) {
                    latest.remove(existing);
                    latest.add(a);
                }
            }
        }
        return latest;
    }

    public List<Student> getExcellentStudents(Group group) {
        List<Student> result = new ArrayList<>();

        for (Student student : group.getStudents()) {
            List<Assessment> studentAssessments = getLatestAssessmentsForStudent(student);

            if (studentAssessments.isEmpty()) {
                continue;
            }

            boolean isExcellent = true;
            for (Assessment a : studentAssessments) {
                if (a.getScore() < 85) {
                    isExcellent = false;
                    break;
                }
            }

            if (isExcellent) {
                result.add(student);
            }
        }
        return result;
    }

    public List<Student> getDebtors(Group group) {
        List<Student> debtors = new ArrayList<>();

        for (Student student : group.getStudents()) {
            List<Assessment> studentAssessments = getLatestAssessmentsForStudent(student);

            for (Assessment a : studentAssessments) {
                if (a.getScore() < 50) {
                    debtors.add(student);
                    break; // Достаточно одного долга
                }
            }
        }
        return debtors;
    }

    public List<Assessment> getDisciplineStatement(Discipline discipline, LocalDate date) {
        List<Assessment> statement = new ArrayList<>();

        for (Assessment a : magazine) {
            boolean isSameDiscipline = a.getControlPoint().getDiscipline().equals(discipline);
            boolean isBeforeOrEqualDate = !a.getDate().isAfter(date);

            if (isSameDiscipline && isBeforeOrEqualDate) {
                statement.add(a);
            }
        }
        return statement;
    }

    public double getGroupAverageScore(Group group, Discipline discipline) {
        double totalScore = 0;
        int count = 0;

        for (Student student : group.getStudents()) {
            List<Assessment> latestAssessments = getLatestAssessmentsForStudent(student);
            for (Assessment a : latestAssessments) {
                if (a.getControlPoint().getDiscipline().equals(discipline)) {
                    totalScore += a.getScore();
                    count++;
                }
            }
        }

        if (count == 0) {
            return 0.0;
        }

        return totalScore / count;
    }

    public double getStudentRating(Student student) {
        List<Assessment> latest = getLatestAssessmentsForStudent(student);
        if (latest.isEmpty()) {
            return 0.0;
        }

        double weightedSum = 0;
        double totalWeight = 0;

        for (Assessment a : latest) {
            double weight = a.getControlPoint().getWeight();
            weightedSum += a.getScore() * weight;
            totalWeight += weight;
        }

        if (totalWeight == 0) {
            return 0.0;
        }

        return weightedSum / totalWeight;
    }

    public double getStudentAverageScore(Student student) {
        List<Assessment> latestAssessments = getLatestAssessmentsForStudent(student);
        if (latestAssessments.isEmpty()) {
            return 0.0;
        }
        double sum = 0;
        for (Assessment a : latestAssessments) {
            sum += a.getScore();
        }
        return sum / latestAssessments.size();
    }
}
