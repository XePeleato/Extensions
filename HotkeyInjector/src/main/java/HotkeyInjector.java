import gearth.extensions.ExtensionForm;
import gearth.extensions.ExtensionInfo;
import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

@ExtensionInfo(
        Title = "Hotkey Injector",
        Description = "Injector with hotkey support",
        Version = "1.0",
        Author = "XePeleato"
)

public class HotkeyInjector extends ExtensionForm implements NativeKeyListener {
    public TextArea packetTxt;
    public TextField keyTF;
    public Button addBtn;
    public ListView packetList;
    public Label packetValidLbl;
    public ChoiceBox packetSideCb;

    private LinkedList<InjectorEntry> mInjectorList = new LinkedList<>();

    private int mKeyCode = -1;

    public static void main(String[] args) {
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);

        runExtensionForm(args, HotkeyInjector.class);
    }

    private boolean isPacketIncomplete(String line) {
        int unMatchedBraces = 0;
        for (int i = 0; i < line.length(); i++)
            if (line.charAt(i) == '{')
                unMatchedBraces++;
            else if (line.charAt(i) == '}')
                unMatchedBraces--;

            return unMatchedBraces != 0;
    }

    private HPacket[] parsePackets(String fullText) {
        LinkedList<HPacket> packets = new LinkedList<>();
        String[] lines = fullText.split("\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            while (isPacketIncomplete(line) && i < lines.length - 1)
                line += '\n' + lines[++i];

            packets.add(new HPacket(line));
        }
        return packets.toArray(new HPacket[0]);
    }

    public ExtensionForm launchForm(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("HotkeyInjector.fxml"));
        Parent root = loader.load();

        stage.setTitle("Hotkey Injector");
        stage.setScene(new Scene(root));
        stage.setResizable(false);

        return loader.getController();
    }

    @Override
    protected void initExtension() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }

        GlobalScreen.addNativeKeyListener(this);

        ObservableList choices = FXCollections.observableArrayList("Client", "Server");

        packetSideCb.setItems(choices);
        packetSideCb.setValue("Client");

        keyTF.textProperty().addListener(ev -> {
            HPacket[] packets = parsePackets(packetTxt.getText());

            for (HPacket p : packets)
                if (p.isCorrupted())
                    return;

            addBtn.setDisable(false);
        });

            packetTxt.textProperty().addListener(ev -> {
                boolean dirty = false;

            HPacket[] packets = parsePackets(packetTxt.getText());

            if (packets.length == 0) {
                dirty = true;
                packetValidLbl.setText("Invalid packet list");
            }

            for (HPacket packet : packets) {
                if (packet.isCorrupted()) {
                    if (!dirty) {
                        packetValidLbl.setText("Invalid packet list");
                        dirty = true;
                    }
                }

                if (dirty) {
                    packetValidLbl.setText("Invalid packet list");
                    addBtn.setDisable(true);
                } else {
                    packetValidLbl.setText("Packets verified");
                    if (mKeyCode > 0)
                        addBtn.setDisable(false);
                }
            }
        });
    }

    public void onAddBtnClick(ActionEvent actionEvent) {
        HPacket[] packets = parsePackets(packetTxt.getText());
        for (HPacket packet : packets) {
            InjectorEntry entry = new InjectorEntry(packet, packetSideCb.getValue().equals("Client") ?
                    HMessage.Direction.TOCLIENT : HMessage.Direction.TOSERVER, NativeKeyEvent.getKeyText(mKeyCode));

            mInjectorList.add(entry);
            packetList.getItems().add(NativeKeyEvent.getKeyText(mKeyCode) + " ~ " + packetSideCb.getValue() + " ~ " + packet.toExpression());
            packetList.scrollTo(packetList.getItems().size() - 1);
        }
    }

    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {}

    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        if (keyTF.isFocused()) {
            keyTF.setText(NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()));
            mKeyCode = nativeKeyEvent.getKeyCode();
        }


        mInjectorList.forEach(entry -> {
            if (!entry.getHotkeyCode().equals(NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()))) return;

            if (entry.getSide() == HMessage.Direction.TOCLIENT)
                sendToClient(entry.getPacket());
            else
                sendToServer(entry.getPacket());
        });
    }

    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) { }

    public void onRemoveBtnClick(ActionEvent actionEvent) {
        if (packetList.getSelectionModel().getSelectedItem() == null) return;

        String entry = (String) packetList.getSelectionModel().getSelectedItem();
        String[] keycodeAndPacket = entry.split(" ~ ");
        packetList.getItems().remove(entry);
        mInjectorList.removeIf( e -> e.getHotkeyCode().equals(keycodeAndPacket[0]) &&
                e.getSide() == (keycodeAndPacket[1].equals("Client") ? HMessage.Direction.TOCLIENT : HMessage.Direction.TOSERVER) &&
                e.getPacket().equals(new HPacket(keycodeAndPacket[2])));
    }
}
