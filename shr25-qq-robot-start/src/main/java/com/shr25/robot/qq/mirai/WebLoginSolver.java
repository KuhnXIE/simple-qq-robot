package com.shr25.robot.qq.mirai;

import kotlin.coroutines.Continuation;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.utils.LoginSolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebLoginSolver extends LoginSolver {

    Logger log = LoggerFactory.getLogger(getClass());
    /** bot的号码 */
    Long qq;

    /** 登录需要处理的事件类型 0.登录成功  1.图片验证码  2. 滑动验证码 3.理不安全设备验证*/
    Integer type;

    /** 需要验证的机器人 */
    Bot bot;

    /** 图片验证码 */
    byte[] imgData;

    /** 需要处理的url */
    String url;

    /** 图片验证码的值 */
    String code;

    /** 滑动模块的ticket */
    String ticket;

    public Long getQq() {
        return qq;
    }

    public void setQq(Long qq) {
        this.qq = qq;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Bot getBot() {
        return bot;
    }

    public void setBot(Bot bot) {
        this.bot = bot;
    }

    public byte[] getImgData() {
        return imgData;
    }

    public void setImgData(byte[] imgData) {
        this.imgData = imgData;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    @Override
    public boolean isSliderCaptchaSupported() {
        return true;
    }
    /**
     * 处理图片验证码.
     * 返回 null 以表示无法处理验证码, 将会刷新验证码或重试登录. 抛出一个 LoginFailedException 以正常地终止登录, 抛出任意其他 Exception 将视为异常终止
     * @param bot
     * @param bytes
     * @param continuation
     * @return
     */
    @Nullable
    @Override
    public Object onSolvePicCaptcha(@NotNull Bot bot, @NotNull byte[] bytes, @NotNull Continuation<? super String> continuation) {
        this.qq = bot.getId();
        this.type = 1;
        this.bot = bot;
        this.imgData = bytes;
        log.info("[PicCaptcha] 需要图片验证码登录, 验证码为 4 字母" );
        log.info("[PicCaptcha] Picture captcha required. Captcha consists of 4 letters.");
        Long currTime = System.currentTimeMillis();
        //最大等待检测5分钟，没有值返回
        for (;System.currentTimeMillis()-currTime > 300000;) {
            if(code != null){
                break;
            }
        }
        return code;
    }

    /**
     * 处理滑动验证码.
     *
     * 返回 `null` 以表示无法处理验证码, 将会刷新验证码或重试登录.
     * 抛出一个 [LoginFailedException] 以正常地终止登录, 抛出任意其他 [Exception] 将视为异常终止
     *
     * @return 验证码解决成功后获得的 ticket.
     */
    @Nullable
    @Override
    public Object onSolveSliderCaptcha(@NotNull Bot bot, @NotNull String url, @NotNull Continuation<? super String> continuation) {
        this.qq = bot.getId();
        this.type = 2;
        this.bot = bot;
        this.url = url;
        log.info("[SliderCaptcha] 需要滑动验证码, 请按照以下链接的步骤完成滑动验证码, 然后输入获取到的 ticket");
        log.info("[SliderCaptcha] Slider captcha required. Please solve the captcha with following link. Type ticket here after completion.");
        log.info("[SliderCaptcha] @see https://github.com/project-mirai/mirai-login-solver-selenium");
        log.info("[SliderCaptcha] @see https://docs.mirai.mamoe.net/mirai-login-solver-selenium/");
        log.info("[SliderCaptcha] 或者输入 TxCaptchaHelper 来使用 TxCaptchaHelper 完成滑动验证码");
        log.info("[SliderCaptcha] Or type `TxCaptchaHelper` to resolve slider captcha with TxCaptchaHelper.apk");
        log.info("[SliderCaptcha] Captcha link: {}", url);
        Long currTime = System.currentTimeMillis();
        //最大等待检测1分钟，没有值返回
        for (;System.currentTimeMillis()-currTime > 60000;) {
            if(ticket != null){
                break;
            }
        }
        return ticket;
    }

    /**
     * 处理不安全设备验证.
     *
     * 返回值保留给将来使用. 目前在处理完成后返回任意内容 (包含 `null`) 均视为处理成功.
     * 抛出一个 [LoginFailedException] 以正常地终止登录, 抛出任意其他 [Exception] 将视为异常终止.
     *
     * @return 任意内容. 返回值保留以供未来更新.
     */
    @Nullable
    @Override
    public Object onSolveUnsafeDeviceLoginVerify(@NotNull Bot bot, @NotNull String url, @NotNull Continuation<? super String> continuation) {
        this.qq = bot.getId();
        this.type = 3;
        this.bot = bot;
        this.url = url;
        log.info("[UnsafeLogin] 当前登录环境不安全，服务器要求账户认证。请在 QQ 浏览器打开 {} 并完成验证后输入任意字符。", url);
        log.info("[UnsafeLogin] Account verification required by the server. Please open {} in QQ browser and complete challenge, then type anything here to submit.", url);
        return null;
    }
}
