package org.example.model;

import java.util.ArrayList;
import java.util.List;

// Учбеная группа
public class Group {
    private String id;
    private String name;
    private List<Student> students;

    public Group(String id, String name, List<Student> students) {
        this.id = id;
        this.name = name;
        this.students = new ArrayList<>(students);
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
    public List<Student> getStudents() {
        return new ArrayList<>(students);
    }
    public void setStudents(List<Student> students) {
        this.students = students;
    }

}
