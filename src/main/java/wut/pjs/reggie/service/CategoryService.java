package wut.pjs.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;
import wut.pjs.reggie.entity.Category;

/**
 * @Author: Jayson_P
 * @Date: 2023/06/09/11:08
 */
@Service
public interface CategoryService extends IService<Category> {
     void remove (Long id);
}
