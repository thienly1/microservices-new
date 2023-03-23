package ly.projects.productservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ly.projects.productservice.dto.ProductRequest;
import ly.projects.productservice.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

import java.math.BigDecimal;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@Transactional
@DirtiesContext
public class ProductControllerTest {

    public static final String APPLICATION_JSON = "application/json";
    public static final String PRODUCT_TEST_URL = "/api/product";
    public static final String ID = "/{id}";
    public static final String TEXT_PLAIN_CHARSET_UTF_8 = "text/plain;charset=UTF-8";
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    @Autowired
    private TestEntityManager em;
    @Autowired private WebApplicationContext webApplicationContext;
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo: 6.0.2");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
        dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    public List<Product> products(){
        return Arrays.asList(
                new Product(null, "Test", "Testsson", BigDecimal.valueOf(1200)),
                new Product(null, "Test2", "Testsson2", BigDecimal.valueOf(1200)),
                new Product(null, "Test3", "Testsson3", BigDecimal.valueOf(1300)),
                new Product(null, "Test4", "Testsson4", BigDecimal.valueOf(1400)),
                new Product(null, "Test5", "Testsson5", BigDecimal.valueOf(1500))
        );
    }

    private List<Product> persistedProduct;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        persistedProduct = products().stream()
                .map(em::persist)
                .collect(Collectors.toList());
    }

    @Test
    void contextTest() {
        assertNotNull(mockMvc);
        assertNotNull(objectMapper);
        assertNotNull(em);
        assertNotNull(webApplicationContext);
    }

    @Test
    @DisplayName("Given valid ProductRequest create() successfully persist object and return expected and status 201")
    void create() throws Exception{
        ProductRequest formInData = new ProductRequest();
        formInData.setName("Test");
        formInData.setDescription("Testsson");
        formInData.setPrice(BigDecimal.valueOf(1200));
        String jsonForm = objectMapper.writeValueAsString(formInData);

        mockMvc.perform(post(PRODUCT_TEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonForm))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(formInData.getName()))
                .andExpect(jsonPath("$.description").value(formInData.getDescription()))
                .andExpect(jsonPath("$.price").value(formInData.getPrice()));
    }








}
