package com.zerobase.challengeproject.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = -1516561471L;

    public static final QMember member = new QMember("member1");

    public final NumberPath<Long> account = createNumber("account", Long.class);

    public final ListPath<com.zerobase.challengeproject.account.entity.AccountDetail, com.zerobase.challengeproject.account.entity.QAccountDetail> accountDetails = this.<com.zerobase.challengeproject.account.entity.AccountDetail, com.zerobase.challengeproject.account.entity.QAccountDetail>createList("accountDetails", com.zerobase.challengeproject.account.entity.AccountDetail.class, com.zerobase.challengeproject.account.entity.QAccountDetail.class, PathInits.DIRECT2);

    public final ListPath<com.zerobase.challengeproject.comment.entity.CoteComment, com.zerobase.challengeproject.comment.entity.QCoteComment> coteComments = this.<com.zerobase.challengeproject.comment.entity.CoteComment, com.zerobase.challengeproject.comment.entity.QCoteComment>createList("coteComments", com.zerobase.challengeproject.comment.entity.CoteComment.class, com.zerobase.challengeproject.comment.entity.QCoteComment.class, PathInits.DIRECT2);

    public final StringPath email = createString("email");

    public final DateTimePath<java.time.LocalDateTime> emailAuthDate = createDateTime("emailAuthDate", java.time.LocalDateTime.class);

    public final StringPath emailAuthKey = createString("emailAuthKey");

    public final BooleanPath emailAuthYn = createBoolean("emailAuthYn");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<com.zerobase.challengeproject.challenge.entity.MemberChallenge, com.zerobase.challengeproject.challenge.entity.QMemberChallenge> memberChallenges = this.<com.zerobase.challengeproject.challenge.entity.MemberChallenge, com.zerobase.challengeproject.challenge.entity.QMemberChallenge>createList("memberChallenges", com.zerobase.challengeproject.challenge.entity.MemberChallenge.class, com.zerobase.challengeproject.challenge.entity.QMemberChallenge.class, PathInits.DIRECT2);

    public final StringPath memberId = createString("memberId");

    public final StringPath memberName = createString("memberName");

    public final EnumPath<com.zerobase.challengeproject.type.MemberType> memberType = createEnum("memberType", com.zerobase.challengeproject.type.MemberType.class);

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final StringPath phoneNum = createString("phoneNum");

    public final StringPath previousEmail = createString("previousEmail");

    public final DateTimePath<java.time.LocalDateTime> registerDate = createDateTime("registerDate", java.time.LocalDateTime.class);

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

