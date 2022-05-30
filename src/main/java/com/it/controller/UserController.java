package com.it.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.it.common.R;
import com.it.common.SendSms;
import com.it.config.RedisConfig;
import com.it.entity.User;
import com.it.service.UserService;
import com.it.utils.ValidateCodeUtils;
import io.lettuce.core.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    //发送手机验证码
    @PostMapping("/sendMsg")
    private R<String> sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();
        System.out.println("phone = " + phone);


        if (StringUtils.isNotEmpty(phone)) {
            //生成随即验证码
            String param = ValidateCodeUtils.generateValidateCode(6).toString();
            log.info("code={}", param);

            //调用腾讯云提供的api发送短信
            SendSms.senMessage(phone,param);

            //将生成的验证码缓存到Redis中，设置有效时间为5分钟
            redisTemplate.opsForValue().set(phone, param, 5, TimeUnit.MINUTES);

            return R.success("发送成功");

        }
        return R.error("短信发送失败");
    }

    @PostMapping("/login")
    private R<User> login(@RequestBody Map map, HttpSession session) {

        log.info("map {}", map);
        log.info(map.toString());

        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();

        //从redis获取缓存的验证码
        Object codeinSession = redisTemplate.opsForValue().get(phone);
        //进行验证码的比对（页面提交的验证码和session中保存的验证码对表）
        if (codeinSession != null && codeinSession.equals(code)) {
            //如果能够比较成功，说明登入成功
            //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(userLambdaQueryWrapper);
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            //如果用户登入成功，就删除redis中缓存的验证码
            redisTemplate.delete(phone);

            return R.success(user);
        }

        return R.error("登入失败");
    }

    @PostMapping("/loginout")
    public R<String> loginout(HttpServletRequest request) {
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }

}
