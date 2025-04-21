package project.DevView.cat_service.question.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.DevView.cat_service.global.dto.response.result.SingleResult;
import project.DevView.cat_service.global.service.ResponseService;
import project.DevView.cat_service.question.dto.request.QuestionCreateRequest;
import project.DevView.cat_service.question.dto.response.QuestionResponseDto;
import project.DevView.cat_service.question.entity.Question;
import project.DevView.cat_service.question.entity.Field;
import project.DevView.cat_service.question.repository.QuestionRepository;
import project.DevView.cat_service.question.repository.FieldRepository;
import project.DevView.cat_service.question.mapper.QuestionMapper;


import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final FieldRepository fieldRepository;

    /**
     * 질문 생성 및 Field 연결
     */
    public SingleResult<QuestionResponseDto> createQuestion(QuestionCreateRequest request) {
        // 1) 요청된 ID들로 Field 엔티티 조회
        List<Field> fields = fieldRepository.findAllById(request.fieldIds());

        // 2) DTO → Entity 변환 (Mapper 사용)
        Question question = QuestionMapper.from(request, new HashSet<>(fields));

        // 3) 저장
        Question saved = questionRepository.save(question);

        // 4) Entity → Response DTO
        QuestionResponseDto dto = new QuestionResponseDto(
                saved.getId(),
                saved.getContent(),
                saved.getFields()
                        .stream()
                        .map(Field::getName)
                        .collect(Collectors.toList())
        );

        // 5) SingleResult 포장하여 반환
        return ResponseService.getSingleResult(dto);
    }
}
