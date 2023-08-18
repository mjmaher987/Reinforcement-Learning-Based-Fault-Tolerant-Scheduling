import java.util.ArrayList;
import java.util.List;

public record TargetEntry(String type, int vmNumber, int cloudletNumber, double makespan,
                          double avg_completion, double avg_cost, double avg_wait, double successfulRate) {
    private static final List<TargetEntry> allTargetEntries = new ArrayList<>();

    public TargetEntry(String type, int vmNumber, int cloudletNumber, double makespan, double avg_completion, double avg_cost, double avg_wait, double successfulRate) {
        this.type = type;
        this.vmNumber = vmNumber;
        this.cloudletNumber = cloudletNumber;
        this.makespan = makespan;
        this.avg_completion = avg_completion;
        this.avg_cost = avg_cost;
        this.avg_wait = avg_wait;
        this.successfulRate = successfulRate;

        allTargetEntries.add(this);
    }

    public int getCloudletNumber() {
        return cloudletNumber;
    }

    public double getMakespan() {
        return makespan;
    }

    public int getVmNumber() {
        return vmNumber;
    }

    public double getAvg_completion() {
        return avg_completion;
    }

    public double getAvg_cost() {
        return avg_cost;
    }

    public double getAvg_wait() {
        return avg_wait;
    }

    public static List<TargetEntry> getAllTargetEntries() {
        return TargetEntry.allTargetEntries;
    }

    public String getType(){
        return type;
    }

    public double getSuccessfulRate(){
        return this.successfulRate;
    }
}
