package com.zerobase.challengeproject.challenge.repository;


import com.zerobase.challengeproject.challenge.entity.Challenge;
import com.zerobase.challengeproject.type.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long>, ChallengeRepositoryCustom {

  Page<Challenge> findAll(Pageable pageable);

  Page<Challenge> findByMemberId(Long memberId, Pageable pageable);

  Page<Challenge> findByTitleContaining(String title, Pageable pageable);

    Page<Challenge> findByCategoryType(CategoryType categoryType, Pageable pageable);


    /**
     * 챌린지 제목으로 검색, title컬럼으로 full text index생성
     * @param title
     * @param pageable
     * @return
     */
    @Query(value = "SELECT * FROM challenge WHERE MATCH(title) AGAINST(:title IN BOOLEAN MODE)",
            countQuery = "SELECT COUNT(*) FROM challenge WHERE MATCH(title) AGAINST(:title IN BOOLEAN MODE)",
            nativeQuery = true)
    Page<Challenge> searchByTitleFullText(@Param("title") String title, Pageable pageable);
}
