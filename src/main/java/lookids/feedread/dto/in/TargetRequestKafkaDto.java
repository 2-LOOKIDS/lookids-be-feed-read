package lookids.feedread.dto.in;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class TargetRequestKafkaDto {

	private List<String> uuid;
	private String authorUuid;


	@Builder
	public TargetRequestKafkaDto(String authorUuid, List<String> uuid) {
		this.authorUuid = authorUuid;
		this.uuid = uuid;
	}

	public static TargetRequestKafkaDto toDto(String authorUuid, List<String> uuid) {
		return TargetRequestKafkaDto.builder()
			.authorUuid(authorUuid)
			.uuid(uuid)
			.build();
	}
}
