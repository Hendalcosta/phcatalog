package com.phantom.phcatalog.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.phantom.phcatalog.dto.ProductDTO;
import com.phantom.phcatalog.entities.Category;
import com.phantom.phcatalog.entities.Product;
import com.phantom.phcatalog.repositories.CategoryRepository;
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
	
	@Mock
	private CategoryRepository categoryRepository;
	
	private long targetId;
	private long nonExistingTargetId;
	private long dependentId;
	private PageImpl<Product> page;
	private Product product;
	private ProductDTO productDTO;
	private Category category;
	
	@BeforeEach
	void setUp() throws Exception {
		
		targetId = 1L;
		nonExistingTargetId = 2L;
		dependentId = 3L;
		product = Factory.createProduct();
		productDTO = Factory.createProductDTO();
		category = Factory.createCategory();
		page = new PageImpl<>(List.of(product));
		
		//non void methods
		
		Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		Mockito.when(repository.findById(targetId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistingTargetId)).thenReturn(Optional.empty());
		
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		
		Mockito.when(repository.getOne(targetId)).thenReturn(product);
		Mockito.when(repository.getOne(nonExistingTargetId)).thenThrow(EntityNotFoundException.class);
		
		Mockito.when(categoryRepository.getOne(targetId)).thenReturn(category);
		Mockito.when(categoryRepository.getOne(nonExistingTargetId)).thenThrow(EntityNotFoundException.class);
		
		//void methods
		Mockito.doNothing().when(repository).deleteById(targetId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingTargetId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
		Mockito.doThrow(ResourceNotFoundException.class).when(repository).findById(nonExistingTargetId);
	}

	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists () {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingTargetId, productDTO);
		});
		
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdIsValid() {
		
		ProductDTO result = service.update(targetId, productDTO);
			
		Assertions.assertNotNull(result);
	}
	
	@Test
	public void findByIDShouldReturnProductDTOWhenIdIsValid() {
		
		ProductDTO result = service.findById(targetId);
			
		Assertions.assertNotNull(result);
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists () {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingTargetId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).findById(nonExistingTargetId);
	}
	
	@Test
	public void findAllPagedShouldReturnPage() {
		Pageable pageable = PageRequest.of(0, 10);
		
		Page<ProductDTO> result = service.findAllPaged(pageable);
		
		Assertions.assertNotNull(result);
		Mockito.verify(repository).findAll(pageable);
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
