package project.DevView.cat_service.interview.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * Field 생성 시 클라이언트에서 보낼 Request DTO
 */
@Getter
@Setter
public class FieldCreateRequestDto {
    private String name;
}
