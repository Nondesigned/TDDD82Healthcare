package tddd82.healthcare;

public interface Flags {
    public void setFlag(ControlFlag flag, boolean value);
    public boolean getFlag(int i);
    public boolean geFlag(ControlFlag flag);
    public byte[] getBytes();
}