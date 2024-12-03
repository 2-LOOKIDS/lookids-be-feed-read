package lookids.feedread.presentation;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lookids.common.entity.BaseResponse;
import lookids.feedread.application.FeedReadService;
import lookids.feedread.dto.out.FeedListResponseDto;
import lookids.feedread.dto.out.FeedReadResponseDto;
import lookids.feedread.vo.out.FeedReadDetailResponseVo;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/read/feed")
public class FeedReadController {

	private final FeedReadService feedReadService;

	// @Operation(summary = "feed List 조회 API", description = "feed List로 조회하는 API 입니다.", tags = {"Feed"})
	// @GetMapping("/feedList")
	// public BaseResponse<Page<FeedListResponseDto>> readFeedList(
	// 	@RequestHeader String uuid,
	// 	@RequestParam(defaultValue = "0") int page,
	// 	@RequestParam(defaultValue = "10") int size) {
	// 	Page<FeedListResponseDto> feedRead = feedReadService.readFeedList(uuid, page, size);
	// 	return new BaseResponse<>(feedRead);
	// }

	@Operation(summary = "feed List 조회 API", description = "feed List로 조회하는 API 입니다.", tags = {"Feed"})
	@GetMapping("/feedList")
	public BaseResponse<Page<FeedListResponseDto>> readFeedAndTagList(
		@RequestHeader String uuid,
		@RequestParam(required = false) String tag,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {
		Page<FeedListResponseDto> feedRead = feedReadService.readFeedAndTagList(uuid, tag, page, size);
		return new BaseResponse<>(feedRead);
	}

	@Operation(summary = "feed List 조회 API", description = "feed List로 조회하는 API 입니다.", tags = {"Feed"})
	@GetMapping("/random")
	public BaseResponse<Page<FeedListResponseDto>> readFeedRandomList(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {
		Page<FeedListResponseDto> feedRead = feedReadService.readFeedRandomList(page, size);
		return new BaseResponse<>(feedRead);
	}

	@Operation(summary = "feed Favorite List 조회 API", description = "사용자가 좋아요 한 feed List로 조회하는 API 입니다.", tags = {"Feed"})
	@GetMapping("/favoriteList")
	public BaseResponse<Page<FeedReadResponseDto>> readFeedFavoriteList(
		@RequestHeader String uuid,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {
		Page<FeedReadResponseDto> feedRead = feedReadService.readFeedFavoriteList(uuid, page, size);
		return new BaseResponse<>(feedRead);
	}

	@Operation(summary = "feed thumbnail List 조회 API", description = "uuid 기준으로 해당 사용자의 feed에 해당하는 썸네일을 List로 조회하는 API 입니다.", tags = {"Feed"})
	@GetMapping("/thumbnailList")
	public BaseResponse<Page<FeedReadResponseDto>> readFeedThumbnailList(
		@RequestHeader String uuid,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {
		Page<FeedReadResponseDto> feedRead = feedReadService.readFeedThumbnailList(uuid, page, size);
		return new BaseResponse<>(feedRead);
	}

	@Operation(summary = "feed 조회 API", description = "feedCode 기준으로 feed의 상세 내용을 조회하는 API 입니다.", tags = {"Feed"})
	@GetMapping("/detail")
	public BaseResponse<FeedReadDetailResponseVo> readFeedDetail(@RequestParam String feedCode) {
		return new BaseResponse<>(feedReadService.readFeedDetail(feedCode).toDetailVo());
	}
}

