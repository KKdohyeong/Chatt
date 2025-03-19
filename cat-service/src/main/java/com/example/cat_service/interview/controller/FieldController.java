package com.example.cat_service.interview.controller;

import com.example.cat_service.interview.dto.FieldCreateRequestDto;
import com.example.cat_service.interview.dto.FieldResponseDto;
import com.example.cat_service.interview.entity.Field;
import com.example.cat_service.interview.service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fields")
@RequiredArgsConstructor
public class FieldController {
    private final FieldService fieldService;

    /**
     * 1) 새 Field 추가
     */
    @PostMapping
    public ResponseEntity<FieldResponseDto> createField(@RequestBody FieldCreateRequestDto request) {
        Field createdField = fieldService.createField(request.getName());
        FieldResponseDto response = new FieldResponseDto(
                createdField.getId(),
                createdField.getName()
        );
        return ResponseEntity.ok(response);
    }
}
