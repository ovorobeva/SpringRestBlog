package com.github.ovorobeva.dao;

import com.github.ovorobeva.model.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Integer> {
    List<Blog> findByTitleContainsOrContentContains(String toSearchInTitle, String toSearchInContent);
}
