package project.DevView.cat_service.question.controller;

import project.DevView.cat_service.question.entity.Field;
import project.DevView.cat_service.question.service.FieldService;
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
        List<Field> allFields = fieldService.getAllFieldEntities();
        model.addAttribute("allFields", allFields);
        return "newQuestion";
    }

}
