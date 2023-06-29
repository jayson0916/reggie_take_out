package wut.pjs.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import wut.pjs.reggie.entity.User;

/**
 * @Author: Jayson_P
 * @Date: 2023/06/20/10:40
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
