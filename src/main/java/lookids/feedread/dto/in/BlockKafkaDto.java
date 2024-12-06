package lookids.feedread.dto.in;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BlockKafkaDto {

	private String uuid;
	private List<String> blockUuid;

	@Builder
	public BlockKafkaDto(String uuid, List<String> blockUuid) {
		this.uuid = uuid;
		this.blockUuid = blockUuid;
	}
}
