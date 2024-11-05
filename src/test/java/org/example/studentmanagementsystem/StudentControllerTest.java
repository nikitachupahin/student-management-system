package org.example.studentmanagementsystem;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class StudentControllerTest {

    private int uriVar = 15;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAllStudents() throws Exception {
        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(view().name("students"))
                .andExpect(model().attributeExists("students"));
    }

    @Test
    public void testAddStudentForm() throws Exception {
        mockMvc.perform(get("/students/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-student"))
                .andExpect(model().attributeExists("student"));
    }

    @Test
    public void testAddStudent() throws Exception {
        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("studentCardNumber", "12345")
                        .param("fullName", "John Doe")
                        .param("course", "3")
                        .param("groupCode", "GRP01")
                        .param("averageScore", "3.5")
                        .param("publicWorkParticipation", "true")
                        .param("numberOfExams", "2")
                        .param("livingInDormitory", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"));
    }

    @Test
    public void testUpdateStudentForm() throws Exception {
        mockMvc.perform(get("/students/update/{id}", 2))
                .andExpect(status().isOk())
                .andExpect(view().name("update-student"))
                .andExpect(model().attributeExists("student"));
    }

    @Test
    public void testEditStudent() throws Exception {
        mockMvc.perform(post("/students/{id}", uriVar)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("fullName", "Jane Doe")
                        .param("course", "4")
                        .param("groupCode", "GRP02")
                        .param("averageScore", "4.0")
                        .param("publicWorkParticipation", "false")
                        .param("numberOfExams", "3")
                        .param("livingInDormitory", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"));
    }

    @Test
    public void testViewStudentGrades() throws Exception {
        mockMvc.perform(get("/students/{id}/grades", 2))
                .andExpect(status().isOk())
                .andExpect(view().name("student-grades"))
                .andExpect(model().attributeExists("student"))
                .andExpect(model().attributeExists("grades"));
    }

    @Test
    public void testIncrScholarship() throws Exception {
        mockMvc.perform(get("/IncrScholarship"))
                .andExpect(status().isOk())
                .andExpect(view().name("incr-scholarship"))
                .andExpect(model().attributeExists("students"));
    }

    @Test
    public void testStandardScholarship() throws Exception {
        mockMvc.perform(get("/StandardScholarship"))
                .andExpect(status().isOk())
                .andExpect(view().name("standard-scholarship"))
                .andExpect(model().attributeExists("students"));
    }

    @Test
    public void testGetAllStudentsGroupedByGroupCode() throws Exception {
        mockMvc.perform(get("/studentsGrouped"))
                .andExpect(status().isOk())
                .andExpect(view().name("students-by-groups"))
                .andExpect(model().attributeExists("studentsGrouped"));
    }

    @Test
    public void testGetGroupsWithAverageScores() throws Exception {
        mockMvc.perform(get("/groupsGrades"))
                .andExpect(status().isOk())
                .andExpect(view().name("groups-grades"))
                .andExpect(model().attributeExists("averageScoresByGroup"));
    }

    @Test
    public void testGetStudentsForExpulsion() throws Exception {
        mockMvc.perform(get("/expulsion"))
                .andExpect(status().isOk())
                .andExpect(view().name("students-for-expulsion"))
                .andExpect(model().attributeExists("students"));
    }

    @Test
    public void testDeleteStudent() throws Exception {
        mockMvc.perform(get("/students/remove/{id}", uriVar))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"));
    }
}
