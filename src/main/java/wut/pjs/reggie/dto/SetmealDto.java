package wut.pjs.reggie.dto;


import lombok.Data;
    import wut.pjs.reggie.entity.Setmeal;
import wut.pjs.reggie.entity.SetmealDish;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;

}
