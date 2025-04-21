package project.DevView.cat_service.interview.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Field 조회/생성 결과용 DTO
 */
@Getter
@Setter
@AllArgsConstructor
public class FieldResponseDto {
    private Long id;
    private String name;
}
