package lookids.feedread.dto.out;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class FollowResponseDto {

	private String uuid;
	private List<String> followUuid;

	@Builder
	public FollowResponseDto(String uuid, List<String> followUuid) {
		this.uuid = uuid;
		this.followUuid = followUuid;
	}
}
