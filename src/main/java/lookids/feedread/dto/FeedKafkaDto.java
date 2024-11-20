package lookids.feedread.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class FeedKafkaDto {

	private String feedCode;
	private String uuid;
	private String petCode;
	private String content;
	private List<String> tags;
	private boolean state;
	private List<String> mediaUrl;
	private LocalDateTime createdAt;


	@Builder
	public FeedKafkaDto(String feedCode, String uuid, String petCode, String content,
		List<String> tags, boolean state, List<String> mediaUrl, LocalDateTime createdAt) {
		this.feedCode = feedCode;
		this.uuid = uuid;
		this.petCode = petCode;
		this.content = content;
		this.tags = tags;
		this.state = state;
		this.mediaUrl = mediaUrl;
		this.createdAt = createdAt;
	}
}
