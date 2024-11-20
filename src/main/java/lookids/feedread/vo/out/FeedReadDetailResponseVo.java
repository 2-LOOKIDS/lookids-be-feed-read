package lookids.feedread.vo.out;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedReadDetailResponseVo {

	private String nickname;
	private String image;
	private String petCode;
	private String content;
	private List<String> tags;
	private List<String> mediaUrl;
	private LocalDateTime createdAt;
}
