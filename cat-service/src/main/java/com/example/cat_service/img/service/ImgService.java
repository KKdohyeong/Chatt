package com.example.cat_service.img.service;

import com.example.cat_service.img.dto_img.request.ImgRequestDto;
import com.example.cat_service.img.dto_img.response.ImgResponseDto;
import com.example.cat_service.img.dto_img.response.SuccessResponseDto;
import com.example.cat_service.img.entity.Image;
import com.example.cat_service.img.repository.ImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImgService {
    private final ImgRepository imgRepository;

    public List<ImgResponseDto> getImgs(){
        return imgRepository.findAllByOrderByIdDesc().stream().map(ImgResponseDto::new).toList();
    }

    @Transactional
    public ImgResponseDto createImg(ImgRequestDto imgRequestDto){
        Image img = new Image(imgRequestDto);
        imgRepository.save(img);
        return new ImgResponseDto(img);
    }

    @Transactional
    public ImgResponseDto getImg(Long id){
        return imgRepository.findById(id).map(ImgResponseDto::new).orElseThrow(
                () -> new IllegalArgumentException("아이디가 존재하지 않습니다.")
        );
    }

    @Transactional
    public ImgResponseDto updateImg(Long id, ImgRequestDto imgRequestDto) throws Exception{
        Image image = imgRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("아이디가 존재하지 않습니다.")
        );
        image.update(imgRequestDto);
        return new ImgResponseDto(image);
    }


    @Transactional
    public SuccessResponseDto deleteImg(Long id) throws Exception{
        Image image = imgRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("아이디가 존재하지 않습니다.")
        );

        imgRepository.deleteById(id);
        return new SuccessResponseDto(true);
    }
}
