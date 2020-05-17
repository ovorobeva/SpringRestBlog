package com.github.ovorobeva;

import com.github.ovorobevablog.Blog;
import com.github.ovorobevablog.BlogMockedData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BlogController {

/*    @RequestMapping("/")
    public String index() {
        return "Congratulations from BlogController.java";
    }*/
    @GetMapping("/blog")
    public List<Blog> getBlogs(){
        BlogMockedData blogMockedData = BlogMockedData.getInstance();
        return blogMockedData.fetchBlogs();
    }

}

