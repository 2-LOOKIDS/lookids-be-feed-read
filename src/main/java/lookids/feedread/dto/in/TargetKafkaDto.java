package lookids.feedread.dto.in;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TargetKafkaDto {

	private List<String> targetCode;

	@Builder
	public TargetKafkaDto(List<String> targetCode) {
		this.targetCode = targetCode;
	}
}
