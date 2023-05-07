package org.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.reggie.common.CustomException;
import org.example.reggie.entity.Category;
import org.example.reggie.entity.Dish;
import org.example.reggie.entity.Setmeal;
import org.example.reggie.mapper.CategoryMapper;
import org.example.reggie.service.CategoryService;
import org.example.reggie.service.DishService;
import org.example.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    /*
    根据id删除分类前判断
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper =new LambdaQueryWrapper<>();

        //添加查询条件
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int cout= dishService.count(dishLambdaQueryWrapper);

        //如果菜品有关联分类，抛出异常
        if (cout>0){
            //已关联菜品
            throw new CustomException("当前分类下关联菜品，不能删除");
        }

        //如果菜品有关联套餐，抛出异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper =new LambdaQueryWrapper<>();

        //添加查询条件
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int cout2=setmealService.count(setmealLambdaQueryWrapper);

        if (cout2>0) {
            //已关联菜品
            throw new CustomException("当前套餐下关联菜品，不能删除");
        }

        //正常删除
        super.removeById(id);

    }
}
