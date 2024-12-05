package lookids.feedread.dto.out;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lookids.feedread.domain.FeedRead;

@Getter
@NoArgsConstructor
public class FeedReadResponseDto {

	private String feedCode;
	private String mediaUrl;

	@Builder
	public FeedReadResponseDto(String feedCode, String mediaUrl) {
		this.feedCode = feedCode;
		this.mediaUrl = mediaUrl;
	}

	public static FeedReadResponseDto toDto(FeedRead feedRead) {
		return FeedReadResponseDto.builder()
			.feedCode(feedRead.getFeedCode())
			.mediaUrl(feedRead.getMediaUrlList() != null && !feedRead.getMediaUrlList().isEmpty()
				?feedRead.getMediaUrlList().get(0) : null)
			.build();
	}
}
