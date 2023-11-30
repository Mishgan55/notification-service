package innowise.khorsun.notificationservice.utill.errors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@RequiredArgsConstructor
public class JsonParsingException extends RuntimeException{
    private final String message;
    private final Date date;
}
