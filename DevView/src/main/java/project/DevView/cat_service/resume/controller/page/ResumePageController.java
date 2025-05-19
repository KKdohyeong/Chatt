package project.DevView.cat_service.resume.controller.page;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import project.DevView.cat_service.resume.service.ResumeService;
import project.DevView.cat_service.resume.dto.ResumeResponse;

@Slf4j
@Controller
@RequestMapping("/resume-interview")
@RequiredArgsConstructor
@Tag(name = "Resume Page", description = "이력서 관련 페이지 API")
public class ResumePageController {

    private final ResumeService resumeService;

    @GetMapping("/{resumeId}/tags")
    @Operation(summary = "이력서 태그 생성 페이지", description = "이력서 태그 생성 페이지 렌더링")
    public String showResumeTags(@PathVariable Long resumeId, Model model) {
        log.info("[ResumePageController] showResumeTags called with resumeId={}", resumeId);
        try {
            ResumeResponse resume = resumeService.getResume(resumeId);
            model.addAttribute("resumeId", resumeId);
            model.addAttribute("resume", resume);
            log.info("[ResumePageController] Resume found: {}", resume);
            return "resumeTags";  // templates/resumeTags.html
        } catch (IllegalArgumentException e) {
            log.error("[ResumePageController] Resume not found: {}", resumeId, e);
            throw e;
        }
    }
} 