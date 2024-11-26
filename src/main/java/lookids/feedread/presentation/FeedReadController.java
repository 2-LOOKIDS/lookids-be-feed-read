package lookids.feedread.presentation;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lookids.feedread.common.entity.BaseResponse;
import lookids.feedread.dto.FeedReadResponseDto;
import lookids.feedread.application.FeedReadService;
import lookids.feedread.vo.out.FeedReadDetailResponseVo;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/read/feed")
public class FeedReadController {

	private final FeedReadService feedReadService;


	@Operation(summary = "feed 조회 API", description = "uuid 기준으로 해당 사용자의 feed List를 조회하는 API 입니다.", tags = {"Feed"})
	@GetMapping()
	public BaseResponse<Page<FeedReadResponseDto>> readFeed(
		@RequestParam String uuid,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "3") int size) {
		Page<FeedReadResponseDto> feedRead = feedReadService.readFeed(uuid, page, size);
		return new BaseResponse<>(feedRead);
	}

	@Operation(summary = "feed 조회 API", description = "feedCode 기준으로 feed의 상세 내용을 조회하는 API 입니다.", tags = {"Feed"})
	@GetMapping("/detail")
	public BaseResponse<FeedReadDetailResponseVo> readFeed(@RequestParam String feedCode) {
		return new BaseResponse<>(feedReadService.readFeed(feedCode).toDetailVo());
	}
}

