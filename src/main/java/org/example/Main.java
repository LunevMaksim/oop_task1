package org.example;

import org.example.model.*;
import org.example.service.GradebookService;

import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Запуск системы 'Журнал успеваемости' ===");

        Student student1 = new Student("S101", "Иван Иванов", "ПИ-1");
        Student student2 = new Student("S102", "Пётр Петров", "ПИ-1");
        Group group = new Group("G1", "ПИ-1", List.of(student1, student2));

        Discipline oop = new Discipline("D1", "Объектно-ориентированное программирование");
        ControlPoint cp1 = new ControlPoint("CP1", "Лабораторная 1", 0.4, 100, oop);
        ControlPoint cp2 = new ControlPoint("CP2", "Курсовая работа", 0.6, 100, oop);

        GradebookService service = new GradebookService();

        service.addAssessment(student1, cp1, 90, LocalDate.now());
        service.addAssessment(student1, cp2, 85, LocalDate.now());

        service.addAssessment(student2, cp1, 40, LocalDate.now()); // Долг

        System.out.println("\n--- Результаты ---");
        System.out.println("Средний балл Ивана: " + service.getStudentAverageScore(student1));
        System.out.println("Рейтинг Ивана: " + service.getStudentRating(student1));

        System.out.println("\nОтличники в группе: ");
        service.getExcellentStudents(group).forEach(s -> System.out.println("- " + s.getFullName()));

        System.out.println("\nДолжники в группе: ");
        service.getDebtors(group).forEach(s -> System.out.println("- " + s.getFullName()));
    }
}