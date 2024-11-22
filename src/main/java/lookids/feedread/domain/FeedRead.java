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
	private List<String> tags;
	private List<String> mediaUrl;
	private LocalDateTime createdAt;


	@Builder
	public FeedRead(ObjectId id, String feedCode, String uuid, String petCode, String content,
		List<String> tags, boolean state, List<String> mediaUrl, LocalDateTime createdAt, String nickname, String image) {
		this.id = id;
		this.feedCode = feedCode;
		this.uuid = uuid;
		this.content = content;
		this.state = state;
		this.petCode = petCode;
		this.tags = tags;
		this.mediaUrl = mediaUrl;
		this.createdAt = createdAt;
		this.nickname = nickname;
		this.image = image;
	}

}
