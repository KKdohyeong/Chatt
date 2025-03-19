package com.example.cat_service.img.controller;


import com.example.cat_service.img.dto_img.request.ImgRequestDto;
import com.example.cat_service.img.dto_img.response.ImgResponseDto;
import com.example.cat_service.img.dto_img.response.SuccessResponseDto;
import com.example.cat_service.img.service.ImgService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/img")
@RequiredArgsConstructor
@RestController
public class ImgController {

    private final ImgService imgService;

    @GetMapping("/test")
    public String test(){
        return "test";
    }

    //전체 목록 조회
    @GetMapping("/imgs")
    public List<ImgResponseDto> getAllImgs() {
        return imgService.getImgs();
    }

    //이미지 등록
    @PostMapping("/img")
    public ImgResponseDto createImg(@RequestBody ImgRequestDto imgRequestDto){
        return imgService.createImg(imgRequestDto);
    }

    // 이미지 검색
    @GetMapping("/img/{id}")
    public ImgResponseDto getImg(@PathVariable Long id){
        return imgService.getImg(id);
    }

    // 이미지 수정
    @PutMapping("/img/{id}")
    public ImgResponseDto updateImg(@PathVariable Long id,@RequestBody ImgRequestDto imgRequestDto) throws Exception{
        return imgService.updateImg(id, imgRequestDto);
    }

    // 이미지 삭제
    @DeleteMapping("/img/{id}")
    public SuccessResponseDto deleteImg(@PathVariable Long id) throws Exception{
        return imgService.deleteImg(id);
    }
}
