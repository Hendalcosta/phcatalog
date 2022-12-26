package com.phantom.phcatalog.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.phantom.phcatalog.dto.ProductDTO;
import com.phantom.phcatalog.repositories.ProductRepository;
import com.phantom.phcatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
@Transactional
public class ProductServiceIntegrationTests {

	@Autowired
	private ProductService service;
	
	@Autowired
	private ProductRepository repository;
	
	private long targetId;
	private long nonExistingTargetId;
	private long countTotalProducts;
	
	@BeforeEach
	void setUp() {
		targetId = 1L;
		nonExistingTargetId =1000L;
		countTotalProducts = 25l;
	}
	
	@Test
	public void deleteShouldEraseResourceWhenIdIsValid() {
		
		service.delete(targetId);
		
		Assertions.assertEquals(countTotalProducts -1, repository.count());
	}
	
	@Test
	public void deleteShouldTjrowsResourceNotFoundExceptionWhenIdIsValid() {
	
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingTargetId);
		});
	}
	
	@Test
	public void findAllPagedShouldReturnPageWhenPage0size10() {
		PageRequest pageRequest = PageRequest.of(0, 10);
		
		Page<ProductDTO> result = service.findAllPaged(pageRequest);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(0, result.getNumber());
		Assertions.assertEquals(10, result.getSize());
		Assertions.assertEquals(countTotalProducts, result.getTotalElements());
	}
	
	@Test
	public void findAllPagedShouldReturnEmptyPageWhenPageDoesNotExists() {
		PageRequest pageRequest = PageRequest.of(100, 10);
		
		Page<ProductDTO> result = service.findAllPaged(pageRequest);
		
		Assertions.assertTrue(result.isEmpty());
	}
	
	@Test
	public void findAllPagedShouldReturnSortedPageWhenPageSortByName() {
		PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));
		
		Page<ProductDTO> result = service.findAllPaged(pageRequest);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName());
		Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
		Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
	}
	
	
}
