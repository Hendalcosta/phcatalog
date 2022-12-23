package com.phantom.phcatalog.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.phantom.phcatalog.entities.Product;
import com.phantom.phcatalog.repositories.ProductRepository;
import com.phantom.phcatalog.services.exceptions.DatabaseException;
import com.phantom.phcatalog.services.exceptions.ResourceNotFoundException;
import com.phantom.phcatalog.tests.Factory;

@ExtendWith(SpringExtension.class) 
public class ProductServiceTests {
	
	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	private long targetId;
	private long nonExistingTargetId;
	private long dependentId;
	private PageImpl<Product> page;
	private Product product;
	
	@BeforeEach
	void setUp() throws Exception {
		
		targetId = 1L;
		nonExistingTargetId = 1000L;
		dependentId = 4L;
		product = Factory.createProduct();
		page = new PageImpl<>(List.of(product));
		
		Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		Mockito.when(repository.findById(targetId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistingTargetId)).thenReturn(Optional.empty());
		
		Mockito.doNothing().when(repository).deleteById(targetId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingTargetId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId () {
		
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists () {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingTargetId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingTargetId);
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists () {
		
		Assertions.assertDoesNotThrow(() -> {
			service.delete(targetId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(targetId);		
	}

}
