package project.DevView.cat_service.img.entity;


import project.DevView.cat_service.img.dto_img.request.ImgRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Image extends TimeStamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String img_name;
    @Column(nullable = false)
    private String img_path;

    public Image(Long id, String name, String path){
        this.id = id;
        this.img_name = name;
        this.img_path = path;
    }

    public Image(ImgRequestDto requestDto){
        this.img_name = requestDto.getImg_name();
        this.img_path = requestDto.getImg_path();
    }

    public void update(ImgRequestDto requestDto){
        this.img_name = requestDto.getImg_name();
        this.img_path = requestDto.getImg_path();
    }
}
