package com.phantom.phcatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.phantom.phcatalog.entities.Product;

@DataJpaTest
public class ProductRepositoryTests {

	@Autowired
	private ProductRepository repository;

	@Test
	public void deleteShouldEraseObjectWhenIdExists() {

		long targetId = 1L;

		repository.deleteById(targetId);

		Optional<Product> result = repository.findById(targetId);
		Assertions.assertFalse(result.isPresent());

	}

	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdNotExists() {
		long targetId = 1000L;
		
		Assertions.assertThrows(EmptyResultDataAccessException.class, () ->{
			repository.deleteById(targetId);
		});
	}

}
