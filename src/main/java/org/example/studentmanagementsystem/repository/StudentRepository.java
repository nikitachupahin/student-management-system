package org.example.studentmanagementsystem.repository;

import org.example.studentmanagementsystem.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//@Repository // letting kno its Repository rather than default // not needed on service layer
public interface StudentRepository extends JpaRepository<Student,Long> {
    // <entity name , typeof primary key> // type of primary key cannot be primitive
}
