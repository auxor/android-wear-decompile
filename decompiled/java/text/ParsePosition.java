package java.text;

public class ParsePosition {
    private int currentPosition;
    private int errorIndex;

    public ParsePosition(int index) {
        this.errorIndex = -1;
        this.currentPosition = index;
    }

    public boolean equals(Object object) {
        if (!(object instanceof ParsePosition)) {
            return false;
        }
        ParsePosition pos = (ParsePosition) object;
        if (this.currentPosition == pos.currentPosition && this.errorIndex == pos.errorIndex) {
            return true;
        }
        return false;
    }

    public int getErrorIndex() {
        return this.errorIndex;
    }

    public int getIndex() {
        return this.currentPosition;
    }

    public int hashCode() {
        return this.currentPosition + this.errorIndex;
    }

    public void setErrorIndex(int index) {
        this.errorIndex = index;
    }

    public void setIndex(int index) {
        this.currentPosition = index;
    }

    public String toString() {
        return getClass().getName() + "[index=" + this.currentPosition + ", errorIndex=" + this.errorIndex + "]";
    }
}
