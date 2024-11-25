package lookids.feedread.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lookids.feedread.domain.FeedRead;
import lookids.feedread.vo.out.FeedReadDetailResponseVo;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedReadDetailResponseDto {

	private String nickname;
	private String image;
	private String petCode;
	private String content;
	private List<String> tagList;
	private List<String> mediaUrlList;
	private LocalDateTime createdAt;

	public static FeedReadDetailResponseDto toDto(FeedRead feedRead) {
		return FeedReadDetailResponseDto.builder()
			.petCode(feedRead.getPetCode())
			.nickname(feedRead.getNickname())
			.image(feedRead.getImage())
			.content(feedRead.getContent())
			.tagList(feedRead.getTagList())
			.mediaUrlList(feedRead.getMediaUrlList())
			.createdAt(feedRead.getCreatedAt())
			.build();
	}

	public FeedReadDetailResponseVo toDetailVo() {
		return FeedReadDetailResponseVo.builder()
			.nickname(nickname)
			.image(image)
			.petCode(petCode)
			.content(content)
			.tagList(tagList)
			.mediaUrlList(mediaUrlList)
			.createdAt(createdAt)
			.build();
	}

}
