package bean;

public class CloneGroup {

    private int id;
    private int[] measureIds;

    public CloneGroup() {
    }

    public CloneGroup(int id, int[] measureIds) {
        this.id = id;
        this.measureIds = measureIds;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int[] getMeasureIds() {
        return measureIds;
    }

    public void setMeasureIds(int[] measureIds) {
        this.measureIds = measureIds;
    }
}
