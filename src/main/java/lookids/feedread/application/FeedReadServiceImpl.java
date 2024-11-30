package lookids.feedread.application;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ExecutableAggregationOperation;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.constraints.AssertFalse;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lookids.common.config.SwaggerConfig;
import lookids.common.entity.BaseResponseStatus;
import lookids.common.exception.BaseException;
import lookids.feedread.domain.FeedRead;
import lookids.feedread.dto.in.FavoriteRequestKafkaDto;
import lookids.feedread.dto.in.FeedDeleteKafkaDto;
import lookids.feedread.dto.out.FavoriteResponseDto;
import lookids.feedread.dto.in.FeedKafkaDto;
import lookids.feedread.dto.out.FeedListResponseDto;
import lookids.feedread.dto.out.FeedReadDetailResponseDto;
import lookids.feedread.dto.out.FeedReadResponseDto;
import lookids.feedread.dto.in.UserImageKafkaDto;
import lookids.feedread.dto.in.UserKafkaDto;
import lookids.feedread.dto.in.UserNickNameKafkaDto;
import lookids.feedread.infrastructure.FeedReadRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@ToString
public class FeedReadServiceImpl implements FeedReadService {

	private final ConcurrentHashMap<String, CompletableFuture<FeedKafkaDto>> feedEventFutureMap = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, CompletableFuture<UserKafkaDto>> userEventFutureMap = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, CompletableFuture<FavoriteResponseDto>> favoriteEventFutureMap = new ConcurrentHashMap<>();
	private final KafkaTemplate<String, FavoriteRequestKafkaDto> favoriteKafkaTemplate;
	private final FeedReadRepository feedReadRepository;
	private final MongoTemplate mongoTemplate;

	//feed service consume
	@KafkaListener(topics = "feed-create", groupId = "feed-read-group", containerFactory = "feedEventListenerContainerFactory")
	public void FeedConsume(FeedKafkaDto feedKafkaDto) {
		String uuid = feedKafkaDto.getUuid();
		CompletableFuture<FeedKafkaDto> feedEventFuture = feedEventFutureMap.computeIfAbsent(uuid,
			key -> new CompletableFuture<>());
		feedEventFuture.complete(feedKafkaDto);
		checkAndCreateFeedEventListener(uuid);
	}

	//user service consume
	@KafkaListener(topics = "feed-create-join-userprofile", groupId = "feed-read-group", containerFactory = "userProfileEventListenerContainerFactory")
	public void UserConsume(UserKafkaDto userKafkaDto) {
		String uuid = userKafkaDto.getUuid();
		CompletableFuture<UserKafkaDto> userprofileEventFuture = userEventFutureMap.computeIfAbsent(uuid,
			key -> new CompletableFuture<>());
		userprofileEventFuture.complete(userKafkaDto);
		checkAndCreateFeedEventListener(uuid);
	}

	//feed, user service save
	private void checkAndCreateFeedEventListener(String uuid) {
		CompletableFuture<UserKafkaDto> userProfileEventFuture = userEventFutureMap.get(uuid);
		CompletableFuture<FeedKafkaDto> feedEventFuture = feedEventFutureMap.get(uuid);
		if (userProfileEventFuture != null && feedEventFuture != null) {
			userProfileEventFuture.thenCombine(feedEventFuture, (userKafkaDto, feedKafkaDto) -> {
				FeedRead feedRead = FeedRead.builder()
					.feedCode(feedKafkaDto.getFeedCode())
					.petCode(feedKafkaDto.getPetCode())
					.uuid(feedKafkaDto.getUuid())
					.content(feedKafkaDto.getContent())
					.tagList(feedKafkaDto.getTagList())
					.mediaUrlList(feedKafkaDto.getMediaUrlList())
					.state(feedKafkaDto.isState())
					.createdAt(feedKafkaDto.getCreatedAt())
					.uuid(userKafkaDto.getUuid())
					// .tag(userKafkaDto.getTag())
					// .image(userKafkaDto.getImage())
					// .nickname(userKafkaDto.getNickname())
					.build();
				feedReadRepository.save(feedRead);
				feedEventFutureMap.remove(uuid);
				userEventFutureMap.remove(uuid);
				return null;
			});
		}
	}

	//userprofile nickname update
	@Transactional
	@KafkaListener(topics = "userprofile-nickname-update", groupId = "feed-read-group", containerFactory = "userNickNameEventListenerContainerFactory")
	public void NickNameUpdateConsume(UserNickNameKafkaDto userNickNameKafkaDto) {
		List<FeedRead> findUuid = feedReadRepository.findAllByUuid(userNickNameKafkaDto.getUuid());
		if (findUuid.isEmpty()) {
			throw new BaseException(BaseResponseStatus.NO_EXIST_FEED);
		}
		List<FeedRead> nickNameUpdate = findUuid.stream()
			.map(feedRead -> userNickNameKafkaDto.toNickNameUpdate(feedRead))
			.collect(Collectors.toList());
		feedReadRepository.saveAll(nickNameUpdate);
	}

	//userprofile image update
	@Transactional
	@KafkaListener(topics = "userprofile-image-update", groupId = "feed-read-group", containerFactory = "userProfileEventListenerContainerFactory")
	public void ImageUpdateConsume(UserImageKafkaDto userImageKafkaDto) {
		List<FeedRead> findUuid = feedReadRepository.findAllByUuid(userImageKafkaDto.getUuid());
		if (findUuid.isEmpty()) {
			throw new BaseException(BaseResponseStatus.NO_EXIST_FEED);
		}
		List<FeedRead> ImageUpdate = findUuid.stream()
			.map(feedRead -> userImageKafkaDto.toImageUpdate(feedRead))
			.collect(Collectors.toList());
		feedReadRepository.saveAll(ImageUpdate);
	}

	//feed delete consume
	@KafkaListener(topics = "feed-delete", groupId = "feed-read-group", containerFactory = "deleteEventListenerContainerFactory")
	public void FeedDeleteConsume(FeedDeleteKafkaDto feedDeleteKafkaDto) {
		FeedRead feedRead = feedReadRepository.findByFeedCodeAndStateFalse(feedDeleteKafkaDto.getFeedCode())
			.orElseThrow(() -> new BaseException(BaseResponseStatus.NO_EXIST_FEED));
		FeedRead updatedFeedRead = feedDeleteKafkaDto.toUpdatedEntity(feedRead);
		feedReadRepository.save(updatedFeedRead);
	}

	//uuid feed favorite List 조회
	@Override
	public Page<FeedReadResponseDto> readFeedFavoriteList(String uuid, int page, int size) {
		List<FeedRead> findUuid = feedReadRepository.findAllByUuid(uuid);
		if (findUuid.isEmpty()) {
			throw new BaseException(BaseResponseStatus.NO_EXIST_USER);
		}

		favoriteKafkaTemplate.send("favorite-request", FavoriteRequestKafkaDto.builder().uuid(uuid).build());
		CompletableFuture<FavoriteResponseDto> futureFeedCodeList = new CompletableFuture<>();
		favoriteEventFutureMap.put(uuid, futureFeedCodeList);

		// 좋아요 목록 kafka 대기
		FavoriteResponseDto favoriteResponseDto = null;
		try {
			favoriteResponseDto = futureFeedCodeList.get();
		} catch (InterruptedException | ExecutionException e) {
			log.error("Error while fetching favorite feed codes", e);
		}
		List<String> targetCodeList = favoriteResponseDto.getTargetCodeList();

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
		Page<FeedRead> feedReadList = feedReadRepository.findByFeedCodeInAndStateFalse(targetCodeList, pageable);
		List<FeedReadResponseDto> feedDtoList = feedReadList.stream()
			.map(FeedReadResponseDto::toDto)
			.collect(Collectors.toList());
		return new PageImpl<>(feedDtoList, pageable, feedReadList.getTotalElements());
	}

	// 좋아요 서비스에서 주는 Dto consume
	@KafkaListener(topics = "favorite-response", groupId = "feed-read-group", containerFactory = "favoriteEventListenerContainerFactory")
	public void readFeedFavorite(FavoriteResponseDto favoriteResponseDto) {
		String uuid = favoriteResponseDto.getUuid();
		CompletableFuture<FavoriteResponseDto> futureFeedCodeList = favoriteEventFutureMap.get(uuid);
		if (futureFeedCodeList != null) {
			futureFeedCodeList.complete(favoriteResponseDto);
		} else {
			log.warn("No future Uuid: {}", uuid);
		}
	}

	// //uuid feed List 조회
	// @Override
	// public Page<FeedListResponseDto> readFeedList(String uuid, int page, int size) {
	// 	List<FeedRead> findUuid = feedReadRepository.findAllByUuid(uuid);
	// 	if (findUuid.isEmpty()) {
	// 		throw new BaseException(BaseResponseStatus.NO_EXIST_USER);
	// 	}
	// 	Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
	// 	Page<FeedRead> feedReadList = feedReadRepository.findByUuidAndStateFalse(uuid, pageable);
	// 	List<FeedListResponseDto> feedDtoList = feedReadList.stream()
	// 		.map(FeedListResponseDto::toDto)
	// 		.collect(Collectors.toList());
	// 	return new PageImpl<>(feedDtoList, pageable, feedReadList.getTotalElements());
	// }

	//uuid feed List 조회 (uuid가 팔로우 한 유저들의 피드 목록 + tag 필터링 포함 조회)
	@Override
	public Page<FeedListResponseDto> readFeedAndTagList(String uuid, String tag, int page, int size) {
		List<FeedRead> findUuid = feedReadRepository.findAllByUuid(uuid);
		if (findUuid.isEmpty()) {
			throw new BaseException(BaseResponseStatus.NO_EXIST_USER);
		}
		//어떤 키워드를 기준으로 정렬하는지
		Criteria criteria = Criteria.where("uuid").is(uuid).and("state").is(false);
		if (tag != null && !tag.isEmpty()) {
			criteria.and("tagList").in(tag);
		}
		// Aggregation
		Aggregation aggregation = Aggregation.newAggregation(
			Aggregation.match(criteria),
			Aggregation.sort(Sort.by(Sort.Direction.DESC, "createdAt")),
			Aggregation.skip((long) page * size),
			Aggregation.limit(size)
		);
		AggregationResults<FeedRead> results = mongoTemplate.aggregate(aggregation, "feedRead", FeedRead.class);
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));

		List<FeedListResponseDto> feedDtoList = results.getMappedResults()
			.stream()
			.map(FeedListResponseDto::toDto)
			.collect(Collectors.toList());

		return new PageImpl<>(feedDtoList, pageable, results.getMappedResults().size());
	}

	//feed thumbnail List 조회
	@Override
	public Page<FeedReadResponseDto> readFeedThumbnailList(String uuid, int page, int size) {
		List<FeedRead> findUuid = feedReadRepository.findAllByUuid(uuid);
		if (findUuid.isEmpty()) {
			throw new BaseException(BaseResponseStatus.NO_EXIST_USER);
		}
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
		Page<FeedRead> feedReadList = feedReadRepository.findByUuidAndStateFalse(uuid, pageable);
		List<FeedReadResponseDto> feedDtoList = feedReadList.getContent().stream()
			.map(FeedReadResponseDto::toDto)
			.collect(Collectors.toList());
		return new PageImpl<>(feedDtoList, pageable, feedReadList.getTotalElements());
	}

	//feed detail 조회
	@Override
	public FeedReadDetailResponseDto readFeedDetail(String feedCode) {
		FeedRead feedRead = feedReadRepository.findByFeedCodeAndStateFalse(feedCode)
			.orElseThrow(() -> new BaseException(BaseResponseStatus.NO_EXIST_FEED));
		return FeedReadDetailResponseDto.toDto(feedRead);
	}
}