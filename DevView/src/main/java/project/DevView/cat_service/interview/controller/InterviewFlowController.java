// src/main/java/project/DevView/cat_service/interview/controller/InterviewFlowController.java
package project.DevView.cat_service.interview.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.DevView.cat_service.interview.entity.InterviewMessage;
import project.DevView.cat_service.interview.service.InterviewFlowService;
import project.DevView.cat_service.question.entity.Question;
import project.DevView.cat_service.user.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import project.DevView.cat_service.global.dto.response.SuccessResponse;
import project.DevView.cat_service.global.dto.response.result.SingleResult;
import project.DevView.cat_service.global.dto.response.result.ListResult;
import project.DevView.cat_service.global.service.ResponseService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interviews")
@Tag(name = "Interview Flow", description = "인터뷰 진행 관련 API")
public class InterviewFlowController {

    private final InterviewFlowService flow;

    @GetMapping("/{interviewId}/next-question")
    @Operation(summary = "다음 질문 조회", description = "아직 답변하지 않은 다음 질문을 조회합니다. 더 이상 질문이 없으면 noMore=true 반환.")
    public SuccessResponse<SingleResult<Map<String, Object>>> getNextQuestion(
            @PathVariable Long interviewId,
            @AuthenticationPrincipal CustomUserDetails user) {

        Question next = flow.getNextQuestion(user.getId(), interviewId);
        if (next == null) {
            return SuccessResponse.ok(ResponseService.getSingleResult(Map.of("noMore", true)));
        }

        flow.createQuestionMessage(interviewId, next);
        Map<String, Object> result = Map.of(
            "questionId", next.getId(),
            "content", next.getContent()
        );
        return SuccessResponse.ok(ResponseService.getSingleResult(result));
    }

    @PostMapping("/{interviewId}/answer")
    @Operation(summary = "질문에 답변 제출", description = "질문에 대한 답변을 제출하고, AI가 꼬리질문을 생성합니다.")
    public SuccessResponse<SingleResult<Map<String, String>>> postAnswer(
            @PathVariable Long interviewId,
            @RequestBody Map<String,String> body,
            @AuthenticationPrincipal CustomUserDetails user) {

        String ans = body.get("content");
        flow.createAnswerMessage(interviewId, ans);
        var follow = flow.createFollowUpQuestion(interviewId, ans);
        return SuccessResponse.ok(ResponseService.getSingleResult(Map.of("followUp", follow.getContent())));
    }

    @GetMapping("/{interviewId}/messages")
    @Operation(summary = "인터뷰 메시지 전체 조회", description = "해당 인터뷰의 모든 메시지(질문/답변/꼬리질문 등)를 조회합니다.")
    public SuccessResponse<ListResult<InterviewMessage>> getMessages(
            @PathVariable Long interviewId,
            @AuthenticationPrincipal CustomUserDetails user) {

        List<InterviewMessage> messages = flow.getMessages(interviewId);
        return SuccessResponse.ok(ResponseService.getListResult(messages));
    }

    @PostMapping("/{interviewId}/finishQuestion")
    @Operation(summary = "현재 꼬리질문 종료", description = "현재 꼬리질문(질문-답변 쌍)을 종료 처리합니다.")
    public SuccessResponse<SingleResult<Map<String, Boolean>>> finishCurrentQuestion(
            @PathVariable Long interviewId,
            @AuthenticationPrincipal CustomUserDetails user) {

        flow.finishCurrentQuestion(interviewId, user.getId());
        return SuccessResponse.ok(ResponseService.getSingleResult(Map.of("success", true)));
    }
}
