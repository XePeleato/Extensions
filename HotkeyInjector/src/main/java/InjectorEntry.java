import gearth.protocol.HMessage;
import gearth.protocol.HPacket;

public class InjectorEntry {
    private HPacket packet;
    private HMessage.Side side;
    private String hotkeyCode;

    public InjectorEntry(HPacket packet, HMessage.Side side, String hotkeyCode) {
        this.packet = packet;
        this.side = side;
        this.hotkeyCode = hotkeyCode;
    }

    public HPacket getPacket() {
        return packet;
    }

    public HMessage.Side getSide() {
        return side;
    }

    public boolean equals(Object o) {
        return o instanceof InjectorEntry &&
                ((InjectorEntry) o).getPacket().equals(packet) && ((InjectorEntry) o).getSide().equals(side) &&
                ((InjectorEntry) o).hotkeyCode.equals(hotkeyCode);
    }

    public String getHotkeyCode() {
        return hotkeyCode;
    }
}
