package com.zerobase.challengeproject.comment.repository;

import com.zerobase.challengeproject.comment.entity.DietComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DietCommentRepository extends JpaRepository<DietComment, Long>, DietCommentRepositoryCustom {
}
