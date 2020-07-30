package controllerstests;

import com.github.ovorobeva.MainApplicationClass;
import com.github.ovorobeva.dao.BlogRepository;
import com.github.ovorobeva.dto.BlogDto;
import com.github.ovorobeva.model.Blog;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = MainApplicationClass.class)

public class BlogControllerIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private BlogRepository blogRepository;

    //ToDo: create a test DB
    @Test
    public void getBlogsTest() {
        createBlog("The first blog for test");
        int id = createBlog("The second blog for test").getId();
        ResponseEntity<List<BlogDto>> response = testRestTemplate.exchange("/blog",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<BlogDto>>() {
                });
        assertThat(response.getBody().size(), is(2));
        assertThat(response.getBody().get(1).getTitle(), is("The second blog for test"));
        assertThat(response.getBody().get(1).getId(), is(id));
    }

    @Test
    public void searchByTitleTwoBlogsTest_manyResults() {
        createBlog("A title for the first blog for test");
        createBlog("A title for the second blog for test");
        createBlog("A title for the third blog for test");
        createBlog("The fourth blog for test");

        ResponseEntity<List<BlogDto>> response = testRestTemplate.exchange("/blog/search/{searchItem}",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<BlogDto>>() {
                }, "title");
        assertThat(response.getBody().size(), is(3));
        assertThat(response.getBody().get(1).getTitle(), is("A title for the second blog for test"));
    }
    @Test
    public void searchByTitleBlogsTest_oneResult() {
        createBlog("A title for the first blog for test");
        createBlog("A title for the second blog for test");
        createBlog("A title for the third blog for test");
        createBlog("The fourth blog for test");

        ResponseEntity<List<BlogDto>> response = testRestTemplate.exchange("/blog/search/{searchItem}",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<BlogDto>>() {
                }, "fourth");
        assertThat(response.getBody().size(), is(1));
        assertThat(response.getBody().get(0).getTitle(), is("The fourth blog for test"));
    }

    @Test
    public void searchByContentTwoBlogsTest_manyResults() {
        createDefinedContentBlog("A content for the first blog for test");
        createDefinedContentBlog("A content for the second blog for test");
        createDefinedContentBlog("A content for the third blog for test");
        createDefinedContentBlog("The fourth blog for test");

        ResponseEntity<List<BlogDto>> response = testRestTemplate.exchange("/blog/search/{searchItem}",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<BlogDto>>() {
                }, "content");
        assertThat(response.getBody().size(), is(3));
        assertThat(response.getBody().get(1).getContent(), is("A content for the second blog for test"));
    }
    @Test
    public void searchByContentBlogsTest_oneResult() {
        createDefinedContentBlog("A content for the first blog for test");
        createDefinedContentBlog("A content for the second blog for test");
        createDefinedContentBlog("A content for the third blog for test");
        createDefinedContentBlog("The fourth blog for test");

        ResponseEntity<List<BlogDto>> response = testRestTemplate.exchange("/blog/search/{searchItem}",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<BlogDto>>() {
                }, "fourth");
        assertThat(response.getBody().size(), is(1));
        assertThat(response.getBody().get(0).getContent(), is("The fourth blog for test"));
    }

    @Test
    public void searchByContentAndTitleBlogsTest() {
        createDefinedContentBlog("A content for the first blog for testing");
        createDefinedContentBlog("A content for the second blog for testing");
        createBlog("A title for the third blog for testing");
        createBlog("The fourth test blog");

        ResponseEntity<List<BlogDto>> response = testRestTemplate.exchange("/blog/search/{searchItem}",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<BlogDto>>() {
                }, "testing");
        assertThat(response.getBody().size(), is(3));
        assertThat(response.getBody().get(0).getContent(), is("A content for the first blog for testing"));
        assertThat(response.getBody().get(1).getContent(), is("A content for the second blog for testing"));
        assertThat(response.getBody().get(2).getContent(), is("New test content for test blog"));
    }

    @Test
    public void getBlogTest() {
        int id = createBlog().getId();
        ResponseEntity<BlogDto> response = testRestTemplate.getForEntity("/blog/{id}", BlogDto.class, id);
        assertThat(response.getBody().getId(), is(id));
        assertThat(response.getBody().getTitle(), is("New test title for test blog"));
        assertThat(response.getBody().getContent(), is("New test content for test blog"));
    }

    @Test
    public void createBlog_testStatus201() {
        Blog blog = new Blog();
        blog.setTitle("Test title");
        blog.setContent("Test Content");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", getCookieForUser("user", "user", "/login"));

        ResponseEntity<BlogDto> response = testRestTemplate.exchange("/blog/create", HttpMethod.POST,
                new HttpEntity<>(blog, headers),
                BlogDto.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(response.getBody().getId(), notNullValue());
        assertThat(response.getBody().getTitle(), is("Test title"));
    }


    @Test
    public void deleteBlogTest() {
        int id = createBlog().getId();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", getCookieForUser("user", "user", "/login"));

        testRestTemplate.exchange("/blog/{id}",
                HttpMethod.DELETE,
                new HttpEntity<>(null, headers),
                Boolean.class,
                id);

        assertThat(blogRepository.findById(id), is(Optional.empty()));
    }

    private Blog createBlog() {
        Blog blog = new Blog();
        blog.setTitle("New test title for test blog");
        blog.setContent("New test content for test blog");
        return blogRepository.saveAndFlush(blog);
    }

    private Blog createBlog(String title) {
        Blog blog = new Blog();
        blog.setTitle(title);
        blog.setContent("New test content for test blog");
        return blogRepository.saveAndFlush(blog);
    }

    private Blog createDefinedContentBlog(String content) {
        Blog blog = new Blog();
        blog.setTitle("New title for a new blog");
        blog.setContent(content);
        return blogRepository.saveAndFlush(blog);
    }

    private String getCookieForUser(String username, String password, String loginUrl) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.set("username", username);
        form.set("password", password);
        ResponseEntity<String> loginResponse = testRestTemplate.postForEntity(
                loginUrl,
                new HttpEntity<>(form, new HttpHeaders()),
                String.class);
        String cookie = loginResponse.getHeaders().get("Set-Cookie").get(0);
        return cookie;
    }

}
