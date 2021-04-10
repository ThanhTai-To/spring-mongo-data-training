package com.pycogroup.superblog.repository;

import java.util.List;

public interface CustomArticleRepository {
    void updateArticlesCategoryName(String categoryId, String newCategoryName);
    List<String> getListCategoryName();
    void deleteArticleCategoryByCategoryId(String categoryId);

    void updateCommentStatusByCommentId(String articleId, String commentId, String status);

    void deleteCommentByCommentId(String articleId, String commentId);
}
