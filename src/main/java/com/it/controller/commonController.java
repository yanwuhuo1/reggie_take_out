package com.it.controller;


import com.it.common.R;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class commonController {

    @Value("${reggie.path}")
    private String basePath;

    //文件上传
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {
      //  file是临时文件，需要转存到指定文件，否则请求完成会被清除
        log.info(file.toString());
        //原始文件名
        String originalFilename = file.getOriginalFilename();
        //截取后缀格式
        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用uuid重新生成文件名，防治文件名称重复造成文件覆盖
        String filename=UUID.randomUUID().toString() + substring;
        //如果没有文件就创建文件夹
        File file1 = new File(basePath);
        //判断当前目录是否存在
        if (!file1.exists()){
            //目录不存在，创建
            file1.mkdirs();
        }

        //将文件存储到指定位置
        file.transferTo(new File(basePath+filename));
        return R.success(filename);
    }
    //文件下载
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws Exception {
        //输入流，通过输入流读取文件内容
        FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));

        //输出流，通过输出流将文件写回浏览器，在浏览器展示图片
        ServletOutputStream outputStream = response.getOutputStream();

        response.setContentType("image/jpeg");

        int len=0;
        byte[] bytes = new byte[1024];
        while ((len=fileInputStream.read(bytes))!=-1){
            outputStream.write(bytes,0,len);
            outputStream.flush();
        }
        fileInputStream.close();
        outputStream.close();

    }
}
