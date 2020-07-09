package life.mvc;

public abstract class AbstractModel implements Model {
    private View view;

    @Override
    public void setView(View view) {
        this.view = view;
    }

    @Override
    public void modelUpdated() {
        view.update();
    }
}
