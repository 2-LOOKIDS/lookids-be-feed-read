package lookids.feedread.vo.out;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedReadResponseVo {

	private String feedCode;
	private List<String> mediaUrlList;
	private String mediaUrl;

}
