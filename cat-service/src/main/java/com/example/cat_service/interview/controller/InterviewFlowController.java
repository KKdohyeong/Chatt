package com.example.cat_service.interview.controller;

import com.example.cat_service.interview.entity.InterviewMessage;
import com.example.cat_service.interview.entity.Question;
import com.example.cat_service.interview.service.InterviewService;
import com.example.cat_service.user.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Interview Flow (Question/Answer) with JSON responses
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interviews")
public class InterviewFlowController {

    private final InterviewService interviewService;

    /**
     * (1) 다음 질문 조회 (GET /api/interviews/{id}/next-question)
     *    => JSON 응답 { noMore:true } or { questionId, content, ... }
     */
    @GetMapping("/{interviewId}/next-question")
    public ResponseEntity<?> getNextQuestion(
            @PathVariable Long interviewId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        Question nextQ = interviewService.findNextQuestionToAsk(userDetails.getId(), interviewId);
        if (nextQ == null) {
            // 더 이상 질문이 없다 => { noMore: true }
            return ResponseEntity.ok(Map.of("noMore", true));
        }

        // 메시지 생성
        interviewService.createQuestionMessage(interviewId, nextQ);

        // 응답: { questionId, content, ... }
        return ResponseEntity.ok(Map.of(
                "questionId", nextQ.getId(),
                "content", nextQ.getContent()
        ));
    }

    /**
     * (2) 사용자 답변 전송 (POST /api/interviews/{id}/answer)
     *     body: { "content": "...user's answer..." }
     *     => JSON 응답: followUp 또는 next step
     */
    @PostMapping("/{interviewId}/answer")
    public ResponseEntity<?> postAnswer(
            @PathVariable Long interviewId,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // (A) 인증 체크
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        // (B) 요청 바디에서 사용자 답변 추출
        String userAnswer = body.get("content");
        if (userAnswer == null || userAnswer.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No answer provided"));
        }

        // (C) 사용자 답변 메시지 생성
        interviewService.createAnswerMessage(interviewId, userAnswer);

        // (D) ChatGPT API 등을 통한 꼬리질문 생성
        var followMsg = interviewService.createFollowUpQuestion(interviewId, userAnswer);

        // (E) JSON 응답: { "followUp": "꼬리질문" }
        return ResponseEntity.ok(Map.of(
                "followUp", followMsg.getContent()
        ));
    }


    /**
     * (3) 메시지 전체 조회 (GET /api/interviews/{id}/messages)
     *     => [{sender:"AI", content:"..."}, ...]
     */
    @GetMapping("/{interviewId}/messages")
    public ResponseEntity<?> getMessages(
            @PathVariable Long interviewId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        List<InterviewMessage> msgs = interviewService.getMessages(interviewId);

        // 간단 JSON 변환
        var result = msgs.stream().map(m -> Map.of(
                "id", m.getId(),
                "sender", m.getSender(),
                "content", m.getContent(),
                "createdAt", m.getCreatedAt()
        )).toList();

        return ResponseEntity.ok(result);
    }

    /**
     * (4) 꼬리 질문 종료 (POST /api/interviews/{id}/finishQuestion)
     *     => JSON { success: true }
     */
    @PostMapping("/{interviewId}/finishQuestion")
    public ResponseEntity<?> finishCurrentQuestion(
            @PathVariable Long interviewId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        interviewService.finishCurrentQuestion(interviewId, userDetails.getId());
        return ResponseEntity.ok(Map.of("success", true));
    }
}
