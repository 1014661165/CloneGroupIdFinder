package bean;

public class Measure {
    private int id;
    private String path;
    private int startLine;
    private int endLine;

    public Measure() {
    }

    public Measure(int id, String path, int startLine, int endLine) {
        this.id = id;
        this.path = path;
        this.startLine = startLine;
        this.endLine = endLine;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }
}
