package org.example.studentmanagementsystem.repository;

import org.example.studentmanagementsystem.entity.StudentGrade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentGradeRepository extends JpaRepository<StudentGrade, Long> {
    void deleteByStudentId(Long studentId);
}
