package org.example.studentmanagementsystem.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "student_card_number", nullable = false, unique = true)
    private String studentCardNumber;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "course", nullable = false)
    private int course;

    @Column(name = "major_code", nullable = false)
    private String majorCode;

    @Column(name = "average_score", nullable = false)
    private double averageScore;

    @Column(name = "public_work_participation", nullable = false)
    private boolean publicWorkParticipation; // true for '1' and false for '0'

    public Student() {
    }

    public Student(String studentCardNumber, String fullName, int course, String majorCode, double averageScore, boolean publicWorkParticipation) {
        this.studentCardNumber = studentCardNumber;
        this.fullName = fullName;
        this.course = course;
        this.majorCode = majorCode;
        this.averageScore = averageScore;
        this.publicWorkParticipation = publicWorkParticipation;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public String getStudentCardNumber() {
        return studentCardNumber;
    }

    public void setStudentCardNumber(String studentCardNumber) {
        this.studentCardNumber = studentCardNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getCourse() {
        return course;
    }

    public void setCourse(int course) {
        this.course = course;
    }

    public String getMajorCode() {
        return majorCode;
    }

    public void setMajorCode(String majorCode) {
        this.majorCode = majorCode;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public boolean isPublicWorkParticipation() {
        return publicWorkParticipation;
    }

    public void setPublicWorkParticipation(boolean publicWorkParticipation) {
        this.publicWorkParticipation = publicWorkParticipation;
    }
}
