package Lookids.Feed.common.utills;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CursorPage<T> {
	private List<T> content;
	private Boolean hasNext;
	private Integer size;
	private Integer page;

	@Builder
	public CursorPage(
		List<T> content,
		Boolean hasNext,
		Integer size,
		Integer page
	) {
		this.content = content;
		this.hasNext = hasNext;
		this.size = size;
		this.page = page;
	}
}
