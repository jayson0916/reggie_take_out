package wut.pjs.reggie.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import wut.pjs.reggie.common.R;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @Author: Jayson_P
 * @Date: 2023/06/13/17:34
 */

/*
* 文件的上传和下载
* */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    //使用@Value获取ReggieApplication配置文件里的参数 basePath==文件转存目录
    @Value("${reggie.path}")
    private String basePath;

    /*
    * 文件上传
    * */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){//MultipartFile的变量名要与前端的参数名相同，否则无法接受
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件就会被自动删除
        log.info(file.toString());

        //获取原始文件名
        String originalFilename = file.getOriginalFilename();

        //截取原始文件后缀名  使用substring截取，abc.jpg 使用substring处理后  .jpg
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID重新生成文件名，避免文件名重复，造成文件的覆盖，保证文件名唯一
        String fileName = UUID.randomUUID().toString()+suffix;

        //创建一个目录对象
        File dir = new File(basePath);
        //判断目录是否存在
        if (!dir.exists()) {
            //如果目录不存在 就新建目录
            dir.mkdirs();
        }

        try {
            //将文件转存到指定位置
            file.transferTo(new File(basePath+fileName));//使用原始文件名动态赋值
        } catch (IOException e) {
            e.printStackTrace();
        }

        //返回文件名给前端
        return R.success(fileName);
    }


    /*
    * 文件的下载 通过输出流向浏览器页面写回数据不需要返回值 二进制形式 binary
    * */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));
            //输出流，通过输出流将文件写回浏览器，在浏览器展开图片了  是要向浏览器写入数据，使用response响应
            ServletOutputStream outputStream = response.getOutputStream();

            //图片数据返回样式格式设置
            response.setContentType("image/jpeg");

            int lent = 0;
            //构造一个bytes数组
            byte[] bytes = new byte[1024];
            //每次读完的内容放入bytes数组当中 当lent==-1时 表示读完了
            while ((lent = fileInputStream.read(bytes))!=-1){
                //outputStream.write(bytes 写入的数据目标 ,0 起始位置,lent 终止位置);
                outputStream.write(bytes,0,lent);
                //使用 flush 刷新
                outputStream.flush();
            }

            //关闭io资源
            fileInputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
