// src/main/java/project/DevView/cat_service/interview/controller/InterviewFlowController.java
package project.DevView.cat_service.interview.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.DevView.cat_service.interview.entity.InterviewMessage;
import project.DevView.cat_service.interview.service.InterviewFlowService;
import project.DevView.cat_service.question.entity.Question;
import project.DevView.cat_service.user.dto.CustomUserDetails;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interviews")
public class InterviewFlowController {

    private final InterviewFlowService flow;

    @GetMapping("/{interviewId}/next-question")
    public ResponseEntity<?> getNextQuestion(@PathVariable Long interviewId,
                                             @AuthenticationPrincipal CustomUserDetails user) {

        Question next = flow.getNextQuestion(user.getId(), interviewId);
        if (next == null) return ResponseEntity.ok(Map.of("noMore", true));

        flow.createQuestionMessage(interviewId, next);
        return ResponseEntity.ok(Map.of(
                "questionId", next.getId(),
                "content", next.getContent()
        ));
    }

    @PostMapping("/{interviewId}/answer")
    public ResponseEntity<?> postAnswer(@PathVariable Long interviewId,
                                        @RequestBody Map<String,String> body,
                                        @AuthenticationPrincipal CustomUserDetails user) {

        String ans = body.get("content");
        flow.createAnswerMessage(interviewId, ans);
        var follow = flow.createFollowUpQuestion(interviewId, ans);
        return ResponseEntity.ok(Map.of("followUp", follow.getContent()));
    }

    @GetMapping("/{interviewId}/messages")
    public ResponseEntity<List<InterviewMessage>> getMessages(
            @PathVariable Long interviewId,
            @AuthenticationPrincipal CustomUserDetails user) {

        return ResponseEntity.ok(flow.getMessages(interviewId));
    }

    @PostMapping("/{interviewId}/finishQuestion")
    public ResponseEntity<?> finishCurrentQuestion(
            @PathVariable Long interviewId,
            @AuthenticationPrincipal CustomUserDetails user) {

        flow.finishCurrentQuestion(interviewId, user.getId());
        return ResponseEntity.ok(Map.of("success", true));
    }
}
