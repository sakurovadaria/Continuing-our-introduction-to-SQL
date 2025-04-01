package Continuing.our.introduction.to.SQL.controller;

import Continuing.our.introduction.to.SQL.exception.StudentNotFoundException;
import Continuing.our.introduction.to.SQL.model.Student;
import Continuing.our.introduction.to.SQL.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;



    @Test
    public void testGetStudentById() throws Exception {

        Student student = new Student( 44, "Snape");
        when(studentService.getStudent(anyLong())).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders.get("/students/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.name").value("Snape"))
                .andExpect(jsonPath("$.age").value(44));
    }

    @Test
    public void testGetStudentByIdWhenStudentNotExist() throws Exception {

        when(studentService.getStudent(anyLong())).thenThrow(StudentNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/students/1"))
                .andDo(print())


                .andExpect(status().isNotFound());

    }

    @Test
    public void testCreateStudent() throws Exception{
        Student student = new Student(44, "Snape");
        when(studentService.createStudent(any(Student.class))).thenReturn(student);

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andDo(print())
                .andExpect(status().isOk());

    }
    @Test
    public void testUpdateStudent() throws Exception{
        Student student = new Student(44, "Snape");
        when(studentService.updateStudent(any(), any(Student.class))).thenReturn(student);

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.put("/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    public void testDeleteStudent() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/students/1"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }


}