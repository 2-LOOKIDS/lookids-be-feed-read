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
	private List<String> tags;
	private List<String> mediaUrl;
	private LocalDateTime createdAt;

	public static FeedReadDetailResponseDto toDto(FeedRead feedRead) {
		return FeedReadDetailResponseDto.builder()
			.petCode(feedRead.getPetCode())
			.nickname(feedRead.getNickname())
			.image(feedRead.getImage())
			.content(feedRead.getContent())
			.tags(feedRead.getTags())
			.mediaUrl(feedRead.getMediaUrl())
			.createdAt(feedRead.getCreatedAt())
			.build();
	}

	public FeedReadDetailResponseVo toDetailVo() {
		return FeedReadDetailResponseVo.builder()
			.nickname(nickname)
			.image(image)
			.petCode(petCode)
			.content(content)
			.tags(tags)
			.mediaUrl(mediaUrl)
			.createdAt(createdAt)
			.build();
	}

}
