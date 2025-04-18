import gearth.extensions.ExtensionForm;
import gearth.extensions.ExtensionFormCreator;
import gearth.extensions.ExtensionInfo;
import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

@ExtensionInfo(
        Title = "PosterMover",
        Description = "Utility to move posters",
        Version = "1.0",
        Author = "XePeleato"
)

public class PosterMover extends ExtensionForm {
    private int mPosterId;
    private int mPosterX;
    private int mPosterY;
    private int mPosterW;
    private int mPosterH;
    private char mPosterO;

    public Button upBtn;
    public Button downBtn;
    public Button leftBtn;
    public Button rightBtn;
    public Button altUpBtn;
    public Button altDownBtn;
    public Button altLeftBtn;
    public Button altRightBtn;
    public Button rotateBtn;



    public ExtensionForm launchForm(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("postermover.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("PosterMover");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.setAlwaysOnTop(true);

        return loader.getController();
    }

    @Override
    protected void initExtension() {
        upBtn.setDisable(true);
        downBtn.setDisable(true);
        leftBtn.setDisable(true);
        rightBtn.setDisable(true);
        altUpBtn.setDisable(true);
        altDownBtn.setDisable(true);
        altLeftBtn.setDisable(true);
        altRightBtn.setDisable(true);
        rotateBtn.setDisable(true);

        intercept(HMessage.Direction.TOSERVER, "MoveWallItem", hMessage -> {
            //:w={x},{y} l={w},{h} {o}
            mPosterId = hMessage.getPacket().readInteger();
            String coords = hMessage.getPacket().readString();
            System.out.println(coords);

            String xString = coords.substring(coords.indexOf("w=") + 2, coords.indexOf(','));
            String yString = coords.substring(coords.indexOf(',') + 1, coords.indexOf(' '));
            int lIndex = coords.indexOf("l=");
            String wString = coords.substring(lIndex + 2, coords.indexOf(',', lIndex));
            String hString = coords.substring(coords.indexOf(',', lIndex) + 1, coords.indexOf(' ', lIndex));

            mPosterX = Integer.decode(xString);
            mPosterY = Integer.decode(yString);
            mPosterW = Integer.decode(wString);
            mPosterH = Integer.decode(hString);
            mPosterO = coords.charAt(coords.length() - 1);

            upBtn.setDisable(false);
            downBtn.setDisable(false);
            leftBtn.setDisable(false);
            rightBtn.setDisable(false);
            altUpBtn.setDisable(false);
            altDownBtn.setDisable(false);
            altLeftBtn.setDisable(false);
            altRightBtn.setDisable(false);
            rotateBtn.setDisable(false);
        });
    }

    private void sendPoster() {
        HPacket packet = new HPacket("MoveWallItem", HMessage.Direction.TOSERVER, mPosterId, ":w=" + mPosterX + "," + mPosterY + " l=" +
                mPosterW + "," + mPosterH + " " + mPosterO);
        sendToServer(packet);
    }

    public void onClickUp(ActionEvent actionEvent) {
        mPosterH--;
        sendPoster();
    }

    public void onClickDown(ActionEvent actionEvent) {
        mPosterH++;
        sendPoster();
    }

    public void onClickLeft(ActionEvent actionEvent) {
        mPosterW--;
        sendPoster();
    }

    public void onClickRight(ActionEvent actionEvent) {
        mPosterW++;
        sendPoster();
    }

    public void onClickAltUp(ActionEvent actionEvent) {
        if (mPosterO == 'r' )
            mPosterY++;
        else
            mPosterX++;

        sendPoster();
    }

    public void onClickAltDown(ActionEvent actionEvent) {
        if (mPosterO == 'r')
            mPosterY--;
        else
            mPosterX--;

        sendPoster();
    }

    public void onClickAltLeft(ActionEvent actionEvent) {
        if (mPosterO == 'r')
            mPosterX--;
        else
            mPosterY++;

        sendPoster();
    }

    public void onClickAltRight(ActionEvent actionEvent) {
        if (mPosterO == 'r')
            mPosterX++;
        else
            mPosterY--;

        sendPoster();
    }

    public void onClickRotate(ActionEvent actionEvent) {
        if (mPosterO == 'r') {
            mPosterO = 'l';
        } else {
            mPosterO = 'r';
        }

        sendPoster();
    }
}
