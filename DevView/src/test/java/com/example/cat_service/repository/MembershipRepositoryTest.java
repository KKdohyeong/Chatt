package com.example.cat_service.repository;


import com.example.cat_service.membership.Membership;
import com.example.cat_service.membership.MembershipRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


@DataJpaTest
public class MembershipRepositoryTest {

    @Autowired
    private MembershipRepository membershipRepository;

    @Test
    public void MembershipRepositoryIsNotNUll(){
        Assertions.assertThat(membershipRepository).isNotNull();
    }


}
