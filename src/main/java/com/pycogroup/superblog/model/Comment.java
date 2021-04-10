package com.pycogroup.superblog.model;

import com.querydsl.core.annotations.QueryEntity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "comments")
@Builder
@QueryEntity
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @Getter
    private String commentId;

    @Getter
    @Setter
    private String content;

    @Getter
    @Setter
    private String userId;

    @Getter
    @Setter
    private String userName;

    @Getter
    @Setter
    private String articleId;

    @Getter
    @Setter
    private LocalDateTime commentedAt;

    @Getter
    @Setter
    @Builder.Default
    private String status = "pending";
}
