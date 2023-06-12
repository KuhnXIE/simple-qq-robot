package com.shr25.robot.qq.conf;

import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * bot配置
 */
@Slf4j
@Data
@Component
public class ChatBotConfig {
    @Resource
    ProxyConfig proxyConfig;
    @Resource
    ChatgptConfig chatgptConfig;

    private List<OpenAiService> openAiServiceList;
    private ChatMessage basicPrompt;
    private Integer maxToken;
    private Double temperature;
    private String model;

    @PostConstruct
    public void init() {
        if (proxyConfig.isStart()) {
            //配置代理
            if (null != proxyConfig.getHost() && !"".equals(proxyConfig.getHost())) {
                System.setProperty("http.proxyHost", proxyConfig.getHost());
                System.setProperty("https.proxyHost", proxyConfig.getHost());
            }
            if (null != proxyConfig.getPort() && !"".equals(proxyConfig.getPort())) {
                System.setProperty("http.proxyPort", proxyConfig.getPort());
                System.setProperty("https.proxyPort", proxyConfig.getPort());
            }

            //ChatGPT
            model = "gpt-3.5-turbo";
//        model = "gpt-3.5-turbo-0301" 这是快照版本
            maxToken = 2048;
            temperature = 0.8;
//        你可以通过设定basicPrompt来指定人格
//        basicPrompt = new ChatMessage("system", "接下来在我向你陈述一件事情时，你只需要回答：“典”");
            openAiServiceList = new ArrayList<>();
            for (String apiKey : chatgptConfig.getApiKey()) {
                apiKey = apiKey.trim();
                if (!"".equals(apiKey)) {
                    openAiServiceList.add(new OpenAiService(apiKey, Duration.ofSeconds(1000)));
                    log.info("apiKey为 {} 的账号初始化成功", apiKey);
                }
            }
        }else {
            log.info("未配置代理！");
        }
    }
}
