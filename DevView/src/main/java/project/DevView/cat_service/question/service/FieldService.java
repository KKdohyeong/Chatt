package project.DevView.cat_service.question.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.DevView.cat_service.global.exception.CustomException;
import project.DevView.cat_service.global.exception.ErrorCode;
import project.DevView.cat_service.global.service.ResponseService;
import project.DevView.cat_service.global.dto.response.result.ListResult;
import project.DevView.cat_service.global.dto.response.result.SingleResult;
import project.DevView.cat_service.question.dto.request.FieldCreateRequestDto;
import project.DevView.cat_service.question.dto.response.FieldResponseDto;
import project.DevView.cat_service.question.entity.Field;
import project.DevView.cat_service.question.mapper.FieldMapper;
import project.DevView.cat_service.question.repository.FieldRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FieldService {

    private final FieldRepository fieldRepository;

    /** 신규 Field 생성 */
    public SingleResult<FieldResponseDto> createField(FieldCreateRequestDto request) {
        Field toSave = FieldMapper.from(request);
        Field saved  = fieldRepository.save(toSave);
        FieldResponseDto dto = new FieldResponseDto(saved.getId(), saved.getName());
        return ResponseService.getSingleResult(dto);
    }

    /** Field 엔티티 조회 (비즈니스 레이어 내부용) */
    public Field getFieldEntityByName(String name) {
        return fieldRepository.findByName(name)
                .orElseThrow(() -> new CustomException(ErrorCode.FIELD_NOT_EXIST));
    }

    /** name으로 Field 조회 (API 응답용) */
    public SingleResult<FieldResponseDto> getFieldByName(String name) {
        Field f = getFieldEntityByName(name);
        return ResponseService.getSingleResult(new FieldResponseDto(f.getId(), f.getName()));
    }

    /** ID로 Field 조회 (API 응답용) */
    public SingleResult<FieldResponseDto> getFieldById(Long id) {
        Field f = fieldRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.FIELD_NOT_EXIST));
        return ResponseService.getSingleResult(new FieldResponseDto(f.getId(), f.getName()));
    }

    /** 모든 Field 목록 조회 (API 응답용) */
    public ListResult<FieldResponseDto> getAllFields() {
        List<Field> all = fieldRepository.findAll();
        List<FieldResponseDto> dtos = all.stream()
                .map(f -> new FieldResponseDto(f.getId(), f.getName()))
                .collect(Collectors.toList());
        return ResponseService.getListResult(dtos);
    }

    /** SSR/내부 로직용 엔티티 리스트 조회 */
    public List<Field> getAllFieldEntities() {
        return fieldRepository.findAll();
    }
}
