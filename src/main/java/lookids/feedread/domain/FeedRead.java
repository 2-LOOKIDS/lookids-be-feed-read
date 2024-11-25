package lookids.feedread.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeedRead {
	@Id
	private ObjectId id;
	private String feedCode;
	private String uuid;
	private String nickname;
	private String image;
	private String content;
	private boolean state;
	private String petCode;
	private List<String> tagList;
	private List<String> mediaUrlList;
	private LocalDateTime createdAt;


	@Builder
	public FeedRead(ObjectId id, String feedCode, String uuid, String petCode, String content,
		List<String> tagList, boolean state, List<String> mediaUrlList, LocalDateTime createdAt, String nickname, String image) {
		this.id = id;
		this.feedCode = feedCode;
		this.uuid = uuid;
		this.content = content;
		this.state = state;
		this.petCode = petCode;
		this.tagList = tagList;
		this.mediaUrlList = mediaUrlList;
		this.createdAt = createdAt;
		this.nickname = nickname;
		this.image = image;
	}

}
