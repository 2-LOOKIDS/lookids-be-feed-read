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


	@Builder
	public TargetRequestDto(List<String> uuid) {
		this.uuid = uuid;
	}

	public static TargetRequestDto toDto(List<String> uuid) {
		return TargetRequestDto.builder()
			.uuid(uuid)
			.build();
	}
}
