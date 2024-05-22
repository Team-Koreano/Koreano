package org.ecommerce.product.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.ecommerce.product.entity.enumerated.Acidity;
import org.ecommerce.product.entity.enumerated.Bean;
import org.ecommerce.product.entity.enumerated.ProductCategory;
import org.ecommerce.product.entity.enumerated.ProductStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Integer id;

	@Column(name = "category", nullable = false)
	@Enumerated(EnumType.STRING)
	private ProductCategory category;

	@Column(nullable = false)
	private Integer price;

	@Column(nullable = false)
	private Integer stock;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id", nullable = false)
	private SellerRep sellerRep;

	@Column()
	private Integer favoriteCount = 0;

	@Column()
	private Boolean isDecaf;

	@Column(nullable = false)
	private String name;

	@Column()
	@Enumerated(EnumType.STRING)
	private Bean bean;

	@Column()
	@Enumerated(EnumType.STRING)
	private Acidity acidity;

	@Column()
	private String information;

	@Column()
	private Boolean isCrush;

	@Column()
	private String size;

	@Column()
	private String capacity;

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	private ProductStatus status = ProductStatus.AVAILABLE;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createDatetime;

	@UpdateTimestamp
	@Column()
	private LocalDateTime updateDatetime;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Image> images = new ArrayList<>();

	public static Product createProduct(final ProductCategory category,
		Integer price, Integer stock, String name, Bean bean
		, Acidity acidity, String information, Boolean isCrush, Boolean isDecaf, String size, String capacity,
		SellerRep seller) {
		return category == ProductCategory.BEAN
			? createBean(category, price, stock, name, bean, acidity, information, isCrush, isDecaf, seller, null, null)
			: createBean(category, price, stock, name, Bean.NONE, Acidity.NONE, information, null, null, seller, size,
			capacity);
	}

	private static Product createBean(ProductCategory category, Integer price, Integer stock, String name, Bean bean
		, Acidity acidity, String information, Boolean isCrush, Boolean isDecaf, SellerRep sellerRep, String size,
		String capacity) {
		Product product = new Product();
		product.category = category;
		product.price = price;
		product.stock = stock;
		product.name = name;
		product.bean = bean;
		product.acidity = acidity;
		product.information = information;
		product.isCrush = isCrush;
		product.isDecaf = isDecaf;
		product.sellerRep = sellerRep;
		product.size = size;
		product.capacity = capacity;
		return product;
	}

	public void toModify(ProductCategory category, Integer price, String name, Bean bean
		, Acidity acidity, String information, Boolean isCrush, Boolean isDecaf, String size, String capacity) {
		this.category = category;
		this.price = price;
		this.name = name;
		this.bean = bean;
		this.acidity = acidity;
		this.information = information;
		this.isCrush = isCrush;
		this.isDecaf = isDecaf;
		this.size = size;
		this.capacity = capacity;
	}

	public void toModifyStatus(ProductStatus productStatus) {
		this.status = productStatus;
	}

	public boolean checkStock(int quantity) {
		if (hasEnoughStock(quantity)) {
			decreaseStock(quantity);
			return true;
		}
		return false;
	}

	private void decreaseStock(int quantity) {
		this.stock -= quantity;
	}

	public void increaseStock(int quantity) {
		this.stock += quantity;
	}

	private boolean hasEnoughStock(int requiredQuantity) {
		return this.stock >= requiredQuantity;
	}

	public String getThumbnailUrl() {
		return images.stream()
			.filter(Image::getIsThumbnail)
			.findFirst()
			.map(Image::getImageUrl)
			.orElse(null);
	}

	public void saveImage(String imageUrl, boolean isThumbnail, Short sequenceNumber) {
		this.images.add(
			Image.ofCreate(imageUrl, isThumbnail, sequenceNumber, this)
		);
	}

	public List<String> getImagesUrl() {
		return this.images.stream()
			.map(Image::getImageUrl)
			.toList();
	}

	public void deleteImages() {
		this.images.clear();
	}
}
