package com.example.cat_service.interview.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ChatGptService {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateFollowUpQuestion(String conversationContext, String userAnswer) {
        // 1) 기존의 prompt 대신,
        //    ChatGPT "messages" 형태로 구성:
        //    - system 메시지에 "conversationContext"
        //    - user 메시지에 userAnswer + "새 follow-up question"
        ChatMessage systemMsg = new ChatMessage("system",
                "You are a helpful assistant. The conversation so far:\n" + conversationContext);
        ChatMessage userMsg = new ChatMessage("user",
                "User answer: " + userAnswer + "\nNow create a follow-up question for the user.");

        // 2) ChatRequest (model: gpt-4o-mini, etc.)
        ChatRequest requestBody = new ChatRequest(
                "gpt-4o-mini",                 // 원하는 모델명 (가정)
                new ChatMessage[]{ systemMsg, userMsg },
                100,
                0.8
        );

        // 3) HTTP 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        // 4) API 호출 (chat/completions)
        HttpEntity<ChatRequest> entity = new HttpEntity<>(requestBody, headers);
        String url = "https://api.openai.com/v1/chat/completions";
        ChatResponse response = restTemplate.postForObject(url, entity, ChatResponse.class);

        // 5) 결과 파싱
        if (response != null
                && response.choices() != null
                && response.choices().length > 0) {
            // chat API 결과 => choices[0].message.content
            return response.choices()[0].message().content().trim();
        } else {
            return "No follow-up question could be generated.";
        }
    }

    // ==== Request/Response DTOs for Chat Completion ====
    // (가급적 기존 구조를 크게 바꾸지 않고, fields만 교체)

    /**
     * ChatRequest: model + messages[] + max_tokens + temperature
     */
    record ChatRequest(
            String model,
            ChatMessage[] messages,
            int max_tokens,
            double temperature
    ) {}

    /**
     * 각 메시지 (role, content)
     */
    record ChatMessage(
            String role,
            String content
    ) {}

    /**
     * ChatResponse: choices[]
     */
    record ChatResponse(
            Choice[] choices
    ) {}

    /**
     * Choice: index, message, finish_reason
     */
    record Choice(
            int index,
            ChatMessage message,
            String finish_reason
    ) {}
}
