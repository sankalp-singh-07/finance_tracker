package com.finance.backend.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.finance.backend.category.model.Category;
import com.finance.backend.category.repository.CategoryRepository;
import com.finance.backend.common.enums.TransactionType;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedDefaultCategories(CategoryRepository categoryRepository) {
        return args -> {
            if (categoryRepository.countByDefaultCategoryTrue() > 0) {
                return;
            }

            List<Category> defaultCategories = List.of(
                    Category.builder().name("Salary").type(TransactionType.INCOME).defaultCategory(true).build(),
                    Category.builder().name("Freelance").type(TransactionType.INCOME).defaultCategory(true).build(),
                    Category.builder().name("Investments").type(TransactionType.INCOME).defaultCategory(true).build(),
                    Category.builder().name("Food").type(TransactionType.EXPENSE).defaultCategory(true).build(),
                    Category.builder().name("Rent").type(TransactionType.EXPENSE).defaultCategory(true).build(),
                    Category.builder().name("Utilities").type(TransactionType.EXPENSE).defaultCategory(true).build(),
                    Category.builder().name("Transport").type(TransactionType.EXPENSE).defaultCategory(true).build(),
                    Category.builder().name("Health").type(TransactionType.EXPENSE).defaultCategory(true).build());

            categoryRepository.saveAll(defaultCategories);
        };
    }
}
