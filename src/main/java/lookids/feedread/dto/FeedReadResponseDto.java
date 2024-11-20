package lookids.feedread.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lookids.feedread.domain.FeedRead;
import lookids.feedread.vo.out.FeedReadResponseVo;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedReadResponseDto {

	private String feedCode;
	private List<String> mediaUrl;


	public static FeedReadResponseDto toDto(FeedRead feedRead) {
		return FeedReadResponseDto.builder()
			.feedCode(feedRead.getFeedCode())
			.mediaUrl(feedRead.getMediaUrl())
			.build();
	}

	public FeedReadResponseVo toVo() {
		return FeedReadResponseVo.builder()
			.feedCode(feedCode)
			.mediaUrl(mediaUrl)
			.build();
	}
}
