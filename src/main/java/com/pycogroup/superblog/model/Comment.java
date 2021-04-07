package com.pycogroup.superblog.model;

import com.querydsl.core.annotations.QueryEntity;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "comments")
@Builder
@QueryEntity
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Getter
    @Setter
    private String commentId;

    @Getter
    @Setter
    private String content;

    @Getter
    @Setter
    private Date commentedAt;

    @Getter
    @Setter
    private String userEmail;

    @Getter
    @Setter
    private String userName;

    @Getter
    @Setter
    @Builder.Default
    private String status = "pending";

}
