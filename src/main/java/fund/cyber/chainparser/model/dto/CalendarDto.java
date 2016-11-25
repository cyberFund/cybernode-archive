package fund.cyber.chainparser.model.dto;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Sample DTO to test REST service
 * <p>
 * @author Andrey Lobarev nxtpool@gmail.com
 */
public class CalendarDto implements Serializable {
    private Calendar time;

    public CalendarDto(Calendar time) {
        this.time = time;
    }

    public CalendarDto() {
    }

    public Calendar getTime() {
        return this.time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }
}
