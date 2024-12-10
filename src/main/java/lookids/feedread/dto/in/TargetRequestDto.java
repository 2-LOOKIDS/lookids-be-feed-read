package lookids.feedread.dto.in;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class TargetRequestDto {

	private List<String> uuid;
	private String authorUuid;


	@Builder
	public TargetRequestDto(String authorUuid, List<String> uuid) {
		this.authorUuid = authorUuid;
		this.uuid = uuid;
	}

	public static TargetRequestDto toDto(String authorUuid, List<String> uuid) {
		return TargetRequestDto.builder()
			.authorUuid(authorUuid)
			.uuid(uuid)
			.build();
	}
}
