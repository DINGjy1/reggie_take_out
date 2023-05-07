package org.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.reggie.entity.Category;
import org.omg.CORBA.PUBLIC_MEMBER;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
