package project.DevView.cat_service.img.repository;

import project.DevView.cat_service.img.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImgRepository extends JpaRepository<Image, Long> {
    List<Image> findAllByOrderByIdDesc();

}
