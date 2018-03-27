package com.ht.dataobject;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * 商品类目
 * @auth Qiu
 * @time 2018/3/8
 **/
@Entity
//动态修改数据库
@DynamicUpdate
//包含了set() get() 以及toString() 等的一些方法
@Data
public class ProductCategory {
    /** 类目ID.  主键自增*/
    @Id
    @GeneratedValue
    private Integer categoryId;
    /**类目名字.*/
    private String categoryName;
    /**类目编号. */
    private Integer categoryType;
    /**创建时间. */
    private Date createTime;
    /**修改时间. */
    private Date updateTime;

    public ProductCategory() {
    }

    public ProductCategory(String categoryName, Integer categoryType) {
        this.categoryName = categoryName;
        this.categoryType = categoryType;
    }
}
