package lookids.feedread.application;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lookids.feedread.domain.FeedRead;
import lookids.feedread.dto.FeedKafkaDto;
import lookids.feedread.dto.UserKafkaDto;
import lookids.feedread.infrastructure.FeedReadRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Component
public class FeedReadService{

	private final ConcurrentHashMap<String, CompletableFuture<FeedKafkaDto>> feedEventFutureMap = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, CompletableFuture<UserKafkaDto>> userEventFutureMap = new ConcurrentHashMap<>();
	private final FeedReadRepository feedReadRepository;

	//feed service consumer
	@KafkaListener(topics = "feed-create", groupId = "feed-read-service", containerFactory = "feedEventListenerContainerFactory")
	public void FeedConsume(FeedKafkaDto feedKafkaDto) {
		log.info("Received KafkaDto: " + feedKafkaDto);
		String uuid = feedKafkaDto.getUuid();
		CompletableFuture<FeedKafkaDto> feedEventFuture = feedEventFutureMap.computeIfAbsent(uuid,
			key-> new CompletableFuture<>());
		feedEventFuture.complete(feedKafkaDto);
		log.info("consume: {}", feedKafkaDto);

		checkAndCreateFeedEventListener(uuid);
	}

	//user service consumer
	@KafkaListener(topics = "userprofile-response", groupId = "kafka-read-service", containerFactory = "userProfileEventListenerContainerFactory")
	public void UserConsume(UserKafkaDto userKafkaDto) {
		String uuid = userKafkaDto.getUuid();
		CompletableFuture<UserKafkaDto> userprofileEventFuture = userEventFutureMap.computeIfAbsent(uuid,
			key-> new CompletableFuture<>());
		userprofileEventFuture.complete(userKafkaDto);
		log.info("consume: {}", userKafkaDto);


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
					.tags(feedKafkaDto.getTags())
					.mediaCode(feedKafkaDto.getMediaCode())
					.state(feedKafkaDto.isState())
					.createdAt(feedKafkaDto.getCreatedAt())
					.uuid(userKafkaDto.getUuid())
					.image(userKafkaDto.getImage())
					.nickname(userKafkaDto.getNickname())
					.build();
				feedReadRepository.save(feedRead);
				feedEventFutureMap.remove(uuid);
				userEventFutureMap.remove(uuid);
				return null;
			});
		}
	}
}
