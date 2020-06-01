package com.github.ovorobeva.controller;

import com.github.ovorobeva.dao.UserRepository;
import com.github.ovorobeva.model.Blog;
import com.github.ovorobeva.dao.BlogRepository;
import com.github.ovorobeva.model.CustomUser;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class BlogController {

    @Autowired
    BlogRepository blogRepository;
    @Autowired
    UserRepository userRepository;


    @GetMapping("/blog")
    public ResponseEntity<List<Blog>> getBlogs() throws EntityNotFoundException {
        List<Blog> blogs = blogRepository.findAll();
        if (blogs.isEmpty()) throw new EntityNotFoundException("The bloglist is empty");
        return ResponseEntity.ok().body(blogs);
    }

    @GetMapping("/blog/{id}")
    public ResponseEntity<Object> getBlog(@PathVariable int id) throws EntityNotFoundException {
        if (!blogRepository.findById(id).isPresent())
            throw new EntityNotFoundException("The blog with ID = " + id + " doesn't exist");
        return ResponseEntity.ok().body(blogRepository.getOne(id));
    }

    @GetMapping("/blog/search/{searchItem}")
    public ResponseEntity<List<Blog>> search(@PathVariable String searchItem) throws TypeMismatchException {
        List<Blog> blogList = blogRepository.findByTitleContainsOrContentContains(searchItem, searchItem);
        return ResponseEntity.ok().body(blogList);
    }

    @PostMapping("/blog/create")
    public ResponseEntity<Blog> create(@Valid @RequestBody Blog blog, Principal principal) {
        blog.setUser(userRepository.findByUsername(principal.getName()));
        return ResponseEntity.status(201).body(blogRepository.save(blog));
    }

    @PutMapping("/blog/{id}")
    public ResponseEntity<Blog> updateBlog(@PathVariable int id, @Valid @RequestBody Blog blog) throws EntityNotFoundException {
        if (!blogRepository.findById(id).isPresent())
            throw new EntityNotFoundException("The blog with ID = " + id + " doesn't exist");
        //TODO: use DTO instead of custom query
        Blog blogToUpdate = (Blog) blogRepository.getOne(id);
        blogToUpdate.setTitle(blog.getTitle());
        blogToUpdate.setContent(blog.getContent());
        blogRepository.save(blogToUpdate);
        return ResponseEntity.ok().body(blogToUpdate);
    }

    @DeleteMapping("/blog/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable int id) throws EntityNotFoundException {
        Optional<Blog> blog = blogRepository.findById(id);
        if (!blog.isPresent())
            throw new EntityNotFoundException("Nothing to delete: id = " + id);
        blogRepository.deleteById(id);

        return ResponseEntity.ok().body(true);
    }
    @PostMapping("/user/create")
    public ResponseEntity<CustomUser> createUser(@Valid @RequestBody Map<String, String> body){
        CustomUser newUser = new CustomUser();
        newUser.setUsername(body.get("login"));
        newUser.setPassword(body.get("password"));
        newUser.setRole("USER");
        return ResponseEntity.status(201).body(userRepository.save(newUser));
    }

}

