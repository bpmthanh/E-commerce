package com.bpmthanh.ecommercebackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Inventory of a product that available for purchase.
 */
@Entity
@Table(name = "inventory")
public class Inventory {

    /** Unique id for the inventory. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    /** The product this inventory is of. */
    /**
     * Mối quan hệ này chỉ ra rằng mỗi đối tượng Inventory thuộc về một đối tượng
     * Product.
     * 
     * optional = false chỉ ra rằng mối quan hệ là bắt buộc, mỗi Product phải có một
     * đối tượng Inventory liên quan. Nếu không có Inventory nào liên quan, sẽ xảy
     * ra lỗi.
     * 
     * orphanRemoval = true chỉ ra rằng khi đối tượng Product không được liên kết
     * với bất kỳ Inventory nào, đối tượng Inventory đó sẽ bị xóa khỏi cơ sở dữ
     * liệu. Điều này đảm bảo tính nhất quán giữa Product và Inventory.
     * 
     * @JoinColumn(name = "product_id", nullable = false, unique = true) chỉ định
     *                  rằng cột "product_id" trong bảng Inventory sẽ được sử dụng
     *                  làm khóa ngoại để liên kết với cột khóa chính trong bảng
     *                  Product. nullable = false chỉ ra rằng cột "product_id" không
     *                  được để trống, tức là mỗi Inventory phải có một giá trị
     *                  product_id hợp lệ. unique = true chỉ ra rằng cột
     *                  "product_id" phải là duy nhất, không được trùng lặp.
     */
    @JsonIgnore
    @OneToOne(optional = false, orphanRemoval = true)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;
    /** The quantity in stock. */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * Gets the quantity in stock.
     * 
     * @return The quantity.
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity in stock of the product.
     * 
     * @param quantity The quantity to be set.
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the product.
     * 
     * @return The product.
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Sets the product.
     * 
     * @param product The product to be set.
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * Gets the ID of the inventory.
     * 
     * @return The ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the inventory.
     * 
     * @param id The ID.
     */
    public void setId(Long id) {
        this.id = id;
    }

}