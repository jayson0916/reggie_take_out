package wut.pjs.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wut.pjs.reggie.common.R;
import wut.pjs.reggie.entity.User;
import wut.pjs.reggie.service.UserService;

import wut.pjs.reggie.util.ValidateCodeUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Jayson_P
 * @Date: 2023/06/20/10:45
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    /*
    * 发送手机验证码*/
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            //生成一个四位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();

            log.info("code={}",code);
            //调用阿里云提供的短信服务API完成发送短信
            //SMSUtils.sendMessage("瑞吉外卖","",phone,code);
            //需要将生成的验证码保存起来
//            session.setAttribute(phone,code);

            //将生成的验证码缓存到redis中，设置有效时间
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);

            return R.success("手机验证码短信发送成功");
        }
            return R.error("手机短信发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        log.info("map:{}", map.toString());
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从Session中获取保存的验证码
//        Object codeInSession = session.getAttribute(phone);

        //从redis获取换从的验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);

        //进行验证码比对（页面提交的验证码和Session中保存的验证码比对）
        if (codeInSession != null && codeInSession.equals(code)) {
            //如果能够比对成功，说明登录成功

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if (user == null) {
                //判断当前手机号是否为新用户，如果是新用户则自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            //如果用户登录成功，则删除redis中缓存的验证码
            redisTemplate.delete(phone);
            return R.success(user);
        }
        return R.error("登陆失败");
    }


    /*
    * 用户退出
    * */
    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request){
        //1.清理Session中保存的当前登录员工的ID
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }

}
