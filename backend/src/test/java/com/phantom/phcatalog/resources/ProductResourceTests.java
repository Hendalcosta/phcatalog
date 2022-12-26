package com.phantom.phcatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phantom.phcatalog.dto.ProductDTO;
import com.phantom.phcatalog.services.ProductService;
import com.phantom.phcatalog.services.exceptions.DatabaseException;
import com.phantom.phcatalog.services.exceptions.ResourceNotFoundException;
import com.phantom.phcatalog.tests.Factory;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ProductService service;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private ProductDTO productDTO;
	private PageImpl<ProductDTO> page;
	private long targetId;
	private long nonExistingTargetId;
	private long dependentId;
	
	@BeforeEach
	void setUp() {
		
		targetId = 1L;
		nonExistingTargetId = 2L;
		dependentId =3L;
		productDTO = Factory.createProductDTO();
		page = new PageImpl<>(List.of(productDTO));
		
		when(service.findAllPaged(any())).thenReturn(page);
		when(service.findById(targetId)).thenReturn(productDTO);
		when(service.findById(nonExistingTargetId)).thenThrow(ResourceNotFoundException.class);
		
		when(service.insert(any())).thenReturn(productDTO);
		
		when(service.update(eq(targetId), any())).thenReturn(productDTO);
		when(service.update(eq(nonExistingTargetId), any())).thenThrow(ResourceNotFoundException.class);
		
		doNothing().when(service).delete(targetId);
		doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingTargetId);
		doThrow(DatabaseException.class).when(service).delete(dependentId);
	}

	@Test
	public void deleteShouldReturnNoContentWhenIdIsValid() throws Exception {
		
		ResultActions result = mockMvc.perform(delete("/products/{id}", targetId)
				.accept(MediaType.APPLICATION_JSON));	
		
		result.andExpect(status().isNoContent());
	}
	
	@Test
	public void deleteShouldReturnNotFoundWhenIdIsNotValid() throws Exception {
		
		ResultActions result = mockMvc.perform(get("/products/{id}", nonExistingTargetId)
				.accept(MediaType.APPLICATION_JSON));	
		
		result.andExpect(status().isNotFound());
	}
	
	
	
	@Test
	public void insertShouldReturnCreatedProductDTO() throws Exception{
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = mockMvc.perform(post("/products")
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));	
		
		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
		
	}
	
	@Test
	public void updateShouldReturnProductWhenIdIsValid() throws Exception{
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = mockMvc.perform(put("/products/{id}", targetId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));	
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
		
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenIdIsValid() throws Exception{
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = mockMvc.perform(put("/products/{id}", nonExistingTargetId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));	
		
		result.andExpect(status().isNotFound());
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

