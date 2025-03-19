package com.example.cat_service.interview.controller;

import com.example.cat_service.interview.entity.Field;
import com.example.cat_service.interview.service.FieldService;
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
    public String showInterviewPage(
            Model model
    ) {
        // 사용 가능한 분야 리스트를 가져온 후 모델에 담아 템플릿에 넘겨줍니다.
        List<Field> fields = fieldService.getAllFields();
        model.addAttribute("fields", fields);

        // 인터뷰 목록(과거 히스토리)이나 북마크 목록 등을 필요하다면 같이 model에 담을 수 있습니다.
        // 예) model.addAttribute("interviews", interviewService.findByUser(userId));

        return "interviewPage"; // resources/templates/interviewPage.html 로 렌더링
    }

    // 북마크 모아보기 페이지
    @GetMapping("/bookmarks")
    public String showBookmarks(
            @AuthenticationPrincipal int userId,
            Model model
    ) {
        // 북마크 목록 등을 가져와서 model에 담고 반환
        // 예) model.addAttribute("bookmarks", bookmarkService.getByUser(userId));
        return "bookmarkPage";
    }
}
