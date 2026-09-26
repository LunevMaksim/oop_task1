package org.example.model;

import java.util.Objects;

// Студент
public class Student {
    private final String recordBookNumber;
    private String fullName;
    private String groupId;

    public Student(String recordBookNumber, String fullName, String groupId) {
        this.recordBookNumber = recordBookNumber;
        this.fullName = fullName;
        this.groupId = groupId;
    }

    public String getRecordBookNumber() {
        return recordBookNumber;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getGroupId() {
        return groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(recordBookNumber, student.recordBookNumber);
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(recordBookNumber);
    }

}
