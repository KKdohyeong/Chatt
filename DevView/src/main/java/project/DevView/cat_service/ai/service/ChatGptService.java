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

    public String getCompletion(String prompt) {
        ChatMessage systemMsg = new ChatMessage("system", 
            "당신은 개발자 기술 면접관입니다. 주어진 대화 맥락을 바탕으로 하나의 심층적인 꼬리질문만 생성해주세요. 여러 질문을 생성하지 마세요. 한국어로 생성을 하면서 질문은 실제 질문하는것처럼 어휘가 자연스러우면 좋아.");
        ChatMessage userMsg = new ChatMessage("user", prompt);

        ChatRequest requestBody = new ChatRequest(
            model,
            new ChatMessage[]{systemMsg, userMsg},
            1000,  // 하나의 질문만 생성하므로 1000 토큰으로 충분
            0.7    // 적절한 창의성
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        HttpEntity<ChatRequest> entity = new HttpEntity<>(requestBody, headers);
        String url = "https://api.openai.com/v1/chat/completions";
        ChatResponse response = restTemplate.postForObject(url, entity, ChatResponse.class);

        if (response != null && response.choices() != null && response.choices().length > 0) {
            String content = response.choices()[0].message().content().trim();
            // 여러 줄의 응답이 있다면 첫 번째 질문만 사용
            return content.split("\n")[0].trim();
        } else {
            throw new RuntimeException("AI 꼬리질문 생성에 실패했습니다.");
        }
    }

    /**
     * 면접 답변을 평가하는 메서드
     * @param chatHistory 대화 기록 (질문과 답변)
     * @return 평가 결과
     */
    public String evaluateAnswer(String chatHistory) {
        ChatMessage systemMsg = new ChatMessage("system", 
            "당신은 자기소개서 기반의 프로젝트 경험 면접을 진행한 기술 면접관입니다. 아래는 지금까지의 질문, 답변 결과입니다.\n\n" +
            "이 데이터를 바탕으로, 이 지원자에 대한 종합 평가를 정중한 어투로 작성하세요.\n" +
            "다음 기준에 따라 이 답변을 1~5점으로 평가하고, 각 항목별로 간단한 이유를 작성하세요.\n" +
            "단, 답변이 명확히 존재하지 않거나 실질적인 내용이 없는 경우 해당 항목에 1점을 부여하고, 그 사유를 명시해 주세요.\n\n" +
            "1. 질문의 의도 파악 : 질문의 핵심을 정확히 이해하고 맞춤으로 답했는가?\n" +
            "2. 답변의 구체성 : 개념이 아닌 실제 경험과 수치를 들어 상세히 설명했는가?\n" +
            "3. 사유와 결과의 일관성 : 문제의 원인, 대응, 결과의 흐름이 일관적인가?\n" +
            "4. 예외 상황에 대한 대응성 : 예상치 못한 상황에 대한 설명이나 대응책이 포함되었는가?\n" +
            "5. 답변/설명의 논리적 구성 : 말의 순서, 구성, 전개 방식이 논리적인가?\n" +
            "6. 답변의 간결성 : 말로 했을 때 1분 이내로 핵심이 정리되어 있는가?\n\n" +
            "---\n" +
            "마지막으로 총점을 계산하세요.\n\n" +
            "총점 (100점 만점) – 위 항목의 평균 점수를 20배하여 계산");
        
        ChatMessage userMsg = new ChatMessage("user", 
            "다음은 면접 대화 기록입니다. 위 기준에 따라 평가해주세요:\n\n" + chatHistory);

        ChatRequest requestBody = new ChatRequest(
            model,
            new ChatMessage[]{systemMsg, userMsg},
            2000,  // 평가는 좀 더 긴 응답이 필요
            0.7    // 적절한 창의성
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
            throw new RuntimeException("AI 답변 평가 생성에 실패했습니다.");
        }
    }

    /**
     * 건너뛰기 후 다음 질문을 가다듬는 메서드
     * @param originalQuestion 원래 질문
     * @return 가다듬어진 질문
     */
    public String refineQuestionAfterSkip(String originalQuestion) {
        ChatMessage systemMsg = new ChatMessage("system", 
            "당신은 개발자 기술 면접관입니다. 사용자가 이전 질문에 대해 '모르겠습니다'라고 답변했을 때, 자연스럽게 다음 질문으로 넘어가기 위한 쿠션어를 포함하여 질문을 가다듬어주세요.\n\n" +
            "다음 원칙을 따라주세요:\n" +
            "1. '그러면 다른 관점에서 질문해보겠습니다' 또는 '다른 주제로 넘어가보겠습니다'와 같은 자연스러운 전환 문구를 사용하세요.\n" +
            "2. 전체 문장이 자연스럽게 이어지도록 하세요.\n" +
            "3. 한국어로 작성하되, 정중하고 전문적인 어투를 사용하세요.");
        
        ChatMessage userMsg = new ChatMessage("user", 
            "다음 질문을 가다듬어주세요:\n" + originalQuestion);

        ChatRequest requestBody = new ChatRequest(
            model,
            new ChatMessage[]{systemMsg, userMsg},
            500,  // 짧은 응답이므로 500 토큰으로 충분
            0.7
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
            // 실패 시 원래 질문 반환
            return originalQuestion;
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