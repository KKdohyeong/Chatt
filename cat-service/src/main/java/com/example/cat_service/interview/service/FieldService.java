package com.example.cat_service.interview.service;

import com.example.cat_service.interview.entity.Field;
import com.example.cat_service.interview.repository.FieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FieldService {

    private final FieldRepository fieldRepository;


    public Field createField(String name) {
        Field field = new Field();
        field.setName(name);
        field.setCreatedAt(LocalDateTime.now());
        // 필요하다면 createdBy, modifiedBy 등도 설정

        return fieldRepository.save(field);
    }

    /**
     * PK로 Field 조회
     */
    public Field getFieldById(Long id) {
        return fieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Field not found. id=" + id));
    }


    /**
     * name=... 인 Field 엔티티를 찾고,
     * 없으면 RuntimeException 등 던짐
     */
    public Field getFieldByName(String fieldName) {
        return fieldRepository.findByName(fieldName)
                .orElseThrow(() -> new RuntimeException("Field not found: " + fieldName));
    }

    /**
     * 모든 Field 엔티티 반환
     */
    public List<Field> getAllFields() {
        return fieldRepository.findAll();
    }
}
