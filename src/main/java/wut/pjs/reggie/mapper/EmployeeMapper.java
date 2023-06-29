package wut.pjs.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import wut.pjs.reggie.entity.Employee;

/**
 * @Author: Jayson_P
 * @Date: 2023/06/04/0:46
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
