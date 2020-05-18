package com.github.ovorobeva.database;

import com.github.ovorobeva.blog.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Integer> {
    List<Blog> findByTitleContainsOrContentContains(String text, String textAgain);
}
