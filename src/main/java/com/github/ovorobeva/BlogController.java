package com.github.ovorobeva;

import com.github.ovorobevablog.Blog;
import com.github.ovorobevablog.BlogMockedData;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class BlogController {

/*    @RequestMapping("/")
    public String index() {
        return "Congratulations from BlogController.java";
    }*/
    BlogMockedData blogMockedData = BlogMockedData.getInstance();

    @GetMapping("/blog")
    public List<Blog> getBlogs(){
        return blogMockedData.fetchBlogs();
    }
    @GetMapping("/blog/{id}")
    public Blog getBlogs(@PathVariable String id){
        return blogMockedData.getBlogById(Integer.parseInt(id));
    }

    @PostMapping("blog/search")
    public List<Blog> search(@RequestBody Map<String, String> body){
        String searchItem = body.get("text");
        return blogMockedData.searchBlogs(searchItem);
    }

    @PostMapping("blog")
    public Blog create(@RequestBody Map<String, String> body){
        int id = Integer.parseInt(body.get("id"));
        String title = body.get("title");
        String content = body.get("content");
        return blogMockedData.createBlog(id, title, content);
    }

    @PutMapping("blog/{id}")
    public Blog updateBlog(@PathVariable String id, @RequestParam String title, String content){
        return blogMockedData.updateBlog(Integer.parseInt(id), title, content);
    }

    @DeleteMapping("blog/{id}")
    public boolean delete(@PathVariable String id){
        return blogMockedData.deleteBlog(Integer.parseInt(id));
    }

}

