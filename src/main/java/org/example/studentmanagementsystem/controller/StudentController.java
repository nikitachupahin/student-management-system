package org.example.studentmanagementsystem.controller;

import org.example.studentmanagementsystem.entity.Student;
import org.example.studentmanagementsystem.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class StudentController {

    private final StudentService service;

    @Autowired
    public StudentController(StudentService service) {
        this.service = service;
    }

    @RequestMapping("/students")
    String getAllStudents(Model model){
        model.addAttribute("students", service.getAllStudents());
        return "students";
    }

    @RequestMapping("students/add")
    String addStudentForm(Model model){
        model.addAttribute("student", new Student());
        return "add-student";
    }

    @PostMapping("students")
    String addStudent(@ModelAttribute("student") Student student, @RequestParam Map<String, Object> grades) {
        service.addStudent(student, grades);
        return "redirect:/students";
    }

    @RequestMapping("students/remove/{id}")
    String deleteStudent(@PathVariable("id") long id) {
        service.deleteStudent(id);
        return "redirect:/students";
    }

    @GetMapping("students/update/{id}")
    String updateStudentForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("student", service.getStudentById(id));
        return "update-student";
    }

    @PostMapping("students/{id}")
    String editStudent(@PathVariable Long id, @ModelAttribute("student") Student student, @RequestParam Map<String, Object> grades) {

        Student existingStudent = service.getStudentById(id);

        if (student.getStudentCardNumber() == null || student.getStudentCardNumber().isEmpty()) {
            student.setStudentCardNumber(existingStudent.getStudentCardNumber());
        }

        service.updateStudent(student, grades);
        return "redirect:/students";
    }

}
