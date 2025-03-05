package org.example.sberproject.repository;

import org.example.sberproject.entity.User;
import org.example.sberproject.entity.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserImageRepository extends JpaRepository<UserImage, Long> {
    List<UserImage> findByUser(User user);
    void deleteAllByUser(User user);
}
