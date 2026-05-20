package com.gym.controller.front;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/front/ai")
public class AiChatController {

    @Value("${ai.qianwen.api-key}")
    private String apiKey;

    @Value("${ai.qianwen.model}")
    private String model;

    @Value("${ai.qianwen.api-url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String SYSTEM_PROMPT = "你是\"FitBot\"，一位专业的健身房智能助手。你的职责包括：\n" +
            "1. 回答健身相关问题（训练方法、饮食建议、恢复技巧）\n" +
            "2. 推荐适合用户的课程和教练\n" +
            "3. 解答器材使用方法和注意事项\n" +
            "4. 提供健身计划建议\n" +
            "5. 解答会员相关问题（预约、续费等）\n\n" +
            "请用友好、专业的语气回答，适当使用 emoji 让回答更生动。回答要简洁实用，避免过长。";

    @PostMapping("/chat")
    public Result<Map<String, String>> chat(@RequestBody Map<String, Object> requestBody) {
        try {
            String message = (String) requestBody.get("message");
            List<Map<String, String>> history = (List<Map<String, String>>) requestBody.get("history");

            if (message == null || message.trim().isEmpty()) {
                return Result.error("消息不能为空");
            }

            // 构建消息列表
            List<Map<String, String>> messages = new ArrayList<>();

            // 系统提示词
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", SYSTEM_PROMPT);
            messages.add(systemMsg);

            // 历史对话（最近10条）
            if (history != null && !history.isEmpty()) {
                int start = Math.max(0, history.size() - 10);
                for (int i = start; i < history.size(); i++) {
                    messages.add(history.get(i));
                }
            }

            // 当前用户消息
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", message);
            messages.add(userMsg);

            // 构建请求体
            Map<String, Object> apiRequest = new HashMap<>();
            apiRequest.put("model", model);
            apiRequest.put("messages", messages);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(apiRequest), headers);

            // 调用千问API
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

            // 解析响应
            JsonNode responseJson = objectMapper.readTree(response.getBody());
            String reply = responseJson.path("choices").path(0).path("message").path("content").asText();

            Map<String, String> result = new HashMap<>();
            result.put("reply", reply);
            return Result.success(result);

        } catch (Exception e) {
            return Result.error("AI服务暂时不可用，请稍后再试");
        }
    }
}
