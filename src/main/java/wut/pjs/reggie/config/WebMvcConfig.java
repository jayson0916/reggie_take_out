package wut.pjs.reggie.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import wut.pjs.reggie.common.JacksonObjectMapper;

import java.util.List;

/**
 * @Author: Jayson_P
 * @Date: 2023/06/03/23:58
 */

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {


    /*
    * 设置静态资源映射
    * */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("静态资源映射启动！");
        registry.addResourceHandler("/backend/**").addResourceLocations("classPath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classPath:/front/");
    }


    /*
    * 扩展MVC消息转换器
    * */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器启动！");
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用jackson将java对象转换成json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的消息转换器对象追加到MVC框架的转换器集合中 index 为0 优先使用扩展的消息转换器
        converters.add(0,messageConverter);
    }
}
