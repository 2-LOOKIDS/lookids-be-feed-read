package lookids.feedread.dto.in;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lookids.feedread.domain.FeedRead;

@Getter
@NoArgsConstructor
@ToString
public class UuidRequestKafkaDto {

	private String uuid;

	@Builder
	public UuidRequestKafkaDto(String uuid) {
		this.uuid = uuid;
	}

	public static UuidRequestKafkaDto toDto(String uuid) {
		return UuidRequestKafkaDto.builder()
			.uuid(uuid)
			.build();
	}
}
