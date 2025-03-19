package com.example.cat_service.interview.controller;

import com.example.cat_service.interview.entity.Field;
import com.example.cat_service.interview.service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionPageController {

    private final FieldService fieldService;

    /**
     * 1) Question 생성 폼 (SSR)
     *    GET /admin/questions/new
     */
    @GetMapping("/new")
    public String newQuestionPage(Model model) {
        // DB에 이미 존재하는 모든 Field 조회
        List<Field> allFields = fieldService.getAllFields();
        model.addAttribute("allFields", allFields);

        // "newQuestion" 템플릿으로 이동
        return "newQuestion";
        // -> src/main/resources/templates/newQuestion.html (경로는 자유롭게)
    }
}
