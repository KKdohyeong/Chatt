package project.DevView.cat_service.interview.controller;

import project.DevView.cat_service.question.entity.Field;
import project.DevView.cat_service.question.service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class InterviewPageController {

    private final FieldService fieldService;

    // 2번째 페이지(챗 화면)를 위한 SSR 컨트롤러
    @GetMapping("/interview")
    public String showInterviewPage(Model model) {
        //  여기만 바꿔주세요: getAllFields() → getAllFieldEntities()
        List<Field> fields = fieldService.getAllFieldEntities();
        model.addAttribute("fields", fields);

        return "interviewPage";
    }

    // 북마크 모아보기 페이지
    @GetMapping("/bookmarks")
    public String showBookmarks(
            @AuthenticationPrincipal int userId,
            Model model
    ) {
        return "bookmarkPage";
    }
}
