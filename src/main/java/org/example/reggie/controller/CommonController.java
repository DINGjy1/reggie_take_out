package org.example.reggie.controller;


import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.example.reggie.common.R;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;

import java.util.UUID;

/*
文件上传，下载
 */
@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /*
    文件上传
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //浏览器tomcat生成临时文件file
        log.info(file.toString());

        //上传前的文件名
        String originalFilename = file.getOriginalFilename();
        String suffix =originalFilename.substring(originalFilename.lastIndexOf("."));//截取 .后缀名

        //防止出现重复文件名而被覆盖，使用UUID
        String fileName=UUID.randomUUID().toString()+suffix;//文件名+.jpg

        //判断目标目录是否存在，不存在就直接创建目录
        File dir =new File(basePath);
        if (!dir.exists()){
            //目录不存在,创建
            dir.mkdirs();
        }


        //转存
        try {
            //转存位置
            file.transferTo(new File(basePath+originalFilename));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  R.success(originalFilename);
    }
    /*
    文件下载
     */
    @GetMapping("/download")
    public void download(String name,HttpServletResponse response){

        try {
            //输入流，读出文件
            FileInputStream fileInputStream= new FileInputStream(new File(basePath+name));

            //输出流，写回浏览器显示
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");//响应：图片文件

            int len=0;
            byte[] bytes =new byte[1024];
            while ( (len= fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            //关闭资源
            outputStream.close();
            fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
