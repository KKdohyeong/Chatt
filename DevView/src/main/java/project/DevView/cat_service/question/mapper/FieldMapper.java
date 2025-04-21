// ───────────────────────────────────────────────────────────
// package: project.DevView.cat_service.question.mapper
// ───────────────────────────────────────────────────────────

package project.DevView.cat_service.question.mapper;

import project.DevView.cat_service.question.dto.request.FieldCreateRequestDto;
import project.DevView.cat_service.question.entity.Field;

public class FieldMapper {

    /** Request DTO → Field 엔티티 */
    public static Field from(FieldCreateRequestDto request) {
        return Field.builder()
                .name(request.name())
                .build();
    }
}
