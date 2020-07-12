import gearth.protocol.HMessage;
import gearth.protocol.HPacket;

public class InjectorEntry {
    private HPacket packet;
    private HMessage.Direction side;
    private String hotkeyCode;

    public InjectorEntry(HPacket packet, HMessage.Direction side, String hotkeyCode) {
        this.packet = packet;
        this.side = side;
        this.hotkeyCode = hotkeyCode;
    }

    public HPacket getPacket() {
        return packet;
    }

    public HMessage.Direction getSide() {
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
