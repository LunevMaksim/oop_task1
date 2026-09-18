package org.example.service;

import org.example.exception.InvalidScoreException;
import org.example.exception.RetakeLimitExceededException;
import org.example.model.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для управления журналом успеваемости студентов.
 * Обеспечивает добавление оценок, отслеживание попыток пересдач,
 * а также вычисление статистических показателей и рейтингов.
 */
public class GradebookService {

    private List<Assessment> magazine;
    private final static int MAX_ATTEMPTS = 3;

    /**
     * Создает новый экземпляр сервиса с пустым журналом оценок.
     */
    public GradebookService() {
        magazine = new ArrayList<>();
    }

    /**
     * Добавляет новую оценку за контрольную точку для указанного студента.
     *
     * @param student студент, получающий оценку
     * @param controlPoint контрольная точка, за которую выставляется оценка
     * @param score количество полученных баллов
     * @param date дата сдачи
     * @return созданный объект {@link Assessment}
     * @throws InvalidScoreException если балл отрицательный или превышает максимально допустимый для КТ
     * @throws RetakeLimitExceededException если количество попыток сдачи превышает установленный лимит (3)
     */
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

    /**
     * Возвращает список только последних попыток сдачи для каждого предмета/КТ указанного студента.
     *
     * @param student студент, для которого ищутся оценки
     * @return список актуальных (последних по номеру попытки) оценок
     */
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

    /**
     * Возвращает список отличников в группе (студенты, у которых все последние оценки не ниже 85 баллов).
     *
     * @param group учебная группа
     * @return список студентов-отличников
     */
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

    /**
     * Возвращает список должников в группе (студенты, имеющие хотя бы одну актуальную оценку ниже 50 баллов).
     *
     * @param group учебная группа
     * @return список студентов-должников
     */
    public List<Student> getDebtors(Group group) {
        List<Student> debtors = new ArrayList<>();

        for (Student student : group.getStudents()) {
            List<Assessment> studentAssessments = getLatestAssessmentsForStudent(student);

            for (Assessment a : studentAssessments) {
                if (a.getScore() < 50) {
                    debtors.add(student);
                    break;
                }
            }
        }
        return debtors;
    }

    /**
     * Формирует ведомость оценок по заданной дисциплине, выставленных не позднее указанной даты.
     *
     * @param discipline дисциплина
     * @param date граничная дата включения оценок в ведомость
     * @return список оценок для ведомости
     */
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

    /**
     * Вычисляет средний балл группы по конкретной дисциплине на основе последних попыток сдачи.
     *
     * @param group учебная группа
     * @param discipline дисциплина
     * @return средний балл группы (0.0, если оценок нет)
     */
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

    /**
     * Вычисляет взвешенный рейтинг студента с учетом весов контрольных точек.
     *
     * @param student студент
     * @return взвешенный рейтинг (0.0, если оценок нет или суммарный вес равен 0)
     */
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

    /**
     * Вычисляет средний балл студента по всем контрольным точкам (на основе последних попыток).
     *
     * @param student студент
     * @return средний балл (0.0, если оценок нет)
     */
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