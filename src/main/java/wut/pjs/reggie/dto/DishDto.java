package wut.pjs.reggie.dto;

import lombok.Data;
import wut.pjs.reggie.entity.Dish;
import wut.pjs.reggie.entity.DishFlavor;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    //菜品对应的口味数据
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
