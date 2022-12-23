package com.phantom.phcatalog.services;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
		
		doNothing().when(repository).deleteById(targetId);
		doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingTargetId);
	}
	
	
	
	@Test
	public void deleteShouldDoNothingWhenIdExists () {
		
		Assertions.assertDoesNotThrow(() -> {
			service.delete(targetId);
		});
		
		verify(repository, times(1)).deleteById(targetId);
		
	}

}
