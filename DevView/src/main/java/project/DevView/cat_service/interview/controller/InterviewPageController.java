package project.DevView.cat_service.interview.controller;

import project.DevView.cat_service.question.entity.Field;
import project.DevView.cat_service.question.service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Tag(name = "Interview Page", description = "인터뷰 SSR 페이지 관련 API")
public class InterviewPageController {

    private final FieldService fieldService;

    @GetMapping("/interview-mode")
    @Operation(summary = "면접 모드 선택 페이지", description = "면접 모드 선택 페이지 렌더링")
    public String showInterviewModeSelect() {
        return "interviewModeSelect";
    }

    @GetMapping("/resume-interview")
    @Operation(summary = "이력서 업로드 페이지", description = "이력서 업로드 페이지 렌더링")
    public String showResumeUpload() {
        return "resumeUpload";
    }

    // 기존 CS 기술 면접 페이지
    @GetMapping("/interview")
    @Operation(summary = "인터뷰 페이지 SSR", description = "CS 기술 면접 페이지 렌더링")
    public String showInterviewPage(Model model) {
        List<Field> fields = fieldService.getAllFieldEntities();
        model.addAttribute("fields", fields);
        return "interviewPage";
    }

    // 북마크 모아보기 페이지
    @GetMapping("/bookmarks")
    @Operation(summary = "북마크 모아보기 SSR", description = "북마크 모아보기 SSR 렌더링")
    public String showBookmarks() {
        return "bookmarkPage";
    }
}
