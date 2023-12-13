package com.mvc.springboot.restapi;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvc.springboot.restapi.models.CollegeStudent;
import com.mvc.springboot.restapi.models.MathGrade;
import com.mvc.springboot.restapi.repository.HistoryGradesDao;
import com.mvc.springboot.restapi.repository.MathGradesDao;
import com.mvc.springboot.restapi.repository.ScienceGradesDao;
import com.mvc.springboot.restapi.repository.StudentDao;
import com.mvc.springboot.restapi.service.StudentAndGradeService;

@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class GradeStudentControllerTest {
	
	private static MockHttpServletRequest request;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Mock
	StudentAndGradeService studentCreateServiceMock;
	
	@Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private MathGradesDao mathGradeDao;

    @Autowired
    private ScienceGradesDao scienceGradeDao;

    @Autowired
    private HistoryGradesDao historyGradeDao;

    @Autowired
    private StudentAndGradeService studentService;
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    ObjectMapper objectMapper;
    
    @Autowired
    private CollegeStudent student;

    @Value("${sql.script.create.student}")
    private String sqlAddStudent;

    @Value("${sql.script.create.math.grade}")
    private String sqlAddMathGrade;

    @Value("${sql.script.create.science.grade}")
    private String sqlAddScienceGrade;

    @Value("${sql.script.create.history.grade}")
    private String sqlAddHistoryGrade;

    @Value("${sql.script.delete.student}")
    private String sqlDeleteStudent;

    @Value("${sql.script.delete.math.grade}")
    private String sqlDeleteMathGrade;

    @Value("${sql.script.delete.science.grade}")
    private String sqlDeleteScienceGrade;

    @Value("${sql.script.delete.history.grade}")
    private String sqlDeleteHistoryGrade;

    public static final MediaType APPLICATION_JSON_UTF8=MediaType.APPLICATION_JSON;
   
    @BeforeAll
    public static void setup() {
    	request = new MockHttpServletRequest();
    	request.setParameter("firstname", "Ramya");
    	request.setParameter("lastname", "Karanam");
    	request.setParameter("emailAddress", "ramya123@gmail.com");
    }
    
    @BeforeEach
    public void setupDatabase() {
        jdbc.execute(sqlAddStudent);
        jdbc.execute(sqlAddMathGrade);
        jdbc.execute(sqlAddScienceGrade);
        jdbc.execute(sqlAddHistoryGrade);
    }
    
    @DisplayName("Getting student list")
    @Test
    public void getStudentList() throws Exception {
    	
    	student.setFirstname("Cincinanti");
    	student.setLastname("CEAS");
    	student.setEmailAddress("karanara@mail.uc.edu");
    	entityManager.persist(student);
    	entityManager.flush();
    	mockMvc.perform(MockMvcRequestBuilders.get("/")).andExpect(status().isOk())
    	.andExpect(content().contentType(APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$", hasSize(2)));
    }
    
    @DisplayName("create Student")
    @Test
    public void createStudentRequest() throws  Exception {
    	student.setFirstname("abcd");
    	student.setLastname("zxcv");
    	student.setEmailAddress("qwert@mail.uc.edu");
    	mockMvc.perform(post("/")
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsString(student)))
    	        .andExpect(status().isOk())
    	        .andExpect(jsonPath("$",hasSize(2)));
       CollegeStudent student1= studentDao.findByEmailAddress("qwert@mail.uc.edu");
       assertNotNull(student1,"object should not be null");
    			
    }

    @DisplayName("delete student")
    @Test
    public void deleteStudent() throws Exception{
    	assertTrue(studentDao.findById(1).isPresent());
    	
    	mockMvc.perform(MockMvcRequestBuilders.delete("/student/{id}",1)).andExpect(status().isOk())
    	.andExpect(content().contentType(APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$", hasSize(0)));
    	
    	assertFalse(studentDao.findById(1).isPresent());

    }
    
    @DisplayName("delete student who doesnt exist")
    @Test
    public void deleteStudentHttpRequestErrorPage() throws Exception {
        assertFalse(studentDao.findById(0).isPresent());

        mockMvc.perform(MockMvcRequestBuilders.delete("/student/{id}", 0))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }
    @DisplayName("get student information")
    @Test
    public void getStudentInformation() throws Exception {
    	Optional<CollegeStudent> student1 = studentDao.findById(1);
    	assertTrue(student1.isPresent());

        mockMvc.perform(MockMvcRequestBuilders.get("/studentInformation/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstname", is("Joshna")))
                .andExpect(jsonPath("$.lastname", is("Karanam")))
                .andExpect(jsonPath("$.emailAddress", is("joshna123@gmail.com")));
    	
    }
    @DisplayName("get student information who doesnt exist")
    @Test
    public void getStudentHttpRequestErrorPage() throws Exception {
        assertFalse(studentDao.findById(0).isPresent());

        mockMvc.perform(MockMvcRequestBuilders.get("/studentInformation/{id}", 0))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }
    @DisplayName("Create Grade")
    @Test
    public void createGradeRequest() throws Exception {
    	 mockMvc.perform(post("/grades")
                 .contentType(MediaType.APPLICATION_JSON)
                 .param("grade", "85.00")
                 .param("gradeType", "math")
                 .param("studentId", "1"))
         .andExpect(status().isOk())
         .andExpect(content().contentType(APPLICATION_JSON_UTF8))
    	        .andExpect(jsonPath("$.id",is(1)))
    	        .andExpect(jsonPath("$.firstname", is("Joshna")))
                .andExpect(jsonPath("$.lastname", is("Karanam")))
                .andExpect(jsonPath("$.emailAddress", is("joshna123@gmail.com")))
                .andExpect(jsonPath("$.studentGrades.mathGradeResults", hasSize(2)));
    }
    @DisplayName("create Grade with invalid Student id")
    @Test
    public void createGradeRequestInvalidStudent() throws Exception{
    	mockMvc.perform(post("/grades")
                .contentType(MediaType.APPLICATION_JSON)
                .param("grade", "85.00")
                .param("gradeType", "math")
                .param("studentId", "0"))
    	.andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    	
    }
    @DisplayName("create Grade with invalid SUbject/GradeType id")
    @Test
    public void createGradeRequestInvalidSubject() throws Exception{
    	mockMvc.perform(post("/grades")
                .contentType(MediaType.APPLICATION_JSON)
                .param("grade", "85.00")
                .param("gradeType", "literature")
                .param("studentId", "1"))
    	.andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    	
    }
    
    @DisplayName("delete the grade of a student")
    @Test
    public void deleteGradeStudent() throws Exception{
    	 Optional<MathGrade> mathGrade = mathGradeDao.findById(1);

         assertTrue(mathGrade.isPresent());

         mockMvc.perform(MockMvcRequestBuilders.delete("/grades/{id}/{gradeType}", 1 , "math"))
                 .andExpect(status().isOk())
                 .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                 .andExpect(jsonPath("$.id", is(1)))
                 .andExpect(jsonPath("$.firstname", is("Joshna")))
                 .andExpect(jsonPath("$.lastname", is("Karanam")))
                 .andExpect(jsonPath("$.emailAddress", is("joshna123@gmail.com")))
                 .andExpect(jsonPath("$.studentGrades.mathGradeResults", hasSize(0)));
    }
    
    @DisplayName("delete the grade of a student which gradeID doesNt exist")
    @Test
    public void deleteGradeStudentInvalidGrade() throws Exception{
    	 Optional<MathGrade> mathGrade = mathGradeDao.findById(1);

         assertTrue(mathGrade.isPresent());

         mockMvc.perform(MockMvcRequestBuilders.delete("/grades/{id}/{gradeType}", 0 , "math"))
                 .andExpect(status().is4xxClientError())
                 .andExpect(jsonPath("$.status", is(404)))
                 .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
             	
    }
    
    @DisplayName("delete the grade of a student which subject doesNt exist")
    @Test
    public void deleteGradeStudentInvalidGradeType() throws Exception{
    	 Optional<MathGrade> mathGrade = mathGradeDao.findById(1);

         assertTrue(mathGrade.isPresent());

         mockMvc.perform(MockMvcRequestBuilders.delete("/grades/{id}/{gradeType}", 1 , "literature"))
                 .andExpect(status().is4xxClientError())
                 .andExpect(jsonPath("$.status", is(404)))
                 .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
             	
    }
    
    @AfterEach
    public void setupAfterTransaction() {
        jdbc.execute(sqlDeleteStudent);
        jdbc.execute(sqlDeleteMathGrade);
        jdbc.execute(sqlDeleteScienceGrade);
        jdbc.execute(sqlDeleteHistoryGrade);
    }
	
	
	
}
