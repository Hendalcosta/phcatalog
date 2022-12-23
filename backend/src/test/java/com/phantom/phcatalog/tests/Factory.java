package com.phantom.phcatalog.tests;

import java.time.Instant;

import com.phantom.phcatalog.dto.ProductDTO;
import com.phantom.phcatalog.entities.Category;
import com.phantom.phcatalog.entities.Product;

public class Factory {
	
	public static Product createProduct() {
		Product product = new Product(1L, "SmartPhone", "Cool Phone", 1000.0, "https://img.com/img.png", Instant.parse("2022-12-23T11:00:00Z"));
		
		product.getCategories().add(new Category (2L, "Electronics"));
		return product;
	}
	
	public static ProductDTO createProductDTO () {
		Product product = createProduct();
		return new ProductDTO(product, product.getCategories());
	}

}
