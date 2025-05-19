package project.DevView.cat_service.question.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.DevView.cat_service.global.dto.response.result.ListResult;
import project.DevView.cat_service.global.dto.response.result.SingleResult;
import project.DevView.cat_service.global.service.ResponseService;
import project.DevView.cat_service.question.dto.request.QuestionCreateRequest;
import project.DevView.cat_service.question.dto.response.QuestionResponseDto;
import project.DevView.cat_service.question.dto.response.QuestionWithStatusDto;
import project.DevView.cat_service.question.entity.Question;
import project.DevView.cat_service.question.entity.Field;
import project.DevView.cat_service.question.entity.UserQuestionHistory;
import project.DevView.cat_service.question.repository.QuestionRepository;
import project.DevView.cat_service.question.repository.FieldRepository;
import project.DevView.cat_service.question.repository.UserQuestionHistoryRepository;
import project.DevView.cat_service.question.mapper.QuestionMapper;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final FieldRepository fieldRepository;
    private final UserQuestionHistoryRepository historyRepository;

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
    
    /**
     * 특정 분야의 모든 질문 조회
     */
    public ListResult<QuestionResponseDto> getAllQuestionsByField(String fieldName) {
        Field field = fieldRepository.findByName(fieldName)
                .orElseThrow(() -> new RuntimeException("해당 분야가 존재하지 않습니다: " + fieldName));
                
        List<Question> questions = questionRepository.findAll();
        
        List<QuestionResponseDto> dtoList = questions.stream()
                .filter(q -> q.getFields().contains(field))
                .map(q -> new QuestionResponseDto(
                        q.getId(),
                        q.getContent(),
                        q.getFields().stream().map(Field::getName).collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
                
        return ResponseService.getListResult(dtoList);
    }
    
    /**
     * 특정 분야의 모든 질문 조회 (유저의 답변 상태 포함)
     */
    public ListResult<QuestionWithStatusDto> getAllQuestionsWithStatusByField(Long userId, String fieldName) {
        // 1. 해당 분야의 모든 질문 조회
        Field field = fieldRepository.findByName(fieldName)
                .orElseThrow(() -> new RuntimeException("해당 분야가 존재하지 않습니다: " + fieldName));
                
        List<Question> allQuestions = questionRepository.findAll().stream()
                .filter(q -> q.getFields().contains(field))
                .collect(Collectors.toList());
                
        // 2. 유저가 답변한 질문 ID 목록 조회
        List<Long> answeredIds = historyRepository.findAnsweredQuestionIds(userId);
        Set<Long> answeredIdSet = new HashSet<>(answeredIds);
        
        // 3. 질문 목록에 답변 여부 표시하여 반환
        List<QuestionWithStatusDto> result = allQuestions.stream()
                .map(q -> new QuestionWithStatusDto(
                        q.getId(),
                        q.getContent(),
                        q.getFields().stream().map(Field::getName).collect(Collectors.toList()),
                        answeredIdSet.contains(q.getId())
                ))
                .collect(Collectors.toList());
                
        return ResponseService.getListResult(result);
    }
}
