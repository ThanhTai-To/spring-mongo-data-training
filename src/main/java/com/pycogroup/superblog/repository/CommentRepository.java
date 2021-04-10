package com.pycogroup.superblog.repository;

import com.pycogroup.superblog.model.Comment;
import org.bson.types.ObjectId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface CommentRepository extends PagingAndSortingRepository<Comment, ObjectId>,
        QuerydslPredicateExecutor<Comment> {
    Comment findByCommentId(ObjectId commentId);
    void deleteAllByArticleId(String articleId);
}
