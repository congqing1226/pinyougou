package com.pinyougou.shop.controller;

import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;

/**
 * @author congzi
 * @Description: 图片上传至 图片服务器controller
 * @create 2018-05-13
 * @Version 1.0
 */
@RestController
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("/upload")
    public Result upload(MultipartFile file){

        try{
            //1 获取原始文件名
            String filename = file.getOriginalFilename();
            //2 获取文件扩展名
            String extName = filename.substring(filename.indexOf(".") + 1);

            //3 使用客户端工具类,进行图片上传
            FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");
            String fileId = client.uploadFile(file.getBytes(), extName);

            Result result = new Result(true,FILE_SERVER_URL+fileId);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false, "上传失败");
        }
    }

}
