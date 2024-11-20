package lookids.feedread.application;

import org.springframework.data.domain.Page;

import lookids.feedread.dto.FeedReadDetailResponseDto;
import lookids.feedread.dto.FeedReadResponseDto;

public interface FeedReadService {
	Page<FeedReadResponseDto> readFeed(String uuid, int page, int size);
	FeedReadDetailResponseDto readFeed(String uuid, String feedCode);

}
