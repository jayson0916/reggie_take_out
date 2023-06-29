package wut.pjs.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import wut.pjs.reggie.entity.AddressBook;
import wut.pjs.reggie.mapper.AddressBookMapper;
import wut.pjs.reggie.service.AddressBookService;

/**
 * @Author: Jayson_P
 * @Date: 2023/06/20/18:43
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
