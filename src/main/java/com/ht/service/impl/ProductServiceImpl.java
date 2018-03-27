package com.ht.service.impl;

import com.ht.dataobject.ProductInfo;
import com.ht.dto.CartDTO;
import com.ht.enums.ProductStatusEnum;
import com.ht.enums.ResultEnum;
import com.ht.exception.SellException;
import com.ht.repository.ProductInfoRepository;
import com.ht.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @auth Qiu
 * @time 2018/3/9
 **/
@Service
@Transactional
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductInfoRepository productInfoRepository;

    @Override
    public ProductInfo findOne(String productId) {
        return productInfoRepository.findOne(productId);
    }

    //查询所有的上架商品
    @Override
    public List<ProductInfo> findUpAll() {
        return productInfoRepository.findByProductStatus(ProductStatusEnum.UP.getCode());
    }

    @Override
    public Page<ProductInfo> findAll(Pageable pageable) {
        return productInfoRepository.findAll(pageable);
    }

    @Override
    public ProductInfo save(ProductInfo productInfo) {
        return productInfoRepository.save(productInfo);
    }

    @Override
    public void increaseStock(List<CartDTO> cartDTOList) {
        //先循环整个订单的详情信息
        for(CartDTO cartDTO:cartDTOList){
            //根据商品的id 查询商品的详细信息
            ProductInfo productInfo=productInfoRepository.findOne(cartDTO.getProductId());
            if (productInfo == null) {
                //如果根据id查询不到商品  就抛出商品部存在的异常
                throw new SellException(ResultEnum.PRODUCT_NOT_EXIST);
            }
            //加库存
            Integer result = productInfo.getProductStock() + cartDTO.getProductQuantity();
            //把加上后的库存设置到商品信息中
            productInfo.setProductStock(result);
            //修改商品的详细信息
            productInfoRepository.save(productInfo);
        }

    }

    @Override
    public void decreaseStock(List<CartDTO> cartDTOList) {
        //判断库存够不够
        for (CartDTO cartDTO: cartDTOList) {
            //根据id查询商品信息
            ProductInfo productInfo = productInfoRepository.findOne(cartDTO.getProductId());
            if (productInfo == null) {
                //如果根据id查询不到商品  就抛出商品部存在的异常
                throw new SellException(ResultEnum.PRODUCT_NOT_EXIST);
            }

            //判断库存是否够  用数据库的库存 减去 要买的商品数量
            Integer result = productInfo.getProductStock() - cartDTO.getProductQuantity();
            if (result < 0) {
                //如果库存不足  抛出库存不足的异常
                throw new SellException(ResultEnum.PRODUCT_STOCK_ERROR);
            }

            //把减去后的库存设置到商品信息中
            productInfo.setProductStock(result);

            //修改商品的详细信息
            productInfoRepository.save(productInfo);
        }
    }

    @Override
    public ProductInfo onSale(String productId) {
        ProductInfo productInfo = productInfoRepository.findOne(productId);
        if (productInfo == null) {
            throw new SellException(ResultEnum.PRODUCT_NOT_EXIST);
        }
        if (productInfo.getProductStatusEnum() == ProductStatusEnum.UP) {
            throw new SellException(ResultEnum.PRODUCT_STATUS_ERROR);
        }

        //更新
        productInfo.setProductStatus(ProductStatusEnum.UP.getCode());
        return productInfoRepository.save(productInfo);
    }

    @Override
    public ProductInfo offSale(String productId) {
        ProductInfo productInfo = productInfoRepository.findOne(productId);
        if (productInfo == null) {
            throw new SellException(ResultEnum.PRODUCT_NOT_EXIST);
        }
        if (productInfo.getProductStatusEnum() == ProductStatusEnum.DOWN) {
            throw new SellException(ResultEnum.PRODUCT_STATUS_ERROR);
        }

        //更新
        productInfo.setProductStatus(ProductStatusEnum.DOWN.getCode());
        return productInfoRepository.save(productInfo);
    }
}
