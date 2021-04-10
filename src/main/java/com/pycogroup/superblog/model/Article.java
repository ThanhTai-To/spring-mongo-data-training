package com.pycogroup.superblog.model;

import com.querydsl.core.annotations.QueryEntity;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "articles")
@Builder
@QueryEntity
@NoArgsConstructor
@AllArgsConstructor
public class Article {
	@Id
	@Getter
	private ObjectId articleId;

	@Getter
	@Setter
	private String title;


	@Getter
	@Setter
	private String content;

	@Getter
	@Setter
	private String authorId;

	@Getter
	@Setter
	private String authorName;

	@Getter
	@Setter
	private LocalDateTime createdAt;

	// It contains a array of category names
	@Getter
	@Setter
	private List<Category> categories;

	// It contains 10 latest comments
	@Getter
	@Setter
	private List<Comment> comments;

}
