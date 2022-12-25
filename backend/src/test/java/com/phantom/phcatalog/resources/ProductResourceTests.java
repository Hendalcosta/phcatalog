package com.phantom.phcatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.phantom.phcatalog.dto.ProductDTO;
import com.phantom.phcatalog.services.ProductService;
import com.phantom.phcatalog.services.exceptions.ResourceNotFoundException;
import com.phantom.phcatalog.tests.Factory;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ProductService service;
	
	private ProductDTO productDTO;
	private PageImpl<ProductDTO> page;
	private long targetId;
	private long nonExistingTargetId;
	
	@BeforeEach
	void setUp() {
		
		targetId = 1L;
		nonExistingTargetId = 2L;	
		productDTO = Factory.createProductDTO();
		page = new PageImpl<>(List.of(productDTO));
		
		when(service.findAllPaged(any())).thenReturn(page);
		when(service.findById(targetId)).thenReturn(productDTO);
		when(service.findById(nonExistingTargetId)).thenThrow(ResourceNotFoundException.class);
		
	}
	
	@Test
	public void findAllShouldReturnPage() throws Exception{
		
		ResultActions result = mockMvc.perform(get("/products")
				.accept(MediaType.APPLICATION_JSON));	
		
		result.andExpect(status().isOk());
	}
	
	@Test
	public void findByIdShouldReturnProductWhenIdIsValid() throws Exception{
		
		ResultActions result = mockMvc.perform(get("/products/{id}", targetId)
				.accept(MediaType.APPLICATION_JSON));	
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
		
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdIsNotValid() throws Exception {
		
		ResultActions result = mockMvc.perform(get("/products/{id}", nonExistingTargetId)
				.accept(MediaType.APPLICATION_JSON));	
		
		result.andExpect(status().isNotFound());
		
	}

}
