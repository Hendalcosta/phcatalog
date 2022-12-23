package com.phantom.phcatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.phantom.phcatalog.entities.Product;
import com.phantom.phcatalog.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {

	@Autowired
	private ProductRepository repository;
	
	private long targetId;
	private long noExistingTargetId;
	private long countTotalProducts;
	
	@BeforeEach
	void setup() throws Exception{
		targetId = 1L;
		noExistingTargetId = 1000L;
		countTotalProducts = 25L;
	}
	
	
	@Test
	public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {
		
		Product product = Factory.createProduct();
		product.setId(null);
		
		product = repository.save(product);
		
		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countTotalProducts +1, product.getId());
	}
	
	
	@Test
	public void deleteShouldEraseObjectWhenIdExists() {

		repository.deleteById(targetId);

		Optional<Product> result = repository.findById(targetId);
		Assertions.assertFalse(result.isPresent());

	}

	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdNotExists() {
		
		Assertions.assertThrows(EmptyResultDataAccessException.class, () ->{
			repository.deleteById(noExistingTargetId);
		});
	}
	
	@Test
	public void findByIDShouldReturnOptionalNotNullWhenIdIsValid() {
		
		Optional<Product> result = repository.findById(targetId);
		
		
		Assertions.assertTrue(result.isPresent());
		
	}
	
	@Test
	public void findByIDShouldReturnEmptyOptionalNotNullWhenIdIsNotValid() {
		
		Optional<Product> result = repository.findById(noExistingTargetId);
		
		Assertions.assertFalse(result.isPresent());
		//Assertions.assertTrue(result.isEmpty());
	}

}
