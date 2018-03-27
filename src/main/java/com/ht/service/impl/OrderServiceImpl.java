package com.ht.service.impl;

import com.ht.converter.OrderMaster2OrderDTOConverter;
import com.ht.dataobject.OrderDetail;
import com.ht.dataobject.OrderMaster;
import com.ht.dataobject.ProductInfo;
import com.ht.dto.CartDTO;
import com.ht.dto.OrderDTO;
import com.ht.enums.OrderStatusEnum;
import com.ht.enums.PayStatusEnum;
import com.ht.enums.ResultEnum;
import com.ht.exception.ResponseBankException;
import com.ht.exception.SellException;
import com.ht.repository.OrderDetailRepository;
import com.ht.repository.OrderMasterRepository;
import com.ht.service.*;
import com.ht.utils.KeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @auth Qiu
 * @time 2018/3/9
 **/
@Service
@Transactional
@Slf4j
public class OrderServiceImpl implements OrderService{
    @Autowired
    private OrderMasterRepository orderMasterRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private PayService payService;

    @Autowired
    private PushMessageService pushMessageService;

    @Autowired
    private WebSocket webSocket;

    /**
     * 创建新订单
     * @param orderDTO  数据传输层对象 传入一个订单信息
     * @return
     */
    @Override
    public OrderDTO create(OrderDTO orderDTO) {

        //生成一个订单id
        String orderId= KeyUtil.getUniqueKey();

        //定义一个订单总价
        BigDecimal orderAmount = new BigDecimal(BigInteger.ZERO);

        // 减库存写法  方法1
        // 定义要减库存的 所有商品的list
        // List<CartDTO> cartDTOList = new ArrayList<>();

        //1.查询商品的信息(数量 价格)
        //先根据orderDTO 主订单里面的 orderDetailList 属性 得到主订单包含的子订单信息(根据商品的id 该商品的订单信息)
        for(OrderDetail orderDetail:orderDTO.getOrderDetailList()){
            //根据商品的id 查询商品的详细信息
            ProductInfo productInfo=productService.findOne(orderDetail.getProductId());

            //如果查出来的商品信息为空  抛出异常 商品不存在
            if(productInfo==null){
              throw   new SellException(ResultEnum.PRODUCT_NOT_EXIST);
            }

            //2. 计算订单总价  注意这里的单价 不能从orderDetail 里面获取 因为传进来没有单价  要从查出来的productInfo获取
            orderAmount = productInfo.getProductPrice()
                    .multiply(new BigDecimal(orderDetail.getProductQuantity()))  //单个商品的总价
                    .add(orderAmount);  //把每一个商品的总价相加 得到订单的总价

            //订单详情入库
            //设置orderDetail的orderid和detailid
            orderDetail.setOrderId(orderId);
            orderDetail.setDetailId(KeyUtil.getUniqueKey());
            //利用下面这种一个一个属性的set  可以使用一个spring 提供的工具类来拷贝属性
            //orderDetail.setProductIcon(productInfo.getProductIcon());

            //把前者对象的属性拷贝到后者对象中
            BeanUtils.copyProperties(productInfo,orderDetail);
            //保存订单详情信息  数据插入 orderDetail表
            orderDetailRepository.save(orderDetail);

            // 减库存的写法  把订单详情里的每一个商品的 数量和id 记录下来 放到定义好的 list中
            //CartDTO cartDTO = new CartDTO(orderDetail.getProductId(), orderDetail.getProductQuantity());
            //cartDTOList.add(cartDTO);
        }

        //3. 写入订单数据库（orderMaster和orderDetail）
        OrderMaster orderMaster = new OrderMaster();
        orderDTO.setOrderId(orderId);
        //把前者对象的属性拷贝到后者对象中  注意这个方法   因为orderDTO里面只有部分信息 所以拷贝给orderMaster的信息不全 还需要自己设置一些属性给 orderMaster
        BeanUtils.copyProperties(orderDTO, orderMaster);
        orderMaster.setOrderAmount(orderAmount);
        orderMaster.setOrderStatus(OrderStatusEnum.NEW.getCode());
        orderMaster.setPayStatus(PayStatusEnum.WAIT.getCode());
        orderMasterRepository.save(orderMaster);


        //4. 扣库存 (判断数量是否够)

        // 减库存写法  方法2
        // 定义要减库存的 所有商品的list
         List<CartDTO> cartDTOList = new ArrayList<>();

         //使用lambda 表达式遍历 得到 订单详情里的 商品数量和商品id
         cartDTOList=orderDTO.getOrderDetailList().stream()
                 .map(e->new CartDTO(e.getProductId(),e.getProductQuantity()))
                 .collect(Collectors.toList());

         //减库存
        productService.decreaseStock(cartDTOList);

        //发送websocket消息
        webSocket.sendMessage(orderDTO.getOrderId());

        return orderDTO;
    }

    /**
     * 根据订单id  查询订单信息
     * @param orderId  订单id
     * @return
     */
    @Override
    public OrderDTO findOne(String orderId) {
        //根据订单id 查询订单主表信息
        OrderMaster orderMaster=orderMasterRepository.findOne(orderId);

        //如果订单不存在  抛出订单不存在的异常
        if(orderMaster==null){
            throw new SellException(ResultEnum.ORDER_NOT_EXIST);
        }

        //查询订单详情  根据主订单的id查询 订单详情表信息
        List<OrderDetail> orderDetailList=orderDetailRepository.findByOrderId(orderMaster.getOrderId());

        //判断订单详情信息不能为空
        if(CollectionUtils.isEmpty(orderDetailList)){
            throw new SellException(ResultEnum.ORDERDETAIL_NOT_EXIST);
        }
        //数据传输层数据
        OrderDTO orderDTO=new OrderDTO();
        //把orderMaster 里面的数据  复制给 orderDTO
        BeanUtils.copyProperties(orderMaster,orderDTO);
        orderDTO.setOrderDetailList(orderDetailList);

        return orderDTO;
    }

    /**
     * 查询一个买家的订单列表
     * @param buyerOpenid  买家的微信id
     * @param pageable 分页参数
     * @return
     */
    @Override
    public Page<OrderDTO> findList(String buyerOpenid, Pageable pageable) {
        //根据买家的微信id  查询买家的主订单信息  不需要查询订单详情信息
        Page<OrderMaster> orderMasterpage=orderMasterRepository.findByBuyerOpenid(buyerOpenid,pageable);

        //注意  此处不需要判断page对象是否为空  因为一个买家的订单列表可以为空
        //把OrderMaster 类型 转换成 OrderDTO类型
        List<OrderDTO> orderDTOList=OrderMaster2OrderDTOConverter.convert(orderMasterpage.getContent());

        //把List类型转换成Page类型  三个参数 第一个就是要转换的List  第二个是Pageable  第三个是数据总条数
        Page<OrderDTO> orderDTOPage=new PageImpl<OrderDTO>(orderDTOList,pageable,orderMasterpage.getTotalElements());
        return orderDTOPage;
    }

    /**
     * 取消订单
     * @param orderDTO 传入一个数据传输层对象  订单信息
     * @return
     */
    @Override
    public OrderDTO cancel(OrderDTO orderDTO) {
        //要修改的对象
        OrderMaster orderMaster=new OrderMaster();

        //先判断订单状态  只有新订单才能取消   已取消和已完结的订单不能再取消
        if(!orderDTO.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())){ //如果不是新订单 抛出异常
            log.error("【取消订单】订单状态不正确, orderId={}, orderStatus={}", orderDTO.getOrderId(), orderDTO.getOrderStatus());
            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }

        //符合修改状态的 修改订单状态
        orderDTO.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
        //把orderDTO 的值复制给 orderMaster 所以上面改orderDTO的状态即可
        BeanUtils.copyProperties(orderDTO,orderMaster);
        OrderMaster result=orderMasterRepository.save(orderMaster);
        if(result==null){
            log.error("【取消订单】更新失败, orderMaster={}", orderMaster);
            throw  new  SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }

        //修改库存  先判断要修改的订单里  是否有订单的详情信息
        //判断数据传输层(OrderDTO)的订单详情字段是否为空
        if(CollectionUtils.isEmpty(orderDTO.getOrderDetailList())){
            log.error("【取消订单】订单中无商品详情, orderDTO={}", orderDTO);
            throw new SellException(ResultEnum.ORDER_DETAIL_EMPTY);
        }
        //若详情不为空  修改商品的库存
        List<CartDTO> cartDTOList=new ArrayList<>();
        //循环整个订单  得到里面的商品详情信息  并且把商品的id和商品的数量设置到CartDTO里
        cartDTOList=orderDTO.getOrderDetailList().stream()
                .map(e->new CartDTO(e.getProductId(),e.getProductQuantity()))
                .collect(Collectors.toList());
        //加库存
        productService.increaseStock(cartDTOList);

        //若用户已经支付  还需要退款
        if(orderDTO.getOrderStatus().equals(PayStatusEnum.SUCCESS.getCode())){
            //调用微信退款
            payService.refund(orderDTO);
        }
        return orderDTO;
    }

    /**
     * 完结订单的方法
     * @param orderDTO 传入一个数据传输层信息 一个订单信息
     * @return
     */
    @Override
    public OrderDTO finish(OrderDTO orderDTO) {
        //先判断订单信息  只有新订单  才能完结订单
        if(!orderDTO.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())){ //订单状态不是新订单时 抛出异常
            log.error("【完结订单】 订单状态不正确 , orderId={} , orderStatus={}",orderDTO.getOrderId(),orderDTO.getOrderStatus());
            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }

        //修改状态
        orderDTO.setOrderStatus(OrderStatusEnum.FINISHED.getCode());
        OrderMaster orderMaster=new OrderMaster();
        //把orderDTO的属性 拷贝给orderMaster
        BeanUtils.copyProperties(orderDTO,orderMaster);

        OrderMaster result=orderMasterRepository.save(orderMaster);
        if(result==null){
            log.error("【完结订单】更新失败, orderMaster={}", orderMaster);
            throw  new  SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }

        //推送微信模版消息
        pushMessageService.orderStatus(orderDTO);
        return orderDTO;
    }

    /**
     *支付订单
     * @param orderDTO 传入一个数据传输层对象  一个订单信息
     * @return
     */
    @Override
    public OrderDTO paid(OrderDTO orderDTO) {
        //判断订单状态 如果不是新订单 不能进行支付 直接抛出异常
        if(!orderDTO.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())){
            log.error("【订单支付】订单状态不正确, orderId={}, orderStatus={}", orderDTO.getOrderId(), orderDTO.getOrderStatus());
            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }

        //判断支付状态  如果不是待支付的订单  不能支付
        if (!orderDTO.getPayStatus().equals(PayStatusEnum.WAIT.getCode())){
            log.error("【订单支付】订单支付状态不正确, orderDTO={}", orderDTO);
            throw new SellException(ResultEnum.ORDER_PAY_STATUS_ERROR);
        }

        //修改支付状态
        orderDTO.setPayStatus(PayStatusEnum.SUCCESS.getCode());
        OrderMaster orderMaster=new OrderMaster();
        //把orderDTO的信息拷贝到orderMaster中
        BeanUtils.copyProperties(orderDTO,orderMaster);

        OrderMaster result=orderMasterRepository.save(orderMaster);
        if(result==null){
            log.error("【订单支付】更新失败, orderMaster={}", orderMaster);
            throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }
        return orderDTO;
    }

    /**
     * 卖家查询所有用户的订单
     * @param pageable
     * @return
     */
    @Override
    public Page<OrderDTO> findList(Pageable pageable) {
        //查询所有主订单
        Page<OrderMaster> orderMasterPage = orderMasterRepository.findAll(pageable);
        //把orderMaster对象 转换成 orderDTO 对象
        List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(orderMasterPage.getContent());

        return new PageImpl<>(orderDTOList, pageable, orderMasterPage.getTotalElements());
    }
}
