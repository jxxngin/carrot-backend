package com.example.carrot.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class ProductEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private int price;

	@Column(nullable = false)
	private String location;

	protected ProductEntity() {
	}

	public ProductEntity(String title, int price, String location) {
		this.title = title;
		this.price = price;
		this.location = location;
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public int getPrice() {
		return price;
	}

	public String getLocation() {
		return location;
	}

	public void update(String title, int price, String location) {
		this.title = title;
		this.price = price;
		this.location = location;
	}
}
