package tddd82.healthcare;

/**
 * ControlFlags
 */
public class ControlFlags implements Flags{

    byte[] bytes;
    /**
     * Constructer: Creates new byte array
     */
    public ControlFlags () {
        bytes = new byte[2];
    }

    /**
     * Returns the bytes containing the flags
     */
    public byte[] getBytes(){
        return bytes;
    }

    /**
     * Returns the flag at position i
     */
    public boolean getFlag(int i) {
        return ServerUtils.getBit(bytes[(int)(i/8)], i) != 0;
    }

    /**
     * Returns flag based on enum Controlflag
     */
    public boolean geFlag(ControlFlag flag) {
        switch (flag) {
            case INITCALL:
                return getFlag(0);

            case ENDCALL:
                return getFlag(1);

            case ACCEPTCALL:
                return getFlag(2);

            case DECLINECALL:
                return getFlag(3);

            case ASKFORENC:
                return getFlag(4);

            case ENC1 :
                return getFlag(5);

            case ENC2:
                return getFlag(6);

            case ENC3:
                return getFlag(7);

            case INITVID:
                return getFlag(8);

            case ENDVID:
                return getFlag(9);

            default:
                return false;
        }
    }

    /**
     * Sets flag based on enum and boolean value
     */
    public void setFlag(ControlFlag flag, boolean value) {
        switch (flag) {
            case INITCALL:
                bytes[0] = ServerUtils.setBit(bytes[0], 0, value);
                break;
            case ENDCALL:
                bytes[0] = ServerUtils.setBit(bytes[0], 1, value);
                break;
            case ACCEPTCALL:
                bytes[0] = ServerUtils.setBit(bytes[0], 2, value);
                break;
            case DECLINECALL:
                bytes[0] = ServerUtils.setBit(bytes[0], 3, value);
                break;
            case ASKFORENC:
                bytes[0] = ServerUtils.setBit(bytes[0], 4, value);
                break;
            case ENC1 :
                bytes[0] = ServerUtils.setBit(bytes[0], 5, value);
                break;
            case ENC2:
                bytes[0] = ServerUtils.setBit(bytes[0], 6, value);
                break;
            case ENC3:
                bytes[0] = ServerUtils.setBit(bytes[0], 7, value);
                break;
            case INITVID:
                bytes[1] = ServerUtils.setBit(bytes[1], 0, value);
                break;
            case ENDVID:
                bytes[1] = ServerUtils.setBit(bytes[1], 1, value);
                break;
            default:
                break;
        }
    }

}