package com.shr25.robot.qq.controller;

import com.shr25.robot.qq.conf.QqConfig;
import com.shr25.robot.qq.mirai.WebLoginSolver;
import com.shr25.robot.qq.model.Vo.QqLogin;
import com.shr25.robot.qq.service.RobotService;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.network.WrongPasswordException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @date 2022/6/28 20:00
 */
@Slf4j
@RestController
public class LoginController {
    Map<Long, WebLoginSolver> cache = new HashMap<>();

    @Autowired
    private QqConfig qqConfig;

    @Autowired
    private RobotService robotService;

    /**
     * @param qqLogin 登录信息
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/login")
    public Object login(QqLogin qqLogin) {
        QqLogin data = new QqLogin();
        data.setQq(qqLogin.getQq());

        Map rs = new HashMap();
        WebLoginSolver old = cache.get(qqLogin.getQq());
        if (old == null) {
            WebLoginSolver webLoginSolver = new WebLoginSolver();
            if (StringUtils.isNotBlank(qqLogin.getPassword())) {
                // 启动机器人
                Thread qqRunThread = new Thread(() -> {
                    try {
                        robotService.start(qqLogin.getQq(), qqLogin.getPassword(), webLoginSolver);
                        webLoginSolver.setType(0);
                        rs.put("code", 200);
                        rs.put("msg", "登录成功");
                    } catch (WrongPasswordException e) {
                        cache.remove(qqLogin.getQq());
                        webLoginSolver.setType(9);
                        rs.put("code", 500);
                        rs.put("msg", e.getMessage().replaceAll(".*message=([^,]+),.*", "$1"));
                    } catch (Exception e) {
                        cache.remove(qqLogin.getQq());
                        webLoginSolver.setType(9);
                        rs.put("code", 500);
                        rs.put("msg", e.getMessage());
                    }
                });
                qqRunThread.setDaemon(true);
                qqRunThread.setName("QQ机器人服务运行线程:" + qqConfig.getQq());
                qqRunThread.start();

                for (; (rs.get("code") == null || rs.get("msg") == null) && webLoginSolver.getType() == null; ) {
                    //等待登录结果
                    if(webLoginSolver.getType() != null) {
                        System.out.println("===========================================================================");
                        System.out.println("code = " + rs.get("code"));
                        System.out.println("msg = " + rs.get("msg"));
                        System.out.println("type = " + webLoginSolver.getType());
                        System.out.println("===========================================================================");
                    }
                }

                log.info("Login---账号:{} 密码: {}]", qqLogin.getQq(), qqLogin.getPassword());

                if (rs.get("code") == null) {
                    if (webLoginSolver.getType() != null) {
                        cache.put(qqLogin.getQq(), webLoginSolver);
                        data.setType(webLoginSolver.getType());
                        data.setImgData(webLoginSolver.getImgData());
                        data.setUrl(webLoginSolver.getUrl());
                        rs.put("code", 403);
                        rs.put("data", data);
                    } else {
                        rs.put("code", 200);
                        rs.put("msg", "登录成功");
                    }
                }
            } else {
                rs.put("code", 500);
                rs.put("msg", "缺少密码！~~~~~~");
            }
        } else {
            if (old.getType() == 1) {
                old.setCode(qqLogin.getCode());
            } else if (old.getType() == 2) {
                old.setTicket(qqLogin.getTicket());
            }

            for (; old.getType() != 0 && old.getType() != 3 && old.getType() != 9; ) {
            }
            if (old.getType() == 3) {
                data.setType(old.getType());
                data.setUrl(old.getUrl());
                rs.put("code", 403);
                rs.put("data", data);
            }else if (old.getType() == 9) {
                rs.put("code", 500);
                rs.put("data", "请重新登录");
            } else {
                rs.put("code", 200);
                rs.put("msg", "登录成功");
            }
            cache.remove(qqLogin.getQq());
        }

        return rs;
    }
}
