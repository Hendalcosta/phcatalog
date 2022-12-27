package com.phantom.phcatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phantom.phcatalog.dto.ProductDTO;
import com.phantom.phcatalog.tests.Factory;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIntegrationTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
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
	public void findAllShouldReturnSortedPageWhenSortByName() throws Exception {
		ResultActions result = mockMvc.perform(get("/products?page=0&size=12&sort=name,asc")
				.accept(MediaType.APPLICATION_JSON));	
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.totalElements").value(countTotalProducts));
		result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
		result.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
		result.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));		
	}
	
	@Test
	public void updateShouldReturnProductWhenIdIsValid() throws Exception{
		
		ProductDTO productDTO = Factory.createProductDTO();
		
		String existingName = productDTO.getName();
		String existingDescription = productDTO.getDescription();
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = mockMvc.perform(put("/products/{id}", targetId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));	
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").value(targetId));
		result.andExpect(jsonPath("$.name").value(existingName));
		result.andExpect(jsonPath("$.description").value(existingDescription));
		
	}
	
	@Test
	public void updateShouldReturnNotFindWhenIdIsNotValid() throws Exception{
		
		ProductDTO productDTO = Factory.createProductDTO();
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = mockMvc.perform(put("/products/{id}", nonExistingTargetId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));	
		
		result.andExpect(status().isNotFound());
	
		
	}
}
