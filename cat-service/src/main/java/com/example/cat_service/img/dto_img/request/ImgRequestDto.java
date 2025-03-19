package com.example.cat_service.img.dto_img.request;

import lombok.Getter;

@Getter

// 이미지 등록 -> 모든 값 필요
public class ImgRequestDto {
    private String img_name;
    private String img_path;

}
