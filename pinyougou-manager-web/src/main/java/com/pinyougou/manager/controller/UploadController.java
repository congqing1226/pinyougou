package com.pinyougou.manager.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import entity.Result;
import util.FastDFSClient;

@RestController
public class UploadController {

	@Value("${FILE_SERVER_URL}")
	private String FILE_SERVER_URL;//="http://192.168.25.133/";
	
	@RequestMapping("/upload")
	public Result upload(MultipartFile file){
		
		try {
			//得到上传文件的扩展名
			String originalFilename = file.getOriginalFilename();//原始文件名
			String extName = originalFilename.substring(originalFilename.indexOf(".")+1) ;
			
			//创建工具类客户端
			FastDFSClient client=new FastDFSClient("classpath:config/fdfs_client.conf");
			String fileId = client.uploadFile(file.getBytes(), extName);
			
			return new Result(true, FILE_SERVER_URL+fileId);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false, "上传失败");
		}
		
		
	}

	
}
