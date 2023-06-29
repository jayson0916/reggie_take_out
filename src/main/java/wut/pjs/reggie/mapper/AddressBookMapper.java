package wut.pjs.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import wut.pjs.reggie.entity.AddressBook;

/**
 * @Author: Jayson_P
 * @Date: 2023/06/20/18:42
 */
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
