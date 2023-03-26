import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class CDR {
    private String number;
    private String callType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String tariffType;
    private String duration;
    private double cost;
    long millis;
//01, 73734435243, 20230725141448, 20230725142110, 11

    public CDR(String callType, String number, String startTime, String endTime, String tariffType) {
        this.number = number;
        this.callType = callType;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        this.startTime =LocalDateTime.parse(startTime.replaceAll(" ",""), formatter);
        this.endTime = LocalDateTime.parse(endTime.replaceAll(" ",""), formatter);
        this.tariffType = tariffType;

        this.millis = this.endTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - this.startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000 * 60)) % 60;
        long hours = (millis / (1000 * 60 * 60)) % 24;
        this.duration = String.format("%02d:%02d:%02d", hours, minutes, seconds);

//        this.cost = tar.calculateCallCost(millis);
    }




    public String getNumber() {
        return number;
    }

    public String getCallType() {
        return callType;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getTariffType() {
        return tariffType;
    }

    public String getSubscriberNumber() {
        return number;
    }

//    |     01    | 2023-02-03 05:55:06 | 2023-02-03 06:02:49 | 00:07:43 |  5.00 |5.00 |

    public String cdrToStr(){
        return ("|     "+callType+"    | "+startTime+" | "+endTime+" | "+duration+" |  "+cost+" |");
    }

    public long getDuration() {
        return millis;
    }

    public String getDur() {
        return duration;
    }
}

