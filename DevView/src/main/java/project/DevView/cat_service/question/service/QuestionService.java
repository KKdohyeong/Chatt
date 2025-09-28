package project.DevView.cat_service.question.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import project.DevView.cat_service.global.dto.response.result.ListResult;
import project.DevView.cat_service.global.dto.response.result.SingleResult;
import project.DevView.cat_service.global.service.ResponseService;
import project.DevView.cat_service.question.dto.request.QuestionCreateRequest;
import project.DevView.cat_service.question.dto.response.QuestionResponseDto;
import project.DevView.cat_service.question.dto.response.QuestionWithStatusDto;
import project.DevView.cat_service.question.dto.response.QuestionPageResponse;
import project.DevView.cat_service.question.entity.Field;
import project.DevView.cat_service.question.entity.Question;
import project.DevView.cat_service.question.entity.FieldQuestionCount;
import project.DevView.cat_service.question.repository.QuestionRepository;
import project.DevView.cat_service.question.repository.FieldQuestionCountRepository;
import project.DevView.cat_service.question.repository.UserQuestionHistoryRepository;
import project.DevView.cat_service.question.mapper.QuestionMapper;
import project.DevView.cat_service.resume.dto.TagQuestionResponse;
import project.DevView.cat_service.resume.entity.ResumeTag;
import project.DevView.cat_service.resume.entity.TagQuestion;
import project.DevView.cat_service.resume.repository.ResumeTagRepository;
import project.DevView.cat_service.resume.repository.TagQuestionRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final FieldQuestionCountRepository fieldQuestionCountRepository;
    private final UserQuestionHistoryRepository historyRepository;
    private final TagQuestionRepository tagQuestionRepository;

    /**
     * 질문 생성 및 Field 연결
     */
    @Transactional
    public SingleResult<QuestionResponseDto> createQuestion(QuestionCreateRequest request) {
        Field field = Field.fromName(request.field());
        Question question = Question.create(field, request.question(), request.answer());

        // 저장
        Question saved = questionRepository.save(question);

        // FieldQuestionCount 업데이트
        int result = fieldQuestionCountRepository.increase(field.name());
        if (result == 0) {
            fieldQuestionCountRepository.save(
                    FieldQuestionCount.init(field, 1L)
            );
        }

        // Entity → Response DTO
        QuestionResponseDto dto = QuestionResponseDto.of(saved);

        // SingleResult 포장하여 반환
        return ResponseService.getSingleResult(dto);
    }
    
    /**
     * 특정 분야의 모든 질문 조회
     */
    public ListResult<QuestionResponseDto> getAllQuestionsByField(String fieldName) {
        Field field = Field.fromName(fieldName);

        List<Question> questions = questionRepository.findByField(field);
        
        List<QuestionResponseDto> dtoList = questions.stream().map(QuestionResponseDto::of).collect(Collectors.toList());
                
        return ResponseService.getListResult(dtoList);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveQuestions(ResumeTag tag, TagQuestionResponse resp) {
        var entities = resp.getQuestions().stream()
                .map(q -> TagQuestion.builder()
                        .resumeTag(tag)
                        .baseQuestion(q.getBaseQuestion())
                        .createdQuestion(q.getCreatedQuestion())
                        .isCompleted(false)
                        .isAsked(false)
                        .build())
                .toList();
        tagQuestionRepository.saveAll(entities);
    }


    /**
     * 특정 분야의 모든 질문 조회 (유저의 답변 상태 포함)
     */
    public ListResult<QuestionWithStatusDto> getAllQuestionsWithStatusByField(Long userId, String fieldName) {
        Field field = Field.fromName(fieldName);
        List<Question> questions = questionRepository.findByField(field);
                
        // 2. 유저가 답변한 질문 ID 목록 조회
        List<Long> answeredIds = historyRepository.findAnsweredQuestionIds(userId);
        Set<Long> answeredIdSet = new HashSet<>(answeredIds);
        
        // 3. 질문 목록에 답변 여부 표시하여 반환
        List<QuestionWithStatusDto> result = questions.stream()
                .map(q -> new QuestionWithStatusDto(
                        q.getId(),
                        q.getQuestion(),
                        q.getAnswer(),
                        q.getField().toString(),
                        answeredIdSet.contains(q.getId())
                ))
                .collect(Collectors.toList());
                
        return ResponseService.getListResult(result);
    }

    /**
     * 단일 질문 조회
     */
    public QuestionResponseDto readQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        return QuestionResponseDto.of(question);
    }

    /**
     * 질문 수정
     */
    @Transactional
    public QuestionResponseDto updateQuestion(Long questionId, String question, String answer) {
        Question existingQuestion = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        
        existingQuestion.update(question, answer);
        Question saved = questionRepository.save(existingQuestion);
        
        return QuestionResponseDto.of(saved);
    }

    /**
     * 질문 삭제
     */
    @Transactional
    public void deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        
        questionRepository.delete(question);
        fieldQuestionCountRepository.decrease(question.getField().name());
    }

    /**
     * 페이지네이션을 통한 필드별 질문 조회
     */
    public QuestionPageResponse readAllByField(String fieldName, Long page, Long pageSize) {
        return QuestionPageResponse.of(
                questionRepository.findAllByField(fieldName, (page - 1) * pageSize, pageSize).stream()
                        .map(QuestionResponseDto::of)
                        .toList(),
                questionRepository.countByFieldLimited(
                        fieldName,
                        PageLimitCalculator.calculatePageLimit(page, pageSize, 10L)
                )
        );
    }

    /**
     * 필드별 질문 개수 조회
     */
    public Long countByField(String fieldName, Long page, Long pageSize) {
        return questionRepository.countByFieldLimited(fieldName, PageLimitCalculator.calculatePageLimit(page, pageSize, 10L));
    }
}
