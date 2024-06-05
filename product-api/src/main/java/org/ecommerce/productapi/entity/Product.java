package org.ecommerce.productapi.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.ecommerce.productapi.entity.enumerated.Acidity;
import org.ecommerce.productapi.entity.enumerated.Bean;
import org.ecommerce.productapi.entity.enumerated.ProductCategory;
import org.ecommerce.productapi.entity.enumerated.ProductStatus;
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
	private String capacity;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createDatetime;

	@UpdateTimestamp
	@Column()
	private LocalDateTime updateDatetime;

	@Column()
	private Short deliveryFee;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductDetail> productDetails = new ArrayList<>();

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Image> images = new ArrayList<>();

	public static Product createProduct(final ProductCategory category,
		Integer price, Integer stock, String name, Bean bean
		, Acidity acidity, String information, Boolean isCrush, Boolean isDecaf, String size, String capacity,
		short deliveryFee, SellerRep seller) {
		return category == ProductCategory.BEAN
			? createBean(category, price, stock, name, bean, acidity, information, isCrush, isDecaf, seller, null, null,
			deliveryFee)
			: createBean(category, price, stock, name, Bean.NONE, Acidity.NONE, information, null, null, seller, size,
			capacity, deliveryFee);
	}

	private static Product createBean(ProductCategory category, Integer price, Integer stock, String name, Bean bean
		, Acidity acidity, String information, Boolean isCrush, Boolean isDecaf, SellerRep sellerRep, String size,
		String capacity, short deliveryFee) {
		Product product = new Product();
		product.category = category;
		product.name = name;
		product.bean = bean;
		product.acidity = acidity;
		product.information = information;
		product.isCrush = isCrush;
		product.isDecaf = isDecaf;
		product.sellerRep = sellerRep;
		product.capacity = capacity;
		product.deliveryFee = deliveryFee;
		return product;
	}

	public void toModify(ProductCategory category, Integer price, String name, Bean bean
		, Acidity acidity, String information, Boolean isCrush, Boolean isDecaf, String size, String capacity,
		short deliveryFee) {
		this.category = category;
		this.name = name;
		this.bean = bean;
		this.acidity = acidity;
		this.information = information;
		this.isCrush = isCrush;
		this.isDecaf = isDecaf;
		this.capacity = capacity;
		this.deliveryFee = deliveryFee;
	}

	public void toModifyStatus(ProductStatus productStatus) {
	}

	public boolean checkStock(int quantity) {
		if (hasEnoughStock(quantity)) {
			decreaseStock(quantity);
			return true;
		}
		return false;
	}

	private void decreaseStock(int quantity) {
	}

	public void increaseStock(int quantity) {
	}

	private boolean hasEnoughStock(int requiredQuantity) {
		return true;
	}

	public String getThumbnailUrl() {
		return images.stream()
			.filter(Image::getIsThumbnail)
			.findFirst()
			.map(Image::getImageUrl)
			.orElse(null);
	}

	public void saveImages(List<Image> images) {
		this.images.addAll(images);
	}

	public List<String> getImagesUrl() {
		return this.images.stream()
			.map(Image::getImageUrl)
			.toList();
	}

	public void deleteImages() {
		this.images.clear();
	}

	// 테이블 변경으로 인해 에러를 피하기 위한 임시 생성자 (추후 pr에서 모듈 합치는 작업 진행하면서 제거 예정)
	public Product(Integer productId, ProductCategory productCategory, int i, int i1, SellerRep seller, int favoriteCount, boolean isDecaf, String name, Bean bean, Acidity acidity, String information,
		boolean isCrush, Object o, Object o1, ProductStatus productStatus, LocalDateTime testTime, LocalDateTime testTime1, short deliveryFee, List<Image> images) {
		this.id = productId;
		this.category = productCategory;
		this.sellerRep = seller;
		this.favoriteCount = favoriteCount;
		this.isDecaf = isDecaf;
		this.name = name;
		this.bean = bean;
		this.acidity = acidity;
		this.information = information;
		this.isCrush = isCrush;
		this.capacity = "testCapacity";
		this.createDatetime = testTime;
		this.updateDatetime = testTime1;
		this.deliveryFee = deliveryFee;
		this.images = images;
	}
}
