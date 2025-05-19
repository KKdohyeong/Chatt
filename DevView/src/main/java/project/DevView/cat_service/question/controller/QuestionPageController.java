package project.DevView.cat_service.question.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import project.DevView.cat_service.question.entity.Field;
import project.DevView.cat_service.question.service.FieldService;
import project.DevView.cat_service.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import project.DevView.cat_service.user.dto.CustomUserDetails;

import java.util.List;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/questions")
@RequiredArgsConstructor
@Tag(name = "Question Page", description = "질문 관련 SSR 페이지")
public class QuestionPageController {

    private final FieldService fieldService;
    private final QuestionService questionService;

    /**
     * 1) Question 생성 폼 (SSR)
     *    GET /admin/questions/new
     */
    @GetMapping("/new")
    @Operation(summary = "질문 생성 폼", description = "새 질문 생성을 위한 폼 페이지")
    public String newQuestionPage(Model model) {
        List<Field> allFields = fieldService.getAllFieldEntities();
        model.addAttribute("allFields", allFields);
        return "newQuestion";
    }
    
    /**
     * 2) 모든 질문 목록 조회 (SSR)
     *    GET /questions/all?field=OS
     */
    @GetMapping("/all")
    @Operation(summary = "분야별 전체 질문 목록", description = "특정 분야의 모든 질문 목록을 보여주는 페이지 (답변 여부 포함)")
    public String allQuestionsPage(
            @RequestParam("field") String fieldName,
            @AuthenticationPrincipal CustomUserDetails user,
            HttpServletRequest request,
            Model model
    ) {
        // 디버깅 로그 추가
        System.out.println("[QuestionPageController] /questions/all 요청 처리");
        System.out.println("[QuestionPageController] 사용자 인증 상태: " + (user != null ? "인증됨" : "인증되지 않음"));
        
        // 쿠키 디버깅
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            System.out.println("[QuestionPageController] 쿠키 목록:");
            for (Cookie cookie : cookies) {
                System.out.println("  - " + cookie.getName() + ": " + cookie.getValue() + 
                                  " (Path: " + cookie.getPath() + ", Domain: " + cookie.getDomain() + ")");
            }
        } else {
            System.out.println("[QuestionPageController] 쿠키 없음");
        }
        
        // 인증된 사용자인 경우 답변 상태와 함께 조회
        if (user != null) {
            System.out.println("[QuestionPageController] 인증된 사용자 ID: " + user.getId());
            var questions = questionService.getAllQuestionsWithStatusByField(user.getId(), fieldName);
            model.addAttribute("questions", questions.getList());
        } else {
            System.out.println("[QuestionPageController] 인증되지 않은 사용자로 질문 조회");
            // 인증되지 않은 사용자인 경우 답변 상태 없이 조회
            var questions = questionService.getAllQuestionsByField(fieldName);
            model.addAttribute("questions", questions.getList());
        }
        
        model.addAttribute("fieldName", fieldName);
        model.addAttribute("isAuthenticated", user != null);
        return "allQuestions";
    }
}
