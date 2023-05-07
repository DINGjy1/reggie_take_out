package org.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.reggie.dto.DishDto;
import org.example.reggie.entity.Dish;


public interface DishService extends IService<Dish> {

    //新增菜品，同时插入对应的口味等dto数据，需要操作两张表：dish dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和口味
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品信息，对应的口味
    public void updateWithFlavor(DishDto dishDto);

}
