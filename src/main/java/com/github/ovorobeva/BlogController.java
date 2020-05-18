package com.github.ovorobeva;

import com.github.ovorobeva.blog.Blog;
import com.github.ovorobeva.database.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class BlogController {

/*    @RequestMapping("/")
    public String index() {
        return "Congratulations from BlogController.java";
    }*/
    @Autowired
    BlogRepository blogRepository;


    @GetMapping("/blog")
    public List<Blog> getBlogs(){
        return blogRepository.findAll();
    }
/*    @GetMapping("/blog/{id}")
    public Optional<Blog> getBlog(@PathVariable String id){
        return blogRepository.findById(Integer.parseInt(id));
    }*/
    //TODO: to fix getting one blog by its id with the getOne method
    @GetMapping("/blog/{id}")
    public Blog getBlog(@PathVariable String id){
        return blogRepository.getOne(Integer.parseInt(id));
    }

    @PostMapping("/blog/search")
    public List<Blog> search(@RequestBody Map<String, String> body){
        String searchItem = body.get("text");
        return blogRepository.findByTitleContainsOrContentContains(searchItem, searchItem);
    }

    @PostMapping("/blog")
    public Blog create(@RequestBody Map<String, String> body){
        String title = body.get("title");
        String content = body.get("content");
        return blogRepository.save(new Blog(title, content));
    }

    @PutMapping("/blog/{id}")
    public Blog updateBlog(@PathVariable String id, @RequestParam String title, String content){
        Blog blogToUpdate = blogRepository.getOne(Integer.parseInt(id));
        blogToUpdate.setTitle(title);
        blogToUpdate.setContent(content);
        return blogRepository.save(blogToUpdate);
    }

    @DeleteMapping("/blog/{id}")
    public boolean delete(@PathVariable String id){
        blogRepository.deleteById(Integer.parseInt(id));
        return true;
    }

}

