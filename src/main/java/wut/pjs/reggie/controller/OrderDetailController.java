package wut.pjs.reggie.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wut.pjs.reggie.service.OrderDetailService;

/**
 * @Author: Jayson_P
 * @Date: 2023/06/23/17:47
 */
@Slf4j
@RestController
@RequestMapping("/oderDetail")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;


}
