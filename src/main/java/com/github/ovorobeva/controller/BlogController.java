package com.github.ovorobeva.controller;

import com.github.ovorobeva.dao.UserRepository;
import com.github.ovorobeva.dto.BlogDto;
import com.github.ovorobeva.model.Blog;
import com.github.ovorobeva.dao.BlogRepository;
import com.github.ovorobeva.model.CustomUser;
import io.swagger.annotations.Api;
import org.modelmapper.ModelMapper;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Api(value = "DemoBlogController", description = "REST APIs related to Blog Entity")
@RestController
public class BlogController {

    @Autowired
    BlogRepository blogRepository;
    @Autowired
    UserRepository userRepository;

    ModelMapper mapper = new ModelMapper();

    @GetMapping("/blog")
    public ResponseEntity<List<BlogDto>> getBlogs() throws EntityNotFoundException {

        List<BlogDto> blogs = blogRepository.findAll()
                .stream()
                .map(element -> mapper.map(element, BlogDto.class))
                .collect(Collectors.toList());
        if (blogs.isEmpty()) throw new EntityNotFoundException("The bloglist is empty");
        return ResponseEntity.ok().body(blogs);
    }


    @GetMapping("/blog/{id}")
    public ResponseEntity<BlogDto> getBlog(@PathVariable int id) throws EntityNotFoundException {
        if (!blogRepository.findById(id).isPresent())
            throw new EntityNotFoundException("The blog with ID = " + id + " doesn't exist");
        return ResponseEntity.ok().body(mapper.map(blogRepository.getOne(id), BlogDto.class));
    }

    @GetMapping("/blog/search/{searchItem}")
    public ResponseEntity<List<BlogDto>> search(@PathVariable String searchItem) throws TypeMismatchException {
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

    @PreAuthorize("principal.username.equals(blogRepository.getOne(#id).getUser().getUsername()) ")
    @PutMapping("/blog/{id}")
    public ResponseEntity<BlogDto> updateBlog(@PathVariable int id, @Valid @RequestBody Blog blog) throws EntityNotFoundException {

        if (!blogRepository.findById(id).isPresent())
            throw new EntityNotFoundException("The blog with ID = " + id + " doesn't exist");
        Blog blogToUpdate = blogRepository.getOne(id);
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
    //TODO: documentation
    //TODO: primitive UI
    //TODO: tests

}

