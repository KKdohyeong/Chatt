package project.DevView.cat_service.ai.service;

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

    @Value("${openai.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateFollowUpQuestion(String conversationContext, String userAnswer) {
        ChatMessage systemMsg = new ChatMessage("system",
                "You are a helpful assistant. The conversation so far:\n" + conversationContext);
        ChatMessage userMsg = new ChatMessage("user",
                "User answer: " + userAnswer + "\nNow create a follow-up question for the user.");

        ChatRequest requestBody = new ChatRequest(
                model,
                new ChatMessage[]{ systemMsg, userMsg },
                100,
                0.8
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        HttpEntity<ChatRequest> entity = new HttpEntity<>(requestBody, headers);
        String url = "https://api.openai.com/v1/chat/completions";
        ChatResponse response = restTemplate.postForObject(url, entity, ChatResponse.class);

        if (response != null && response.choices() != null && response.choices().length > 0) {
            return response.choices()[0].message().content().trim();
        } else {
            return "No follow-up question could be generated.";
        }
    }

    // DTO 클래스들
    record ChatRequest(
        String model,
        ChatMessage[] messages,
        int max_tokens,
        double temperature
    ) {}

    record ChatMessage(
        String role,
        String content
    ) {}

    record ChatResponse(
        Choice[] choices
    ) {}

    record Choice(
        int index,
        ChatMessage message,
        String finish_reason
    ) {}
} 