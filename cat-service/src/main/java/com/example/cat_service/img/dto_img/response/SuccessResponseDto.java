package com.example.cat_service.img.dto_img.response;



// 이미지 삭제를 성공적으로 햇을 때의 성공 response
// 왜 이것을 만드느냐? HTTP 통신에서는 클라이언트가 서버에 요청을 하면 항상? 응답을 보낸다.
// 수정/등록/조회의 경우 해당하는 객체를 return하면 되지만 삭제의 경우엔 해당하는 객체가 존재하지 않는다.
// 따라서 successResponse class를 만들어서 그 객체를 보낼 예정이다.
public class SuccessResponseDto {
    private boolean success;

    public SuccessResponseDto(boolean success){
        this.success = success;
    }
}
//https://velog.io/@kimdy0915/%EC%8A%A4%ED%94%84%EB%A7%81-%EB%B6%80%ED%8A%B8Spring-Boot%EB%A1%9C-%EA%B2%8C%EC%8B%9C%ED%8C%90-%EB%B2%A1%EC%97%94%EB%93%9C-%EC%84%9C%EB%B2%84-%EB%A7%8C%EB%93%A4%EA%B8%B0
//위의 사이트 참고, 난 아직 안만듦