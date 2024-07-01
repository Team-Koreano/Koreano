package org.ecommerce.productapi.entity;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.productapi.entity.enumerated.ProductStatus;
import org.ecommerce.productapi.exception.ProductErrorCode;

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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product_detail")
public class ProductDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@JoinColumn(name = "product_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private Product product;

	@Column(name = "price", nullable = false)
	private Integer price;

	@Column(name = "stock", nullable = false)
	private Integer stock;

	@Column(name = "size", length = 45)
	private String size;

	@Column(name = "is_default")
	private Boolean isDefault;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 45)
	private ProductStatus status;

	public static ProductDetail ofCreate(Product product, Integer price, Integer stock, String size, Boolean isDefault,
		ProductStatus status) {
		ProductDetail productDetail = new ProductDetail();
		productDetail.product = product;
		productDetail.price = price;
		productDetail.stock = stock;
		productDetail.size = size;
		productDetail.isDefault = isDefault;
		productDetail.status = status;
		return productDetail;
	}

	public void toModifyStatus(ProductStatus status) {
		this.status = status;
	}

	public void checkStock(int stock) {
		if (!hasEnoughStock(stock)) {
			throw new CustomException(ProductErrorCode.CAN_NOT_BE_SET_TO_BELOW_ZERO);
		}
		decreaseStock(stock);
	}

	public void changeIsDefaultFalse() {
		if (this.isDefault) {
			this.isDefault = false;
		}
	}

	public void increaseStock(Integer stock) {
		this.stock += stock;
	}

	public void toModifyProductDetail(Integer price, String size, Boolean isDefault) {
		this.price = price;
		this.size = size;
		this.isDefault = isDefault;
	}

	private void decreaseStock(int stock) {
		this.stock -= stock;
	}

	private boolean hasEnoughStock(int stock) {
		return this.stock >= stock;
	}

}