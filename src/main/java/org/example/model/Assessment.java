package org.example.model;

import java.time.LocalDate;

public class Assessment {
    private Student student;
    private ControlPoint controlPoint;
    private int score;
    private int attemptNumber;
    private LocalDate date;

    public Assessment(Student student, ControlPoint controlPoint, int score, int attemptNumber, LocalDate date) {
        this.student = student;
        this.controlPoint = controlPoint;
        this.score = score;
        this.attemptNumber = attemptNumber;
        this.date = date;
    }

    public Student getStudent() {
        return student;
    }
    public void setStudent(Student student) {
        this.student = student;
    }
    public ControlPoint getControlPoint() {
        return controlPoint;
    }
    public void setControlPoint(ControlPoint controlPoint) {
        this.controlPoint = controlPoint;
    }
    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }
    public int getAttemptNumber() {
        return attemptNumber;
    }
    public void setAttemptNumber(int attemptNumber) {
        this.attemptNumber = attemptNumber;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

}
