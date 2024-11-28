package lookids.feedread.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.bson.Document;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lookids.feedread.domain.FeedRead;

@Getter
@NoArgsConstructor
@ToString
public class FeedListResponseDto {
	// private String uuid;
	private String nickname;
	private String tag;
	private String image;
	private String feedCode;
	private List<String> mediaUrlList;
	private String content;
	private LocalDateTime createdAt;

	@Builder
	public FeedListResponseDto(String uuid, String nickname, String tag, String image, String feedCode, List<String> mediaUrlList, String content, LocalDateTime createdAt) {
		// this.uuid = uuid;
		this.nickname = nickname;
		this.tag = tag;
		this.image = image;
		this.feedCode = feedCode;
		this.mediaUrlList = mediaUrlList;
		this.content = content;
		this.createdAt = createdAt;
	}

	public static FeedListResponseDto toDto(FeedRead feedRead) {
		return FeedListResponseDto.builder()
			.uuid(feedRead.getUuid())
			.nickname(feedRead.getNickname())
			.tag(feedRead.getTag())
			.image(feedRead.getImage())
			.feedCode(feedRead.getFeedCode())
			.content(feedRead.getContent())
			.mediaUrlList(feedRead.getMediaUrlList())
			.createdAt(feedRead.getCreatedAt()
				.atZone(ZoneId.systemDefault())
				.withZoneSameInstant(ZoneId.of("Asia/Seoul"))
				.toLocalDateTime())
			.build();
	}
}
