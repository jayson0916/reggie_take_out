package wut.pjs.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import wut.pjs.reggie.entity.User;
import wut.pjs.reggie.mapper.UserMapper;
import wut.pjs.reggie.service.UserService;

/**
 * @Author: Jayson_P
 * @Date: 2023/06/20/10:42
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
