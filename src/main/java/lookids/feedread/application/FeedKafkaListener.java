package lookids.feedread.application;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lookids.common.entity.BaseResponseStatus;
import lookids.common.exception.BaseException;
import lookids.feedread.domain.FeedRead;
import lookids.feedread.dto.in.BlockKafkaDto;
import lookids.feedread.dto.in.FeedDeleteKafkaDto;
import lookids.feedread.dto.in.FeedKafkaDto;
import lookids.feedread.dto.in.PetImageKafkaDto;
import lookids.feedread.dto.in.TargetKafkaDto;
import lookids.feedread.dto.in.TargetRequestKafkaDto;
import lookids.feedread.dto.in.UserImageKafkaDto;
import lookids.feedread.dto.in.UserKafkaDto;
import lookids.feedread.dto.in.UserNickNameKafkaDto;
import lookids.feedread.dto.in.UuidKafkaDto;
import lookids.feedread.dto.out.FavoriteResponseDto;
import lookids.feedread.dto.out.FollowResponseDto;
import lookids.feedread.infrastructure.FeedReadRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@ToString
public class FeedKafkaListener {

	private final KafkaTemplate<String, TargetRequestKafkaDto> recommendKafkaTemplate;

	private final FeedReadRepository feedReadRepository;

	@Transactional
	@KafkaListener(topics = "userprofile-nickname-update", groupId = "feed-read-group", containerFactory = "userNickNameEventListenerContainerFactory")
	public void NickNameUpdateConsume(UserNickNameKafkaDto userNickNameKafkaDto) {
		List<FeedRead> findUuid = feedReadRepository.findAllByUuid(userNickNameKafkaDto.getUuid());
		if (findUuid.isEmpty()) {
			throw new BaseException(BaseResponseStatus.NO_EXIST_FEED);
		}
		List<FeedRead> nickNameUpdate = findUuid.stream().map(userNickNameKafkaDto::toNickNameUpdate)
			.collect(Collectors.toList());
		feedReadRepository.saveAll(nickNameUpdate);
	}

	@Transactional
	@KafkaListener(topics = "userprofile-image-update", groupId = "feed-read-group", containerFactory = "userProfileEventListenerContainerFactory")
	public void ImageUpdateConsume(UserImageKafkaDto userImageKafkaDto) {
		List<FeedRead> findUuid = feedReadRepository.findAllByUuid(userImageKafkaDto.getUuid());
		if (findUuid.isEmpty()) {
			throw new BaseException(BaseResponseStatus.NO_EXIST_FEED);
		}
		List<FeedRead> ImageUpdate = findUuid.stream().map(userImageKafkaDto::toImageUpdate)
			.collect(Collectors.toList());
		feedReadRepository.saveAll(ImageUpdate);
	}

	@KafkaListener(topics = "feed-delete", groupId = "feed-read-group", containerFactory = "deleteEventListenerContainerFactory")
	public void FeedDeleteConsume(FeedDeleteKafkaDto feedDeleteKafkaDto) {
		FeedRead feedRead = feedReadRepository.findByFeedCodeAndStateTrue(feedDeleteKafkaDto.getFeedCode())
			.orElseThrow(() -> new BaseException(BaseResponseStatus.NO_EXIST_FEED));
		FeedRead updatedFeedRead = feedDeleteKafkaDto.toUpdatedEntity(feedRead);
		log.info("adskjfhaksljdfh: {}", feedDeleteKafkaDto);
		feedReadRepository.save(updatedFeedRead);
	}

	@KafkaListener(topics = "recommend-user", groupId = "feed-read-group", containerFactory = "recommendEventListenerContainerFactory")
	public void recommendTarget(TargetKafkaDto targetKafkaDto) {
		List<FeedRead> findUuidList = feedReadRepository.findByFeedCodeIn(targetKafkaDto.getTargetCode());
		List<String> uuidList = findUuidList.stream()
			.map(FeedRead::getUuid)
			.collect(Collectors.toList());
		TargetRequestKafkaDto targetRequestKafkaDto = TargetRequestKafkaDto.toDto(targetKafkaDto.getAuthorUuid(), uuidList);
		recommendKafkaTemplate.send("recommend-user-response", targetRequestKafkaDto);
	}

	@Transactional
	@KafkaListener(topics = "petprofile-update", groupId = "feed-read-group", containerFactory = "petProfileEventListenerContainerFactory")
	public void ImageUpdateConsume(PetImageKafkaDto petImageKafkaDto) {
		List<FeedRead> findPetCode = feedReadRepository.findAllBypetCode(petImageKafkaDto.getPetCode());
		if (findPetCode.isEmpty()) {
			throw new BaseException(BaseResponseStatus.NO_EXIST_FEED);
		}
		List<FeedRead> ImageUpdate = findPetCode.stream().map(petImageKafkaDto::toImageUpdate)
			.collect(Collectors.toList());
		feedReadRepository.saveAll(ImageUpdate);
	}

	@KafkaListener(topics = "account-delete", groupId = "feed-read-group", containerFactory = "accountDeleteEventListenerContainerFactory")
	public void ImageUpdateConsume(UuidKafkaDto uuidKafkaDto) {
		List<FeedRead> findUuid = feedReadRepository.findAllByUuid(uuidKafkaDto.getUuid());
		if (findUuid.isEmpty()) {
			throw new BaseException(BaseResponseStatus.NO_EXIST_FEED);
		}
		List<FeedRead> feedDelete = findUuid.stream().map(uuidKafkaDto::toDelete)
			.collect(Collectors.toList());
		feedReadRepository.saveAll(feedDelete);
	}

}
