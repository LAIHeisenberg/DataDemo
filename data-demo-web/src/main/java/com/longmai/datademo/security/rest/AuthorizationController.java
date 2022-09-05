/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.longmai.datademo.security.rest;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.longmai.datademo.annotation.rest.AnonymousDeleteMapping;
import com.longmai.datademo.annotation.rest.AnonymousGetMapping;
import com.longmai.datademo.annotation.rest.AnonymousPostMapping;
import com.longmai.datademo.config.RsaProperties;
import com.longmai.datademo.exception.BadRequestException;
import com.longmai.datademo.security.config.bean.LoginCodeEnum;
import com.longmai.datademo.security.config.bean.LoginProperties;
import com.longmai.datademo.security.config.bean.SecurityProperties;
import com.longmai.datademo.security.security.TokenProvider;
import com.longmai.datademo.security.service.OnlineUserService;
import com.longmai.datademo.security.service.dto.AuthUserDto;
import com.longmai.datademo.security.service.dto.JwtUserDto;
import com.longmai.datademo.utils.*;
import com.wf.captcha.base.Captcha;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Zheng Jie
 * @date 2018-11-23
 * 授权、根据token获取用户详细信息
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthorizationController {
    private final SecurityProperties properties;
    private final RedisUtils redisUtils;
    private final OnlineUserService onlineUserService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    @Resource
    private LoginProperties loginProperties;
//    @Autowired
//    private UserService userService;
    @Autowired
    private UserDetailsService userDetailsService;

    @AnonymousPostMapping(value = "/login")
    public ResponseEntity<Object> login(@Validated @RequestBody AuthUserDto authUser, HttpServletRequest request) throws Exception {

        String password = null;
        String username = authUser.getUsername();
        Authentication authentication;
//        if (authUser.getAuthMethod() == 1){
//            UserDto userDto = userService.findByDn(authUser.getDn());
//            UserDto userDto = new UserDto();
//            if (userDto == null){
//                throw new BadRequestException("设备号无效");
//            }
//            PublicKey publicKey = CertUtils.getPublicKey(userDto.getCert());
//            if(publicKey == null){
//                throw new BadRequestException("证书不存在");
//            }
//            if(!RsaUtils.verify(authUser.getPreSignCode(), authUser.getSign(), publicKey, "SHA1withRSA")){
//                throw new BadRequestException("验签失败");
//            }
//            UserDetails userDetails = userDetailsService.loadUserByUsername(userDto.getUserName());
//            authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//
//        }else {
//            // 密码解密
//            password = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, authUser.getPassword());
//            // 查询验证码
//            String code = (String) redisUtils.get(authUser.getUuid());
//            // 清除验证码
//            redisUtils.del(authUser.getUuid());
//            if (StringUtils.isBlank(code)) {
//                throw new BadRequestException("验证码不存在或已过期");
//            }
//            if (StringUtils.isBlank(authUser.getCode()) || !authUser.getCode().equalsIgnoreCase(code)) {
//                throw new BadRequestException("验证码错误");
//            }
//            UsernamePasswordAuthenticationToken authenticationToken =
//                    new UsernamePasswordAuthenticationToken(username, password);
//            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
//        }

//        SecurityContextHolder.getContext().setAuthentication(authentication);
        authentication =
                    new UsernamePasswordAuthenticationToken(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.createToken(authentication);
        final JwtUserDto jwtUserDto = (JwtUserDto) authentication.getPrincipal();
        // 保存在线信息
        onlineUserService.save(jwtUserDto, token, request);
        // 返回 token 与 用户信息
        Map<String, Object> authInfo = new HashMap<String, Object>(2) {{
            put("token", properties.getTokenStartWith() + token);
            put("user", jwtUserDto);
        }};
        if (loginProperties.isSingleLogin()) {
            //踢掉之前已经登录的token
            onlineUserService.checkLoginOnUser(authUser.getUsername(), token);
        }
        return ResponseEntity.ok(authInfo);
    }


    @GetMapping(value = "/info")
    public ResponseEntity<Object> getUserInfo() {
        return ResponseEntity.ok(SecurityUtils.getCurrentUser());
    }

    @AnonymousGetMapping(value = "/code")
    public ResponseEntity<Object> getCode() {
        // 获取运算的结果
        Captcha captcha = loginProperties.getCaptcha();
        String uuid = properties.getCodeKey() + IdUtil.simpleUUID();
        //当验证码类型为 arithmetic时且长度 >= 2 时，captcha.text()的结果有几率为浮点型
        String captchaValue = captcha.text();
        if (captcha.getCharType() - 1 == LoginCodeEnum.ARITHMETIC.ordinal() && captchaValue.contains(".")) {
            captchaValue = captchaValue.split("\\.")[0];
        }
        // 保存
        redisUtils.set(uuid, captchaValue, loginProperties.getLoginCode().getExpiration(), TimeUnit.MINUTES);
        // 验证码信息
        Map<String, Object> imgResult = new HashMap<String, Object>(2) {{
            put("img", captcha.toBase64());
            put("uuid", uuid);
        }};
        return ResponseEntity.ok(imgResult);
    }

    @AnonymousDeleteMapping(value = "/logout")
    public ResponseEntity<Object> logout(HttpServletRequest request) {
        onlineUserService.logout(tokenProvider.getToken(request));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @AnonymousGetMapping(value = "/preSign-code")
    public ResponseEntity<Object> getPreSignCode(){
        String code = RandomUtil.randomString(16);
        Map<String, String> resMap = new HashMap<String, String>(2) {{
            put("code", code);
        }};
        return ResponseEntity.ok(resMap);
    }

}
