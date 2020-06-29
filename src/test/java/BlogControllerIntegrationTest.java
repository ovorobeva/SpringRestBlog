import com.github.ovorobeva.MainApplicationClass;
import com.github.ovorobeva.dao.BlogRepository;
import com.github.ovorobeva.dao.UserRepository;
import com.github.ovorobeva.dto.BlogDto;
import com.github.ovorobeva.model.Blog;
import com.github.ovorobeva.model.CustomUser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import com.github.ovorobeva.dao.BlogRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = MainApplicationClass.class)

public class BlogControllerIntegrationTest {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mvc;

    @Before
    public void setup(){
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

/*     @After
    public void resetDb(){
        blogRepository.deleteAll();
        blogRepository.flush();
    }
   @Before
    public void signIn(){
        Map<String, String> body = new HashMap<>();
        body.put("username","admin");
        body.put("password", "admin");
        testRestTemplate.postForObject("/login", body, body.getClass());
    }*/

    @Test
    @WithMockUser(value = "admin", password = "admin")
    public void create_testStatus201(){
        Blog blog = new Blog();
        blog.setTitle("Test title");
        blog.setContent("Test Content");

        ResponseEntity<BlogDto> response = testRestTemplate.postForEntity("/blog/create",blog, BlogDto.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(response.getBody().getId(),notNullValue());
        assertThat(response.getBody().getTitle(), is("Test title"));
    }

    @Test
    public void getBlog(){
        int id = createBlog().getId();
        ResponseEntity<BlogDto> response = testRestTemplate.getForEntity("/blog/{id}", BlogDto.class, id);
        assertThat(response.getBody().getId(), is(id));
        assertThat(response.getBody().getTitle(), is("New test title for test blog"));
        assertThat(response.getBody().getContent(), is("New test content for test blog"));
    }

    private Blog createBlog(){
        Blog blog = new Blog();
        blog.setTitle("New test title for test blog");
        blog.setContent("New test content for test blog");
        return blogRepository.saveAndFlush(blog);
    }

}
