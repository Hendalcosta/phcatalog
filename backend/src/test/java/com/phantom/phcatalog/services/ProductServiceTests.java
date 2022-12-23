package com.phantom.phcatalog.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.phantom.phcatalog.repositories.ProductRepository;

@ExtendWith(SpringExtension.class) 
public class ProductServiceTests {
	
	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	private long targetId;
	private long nonExistingTargetId;
	
	@BeforeEach
	void setUp() throws Exception {
		
		targetId = 1L;
		nonExistingTargetId = 1000L;
		
		Mockito.doNothing().when(repository).deleteById(targetId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingTargetId);
	}
	
	
	
	@Test
	public void deleteShouldDoNothingWhenIdExists () {
		
		Assertions.assertDoesNotThrow(() -> {
			service.delete(targetId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(targetId);
		
	}

}
