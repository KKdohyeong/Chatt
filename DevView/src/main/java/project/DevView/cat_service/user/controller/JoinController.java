package project.DevView.cat_service.user.controller;


import project.DevView.cat_service.user.dto.JoinDTO;
import project.DevView.cat_service.user.entity.UserEntity;
import project.DevView.cat_service.user.service.JoinService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class JoinController {

    private static final Logger logger = LoggerFactory.getLogger(JoinController.class);

    private final JoinService joinService;

    @PostMapping("/join")
    public ResponseEntity<UserEntity> joinProcess(@RequestBody JoinDTO joinDto) {
        logger.info("Received join request: {}", joinDto);
        try {
            UserEntity userEntity = joinService.joinProcess(joinDto);
            if (userEntity == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 사용자 이미 존재
            }
            logger.info("User registered successfully: {}", userEntity.getUsername());
            return ResponseEntity.ok(userEntity); // UserEntity를 반환
        } catch (Exception e) {
            logger.error("Error during user registration: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}