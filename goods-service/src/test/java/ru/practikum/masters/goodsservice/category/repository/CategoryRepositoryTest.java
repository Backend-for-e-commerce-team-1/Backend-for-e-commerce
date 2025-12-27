package ru.practikum.masters.goodsservice.category.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practikum.masters.goodsservice.category.model.Category;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@ActiveProfiles("local")
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void save_shouldBeSaveCategory() {

        Category category = Category.createWithName("Electronics", "Gadgets and devices");

        Category saved = categoryRepository.save(category);

        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void findById_shouldBeFoundCategory() {

        Category category = Category.createWithName("Books", "Literature and textbooks");

        Category saved = categoryRepository.save(category);

        Optional<Category> found = categoryRepository.findById(saved.getId());

        assertThat(found).isPresent();

        Category retrieved = found.get();
        assertThat(retrieved.getName()).isEqualTo("Books");
        assertThat(retrieved.getDescription()).isEqualTo("Literature and textbooks");
        assertThat(retrieved.getId()).isEqualTo(saved.getId());
    }

    @Test
    void findByNameIgnoreCase_shouldBeFoundCategory() {

        Category category = Category.createWithName("Books", "Literature and textbooks");

        Category saved = categoryRepository.save(category);

        Optional<Category> found = categoryRepository.findByNameIgnoreCase(saved.getName());

        assertThat(found).isPresent();

        Category retrieved = found.get();
        assertThat(retrieved.getName()).isEqualTo("Books");
        assertThat(retrieved.getDescription()).isEqualTo("Literature and textbooks");
        assertThat(retrieved.getId()).isEqualTo(saved.getId());
    }

    @Test
    void existsByNameIgnoreCase_shouldBeAnswerExistCategory() {

        Category category = Category.createWithName("Books", "Literature and textbooks");

        Category saved = categoryRepository.save(category);

        Boolean found = categoryRepository.existsByNameIgnoreCase(saved.getName());

        assertThat(found).isTrue();
    }

    @Test
    void shouldReturnEmptyWhenNameNotFound() {

        Optional<Category> found = categoryRepository.findByNameIgnoreCase("NonExistingCategory");

        assertThat(found).isEmpty();
    }

    @Test
    void shouldDeleteCategory() {

        Category category = Category.createWithName("Music", "Instruments and albums");
        Category saved = categoryRepository.save(category);

        UUID id = saved.getId();

        categoryRepository.deleteById(id);

        Boolean exists = categoryRepository.existsById(id);
        assertThat(exists).isFalse();
    }

    @Test
    void shouldCheckIfCategoryExistsById() {

        Category category = Category.createWithName("Sports", "Equipment and clothing");
        Category saved = categoryRepository.save(category);

        Boolean exists = categoryRepository.existsById(saved.getId());
        assertThat(exists).isTrue();

        Boolean notExists = categoryRepository.existsById(UUID.randomUUID());
        assertThat(notExists).isFalse();
    }
}
