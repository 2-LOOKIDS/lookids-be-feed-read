package lookids.feedread.dto.in;

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
	private List<String> tagList;
	private boolean state;
	private List<String> mediaUrlList;
	private LocalDateTime createdAt;


	@Builder
	public FeedKafkaDto(String feedCode, String uuid, String petCode, String content,
		List<String> tagList, boolean state, List<String> mediaUrlList, LocalDateTime createdAt) {
		this.feedCode = feedCode;
		this.uuid = uuid;
		this.petCode = petCode;
		this.content = content;
		this.tagList = tagList;
		this.state = state;
		this.mediaUrlList = mediaUrlList;
		this.createdAt = createdAt;
	}
}
