package com.ht.controller;

import com.ht.VO.ProductInfoVO;
import com.ht.VO.ProductVO;
import com.ht.VO.ResultVO;
import com.ht.dataobject.ProductCategory;
import com.ht.dataobject.ProductInfo;
import com.ht.service.CategoryService;
import com.ht.service.ProductService;
import com.ht.utils.ResultVOUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.cache.annotation.Cacheable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 买家查询的商品信息Controller
 * @auth Qiu
 * @time 2018/3/9
 **/

@RestController
@RequestMapping("/buyer/product")
public class BuyerProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    //注意  要缓存的对象一定要可以序列化的
    //加入redis缓存  第一次会访问到里面的方法 并且把返回的对象给缓存起来   第二次及以后后就会访问缓存数据  不需要进入里面的方法了
    //cacheNames是redis的key的一个前缀  key是加到redis前缀后面的字符串  condition是判断是否缓存的条件 unless(如果不 的意思) 依据反结果来判断是否缓存 表示结果的code不等于0就不缓存(结果等于0 成功才会缓存)
    @Cacheable(cacheNames = "product", key = "123", condition = "#sellerId.length() > 3", unless = "#result.getCode() != 0")
    public ResultVO list(){

        //1.查询所有的上架的商品的详细信息  -----> 查询product_info表
        List<ProductInfo> productInfoList=productService.findUpAll();

        //2.查询类目(一次性查询)  传统的方法
       /* List<Integer> categoryTypeList=new ArrayList<>();
        for(ProductInfo info:productInfoList){//把上架的所有商品的类目编号设置到categoryTypeList中
            categoryTypeList.add(info.getCategoryType());
        }
        //根据类目编号  查询商品的类目信息  ------>查询product_category表
        List<ProductCategory> productCategoryList=categoryService.findByCategoryTypeIn(categoryTypeList);*/

        //精简方法(java8, lambda表达式)
        //查询所有上架商品的类目编号
        List<Integer> categoryTypeList=productInfoList.stream()
                .map(e ->e.getCategoryType())
                .collect(Collectors.toList());

        //根据类目编号  查询商品的类目信息  ------>查询product_category表
        //特别注意此方法  虽然上架的商品中可能会有同类型的 categoryTypeList会有相同的类目编号
        //但是 findByCategoryTypeIn() 方法会帮我们去掉重复的值  所以不会导致重复循环多次同类目的商品
        List<ProductCategory> productCategoryList=categoryService.findByCategoryTypeIn(categoryTypeList);

        //商品类目集合
        List<ProductVO> productVOList=new ArrayList<>();
        //3.数据拼装
        for(ProductCategory productCategory:productCategoryList){
                ProductVO productVO=new ProductVO();
                productVO.setCategoryName(productCategory.getCategoryName());
                productVO.setCategoryType(productCategory.getCategoryType());

                //商品同类目下的  上架商品集合
                List<ProductInfoVO> productInfoVOList=new ArrayList<>();
                for(ProductInfo productInfo:productInfoList){
                    ProductInfoVO productInfoVO=new ProductInfoVO();
                    //当上架商品的类型 和 商品类目表的类型一致时 才把值设置到 productInfoVO中
                    if (productInfo.getCategoryType().equals(productCategory.getCategoryType())) {
                        //传统方法 这样把值给set到对象中
                  /* productInfoVO.setProductId(productInfo.getProductId());
                       productInfoVO.setProductName(productInfo.getProductName());
                       productInfoVO.setProductPrice(productInfo.getProductPrice());
                       productInfoVO.setProductIcon(productInfo.getProductIcon());
                       productInfoVO.setProductDescription(productInfo.getProductDescription());
                   */
                        //使用BeanUtils工具类设置  spring自带的  把前者对象的属性复制到后者对象中
                        //把商品详细信息 设置到 ProductinfoVO对象中
                        BeanUtils.copyProperties(productInfo, productInfoVO);
                        //把商品详情信息productInfoVO对象  放到 商品详细信息的list中
                        productInfoVOList.add(productInfoVO);
                    }
                }
            //把商品详细信息的list 设置到 productVO 对象的 productInfoVOList字段
            productVO.setProductInfoVOList(productInfoVOList);
                //把同类目的商品 放到同类目的集合中
            productVOList.add(productVO);
        }
        //把数据设置到 resultVO 最外层的数据对象中
        ResultVOUtil resultVOUtil=new ResultVOUtil();
        //调用返回数据成功的方法
        return  resultVOUtil.success(productVOList);


     /*   测试数据格式是否符合API标准使用
        ResultVO resultVO=new ResultVO();
        ProductVO productVO=new ProductVO();
        ProductInfoVO productInfoVO=new ProductInfoVO();
        resultVO.setCode(0);
        resultVO.setMsg("成功");
        resultVO.setData(Arrays.asList(productVO));

        productVO.setCategoryName("热销榜");
        productVO.setCategoryType(2);
        productVO.setProductInfoVOList(Arrays.asList(productInfoVO));

        productInfoVO.setProductId("123");
        productInfoVO.setProductName("皮皮虾");
        productInfoVO.setProductPrice(new BigDecimal(12.3));
        productInfoVO.setProductIcon("www");
        productInfoVO.setProductDescription("这是皮皮虾");*/

    }

}
