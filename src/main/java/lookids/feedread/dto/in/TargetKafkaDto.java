package lookids.feedread.dto.in;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class TargetKafkaDto {

	private String authorUuid;
	private List<String> targetCode;

	@Builder
	public TargetKafkaDto(String authorUuid, List<String> targetCode) {
		this.authorUuid = authorUuid;
		this.targetCode = targetCode;
	}
}
