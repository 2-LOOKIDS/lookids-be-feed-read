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

	private List<String> targetCode;

	@Builder
	public TargetKafkaDto(List<String> targetCode) {
		this.targetCode = targetCode;
	}
}
