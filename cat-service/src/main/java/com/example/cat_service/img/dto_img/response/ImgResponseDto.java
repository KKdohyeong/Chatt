package com.example.cat_service.img.dto_img.response;


import com.example.cat_service.img.entity.Image;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
// 이미지 하나를 반환할 때 이 정보를 준다.
public class ImgResponseDto {
    private Long id;
    private String img_name;
    private String img_path;

    public ImgResponseDto(Image img){
        this.id = img.getId();
        this.img_name = img.getImg_name();
        this.img_path = img.getImg_path();

    }
}
