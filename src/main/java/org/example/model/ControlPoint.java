package org.example.model;

// Тест или экзамен
public class ControlPoint {
    private String id;
    private String name;
    private double weight;
    private int maxScore;
    private Discipline discipline;

    public ControlPoint(String id, String name, double weight, int maxScore, Discipline discipline) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.maxScore = maxScore;
        this.discipline = discipline;
    }
    public ControlPoint(String id, String name, double weight, Discipline discipline) {
        this(id, name, weight, 100, discipline);
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getWeight() {
        return weight;
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }
    public int getMaxScore() {
        return maxScore;
    }
    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }
    public Discipline getDiscipline() {
        return discipline;
    }
    public void setDiscipline(Discipline discipline) {
        this.discipline = discipline;
    }

}
