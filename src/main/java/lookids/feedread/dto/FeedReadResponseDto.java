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
	private List<String> mediaUrlList;


	public static FeedReadResponseDto toDto(FeedRead feedRead) {
		return FeedReadResponseDto.builder()
			.feedCode(feedRead.getFeedCode())
			.mediaUrlList(feedRead.getMediaUrlList() != null && !feedRead.getMediaUrlList().isEmpty()
				? List.of(feedRead.getMediaUrlList().get(0)) : List.of()) // 첫 번째 mediaUrl 보여주도록 설정
			// .mediaUrlList(feedRead.getMediaUrlList())
			.build();
	}

	public FeedReadResponseVo toVo() {
		return FeedReadResponseVo.builder()
			.feedCode(feedCode)
			.mediaUrlList(mediaUrlList)
			.build();
	}
}
