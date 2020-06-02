package com.github.ovorobeva.controller;

import com.github.ovorobeva.dao.UserRepository;
import com.github.ovorobeva.dto.BlogDto;
import com.github.ovorobeva.model.Blog;
import com.github.ovorobeva.dao.BlogRepository;
import com.github.ovorobeva.model.CustomUser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class BlogController {

    @Autowired
    BlogRepository blogRepository;
    @Autowired
    UserRepository userRepository;


    @GetMapping("/blog")
    public ResponseEntity<List<BlogDto>> getBlogs() throws EntityNotFoundException {
        ModelMapper mapper = new ModelMapper();

        List<BlogDto> blogs = blogRepository.findAll()
                .stream()
                .map(element -> mapper.map(element, BlogDto.class))
                .collect(Collectors.toList());
        if (blogs.isEmpty()) throw new EntityNotFoundException("The bloglist is empty");
        return ResponseEntity.ok().body(blogs);
    }

    @GetMapping("/blog/{id}")
    public ResponseEntity<BlogDto> getBlog(@PathVariable int id) throws EntityNotFoundException {
        ModelMapper mapper = new ModelMapper();
        if (!blogRepository.findById(id).isPresent())
            throw new EntityNotFoundException("The blog with ID = " + id + " doesn't exist");
        return ResponseEntity.ok().body(mapper.map(blogRepository.getOne(id), BlogDto.class));
    }

    @GetMapping("/blog/search/{searchItem}")
    public ResponseEntity<List<BlogDto>> search(@PathVariable String searchItem) throws TypeMismatchException {
        ModelMapper mapper = new ModelMapper();
        List<BlogDto> blogList = blogRepository.findByTitleContainsOrContentContains(searchItem, searchItem)
                .stream()
                .map(element -> mapper.map(element, BlogDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(blogList);
    }

    @PostMapping("/blog/create")
    public ResponseEntity<BlogDto> create(@Valid @RequestBody Blog blog, Principal principal) {
        ModelMapper mapper = new ModelMapper();
        blog.setUser(userRepository.findByUsername(principal.getName()));
        return ResponseEntity.status(201).body(mapper.map(blogRepository.save(blog), BlogDto.class));
    }

    @PutMapping("/blog/{id}")
    public ResponseEntity<BlogDto> updateBlog(@PathVariable int id, @Valid @RequestBody Blog blog) throws EntityNotFoundException {
        ModelMapper mapper = new ModelMapper();

        if (!blogRepository.findById(id).isPresent())
            throw new EntityNotFoundException("The blog with ID = " + id + " doesn't exist");
        //TODO: use DTO instead of custom query
        Blog blogToUpdate = (Blog) blogRepository.getOne(id);
        blogToUpdate.setTitle(blog.getTitle());
        blogToUpdate.setContent(blog.getContent());
        blogRepository.save(blogToUpdate);
        return ResponseEntity.ok().body(mapper.map(blogToUpdate, BlogDto.class));
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
    //TODO: DTO
    //TODO: documentation
    //TODO: primitive UI
    //TODO: tests

}

