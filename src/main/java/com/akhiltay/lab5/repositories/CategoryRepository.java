package com.akhiltay.lab5.repositories;

import com.akhiltay.lab5.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
