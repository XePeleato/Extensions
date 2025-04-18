import gearth.extensions.ExtensionForm;
import gearth.extensions.ExtensionFormCreator;
import javafx.stage.Stage;

public class PosterMoverLauncher extends ExtensionFormCreator {
    @Override
    protected ExtensionForm createForm(Stage stage) throws Exception {
        return new PosterMover().launchForm(stage);
    }
    public static void main(String[] args) {
        runExtensionForm(args, PosterMoverLauncher.class);
    }
}
