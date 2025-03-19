package com.example.cat_service.interview.service;

import com.example.cat_service.interview.dto.QuestionResponseDto;
import com.example.cat_service.interview.entity.Field;
import com.example.cat_service.interview.entity.Question;
import com.example.cat_service.interview.repository.FieldRepository;
import com.example.cat_service.interview.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final FieldRepository fieldRepository;

    /**
     * Question 생성 + 복수 Field 연결
     */
    public QuestionResponseDto createQuestion(String content, List<Long> fieldIds) {
        // 1) DB에서 fieldIds에 해당하는 Field 엔티티들 조회
        List<Field> fields = fieldRepository.findAllById(fieldIds);

        // 2) Question 엔티티 생성
        Question question = new Question();
        question.setContent(content);
        question.setCreatedAt(LocalDateTime.now());
        // etc...

        // 3) Question-Field 연결 (ManyToMany)
        //    question의 fields: Set<Field>이므로 addAll 사용
        question.getFields().addAll(fields);

        // 4) DB에 저장
        Question saved = questionRepository.save(question);

        // 5) 반환 DTO 생성
        return new QuestionResponseDto(
                saved.getId(),
                saved.getContent(),
                saved.getFields().stream()
                        .map(Field::getName)
                        .collect(Collectors.toList())
        );
    }
}
