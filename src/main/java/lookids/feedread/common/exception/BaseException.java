package lookids.feedread.common.exception;

import lombok.Getter;
import lookids.feedread.common.entity.BaseResponseStatus;

@Getter
public class BaseException extends RuntimeException {

    private final BaseResponseStatus status;

    public BaseException(BaseResponseStatus status) {
        this.status = status;
    }
}