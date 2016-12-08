package CS151;

import java.util.Arrays;
import java.util.Random;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Line;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class DLine extends DShape implements ModelListener
{

    private Rectangle rect = new Rectangle();
    private Line line;
    private final double handleWidth = 8;
    private final double handleCenter = handleWidth / 2;

    public DLine()
    {
        model = new DLineModel();

        line = new Line(((DLineModel) model).getStart().getX(), ((DLineModel) model).getStart().getY(),
			((DLineModel) model).getEnd().getX(), ((DLineModel) model).getEnd().getY());
        line.setStroke(Color.BLACK);
        //line.setStrokeWidth(5);

        // top left resize handle:
        resizeHandleLeft = new Rectangle(handleWidth, handleWidth, Color.BLACK);
        // bind to top left corner of Rectangle:
        resizeHandleLeft.xProperty().bind(line.startXProperty().subtract(handleCenter));
        resizeHandleLeft.yProperty().bind(line.startYProperty().subtract(handleCenter));

        // right resize handle:
        resizeHandleRight = new Rectangle(handleWidth, handleWidth, Color.BLACK);
        // bind to bottom right corner of Rectangle:
        resizeHandleRight.xProperty().bind(line.endXProperty().subtract(handleCenter));
        resizeHandleRight.yProperty().bind((line.endYProperty().subtract(handleCenter)));

        // force circles to live in same parent as rectangle:
        line.parentProperty().addListener((obs, oldParent, newParent) -> {
            for (Shape r : Arrays.asList(resizeHandleLeft, resizeHandleRight)) {
                Pane currentParent = (Pane) r.getParent();
                if (currentParent != null) {
                    currentParent.getChildren().remove(r);
                }
                ((Pane) newParent).getChildren().add(r);
            }
        });

        Wrapper<Point2D> mouseLocation = new Wrapper<>();

        setUpDragging(line, mouseLocation);
        setUpDragging(resizeHandleLeft, mouseLocation);
        setUpDragging(resizeHandleRight, mouseLocation);

        //TODO: fix out of bound error
        resizeHandleLeft.setOnMouseDragged(event -> {

            if (mouseLocation.value != null) {
                double deltaX = event.getSceneX() - mouseLocation.value.getX();
                double deltaY = event.getSceneY() - mouseLocation.value.getY();
                double newMaxX = line.getStartX() + deltaX;
                double newMaxY = line.getStartY() + deltaY;

                double newX = line.getStartX() + deltaX;
                double newY = line.getStartY() + deltaY;

                if (line.getParent().getBoundsInLocal().getMaxX() - handleWidth >= newMaxX
                        && line.getParent().getBoundsInLocal().getMaxY() - handleWidth >= newMaxY
                        && line.getParent().getBoundsInLocal().getMinY() + handleWidth <= newMaxY
                        && line.getParent().getBoundsInLocal().getMinX() + handleWidth <= newMaxX) {
                    line.setStartX(newX);
                    line.setStartY(newY);

                }
                mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
                updateModel();
                moveToFront();

            }
        });

        resizeHandleRight.setOnMouseDragged(event -> {
            if (mouseLocation.value != null) {
                double deltaX = event.getSceneX() - mouseLocation.value.getX();
                double deltaY = event.getSceneY() - mouseLocation.value.getY();
                double newMaxX = line.getEndX() + deltaX;
                double newMaxY = line.getEndY() + deltaY;

                double newX = line.getEndX() + deltaX;
                double newY = line.getEndY() + deltaY;

                if (line.getParent().getBoundsInLocal().getMaxX() - handleWidth >= newMaxX
                        && line.getParent().getBoundsInLocal().getMaxY() - handleWidth >= newMaxY
                        && line.getParent().getBoundsInLocal().getMinY() + handleWidth <= newMaxY
                        && line.getParent().getBoundsInLocal().getMinX() + handleWidth <= newMaxX) {
                    line.setEndX(newX);
                    line.setEndY(newY);

                }
                mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
                updateModel();
                moveToFront();
            }
        });

        line.setOnMouseClicked(event -> {
            moveToFront();

        });

        line.setOnMouseDragged(event -> {
            if (mouseLocation.value != null) {
                double deltaX = event.getSceneX() - mouseLocation.value.getX();
                double deltaY = event.getSceneY() - mouseLocation.value.getY();
                double newX = rect.getX() + deltaX;
                double newMaxX = newX + rect.getWidth();
                line.setStartX(deltaX + line.getStartX());
                line.setStartY(deltaY + line.getStartY());
                line.setEndX(deltaX + line.getEndX());
                line.setEndY(deltaY + line.getEndY());
                mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
                updateModel();
                moveToFront();

            }

        });

        line.setOnMouseClicked(event -> {
            moveToFront();

        });

    }

    private void setUpDragging(Shape r, Wrapper<Point2D> mouseLocation)
    {

        r.setOnDragDetected(event -> {
            r.getParent().setCursor(Cursor.CLOSED_HAND);
            mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
        });

        r.setOnMouseReleased(event -> {
            r.getParent().setCursor(Cursor.DEFAULT);
            mouseLocation.value = null;
        });
    }

    private void moveToFront()
    {
        line.toFront();
        resizeHandleLeft.toFront();
        resizeHandleRight.toFront();
        updateModel();
    }

    @Override
    public void drawKnobs()
    {
        resizeHandleLeft.setVisible(true);
        resizeHandleRight.setVisible(true);
    }

    @Override
    public void removeKnobs()
    {
        //resizeHandleRight.setVisible(false);
        //resizeHandleLeft.setVisible(false);
    }

    static class Wrapper<T>
    {

        T value;
    }

    @Override
    public void draw()
    {
        line.setStroke(model.getColor());
        double startX = ((DLineModel) model).getStart().getX();
        double startY = ((DLineModel) model).getStart().getY();
        double endX = ((DLineModel) model).getEnd().getX();
        double endY = ((DLineModel) model).getEnd().getY();
        line.setStartX(startX);
        line.setStartY(startY);
        line.setEndX(endX);
        line.setEndY(endY);
        updateModel();
    }

    private void updateModel()
    {

        Point2D start = new Point2D(line.getStartX(), line.getStartY());
        Point2D end = new Point2D(line.getEndX(), line.getEndY());

        ((DLineModel) model).setStart(start);
        ((DLineModel) model).setEnd(end);

    }

    public void setModel(DShapeModel model)
    {
        this.model = model;
        Point2D start = new Point2D(model.getX(), model.getY());
        Point2D end = new Point2D(model.getX() + model.getWidth(), model.getY() + model.getWidth());
        ((DLineModel) model).setStart(start);
        ((DLineModel) model).setEnd(end);
    }

    public Shape getShape()
    {
        return line;
    }

    /*
    public void randomize(int max)
    {
        model.randomize(max);
        Random rand = new Random();
        int startY = rand.nextInt(model.getHeight()) + model.getY();
        int endY = rand.nextInt(model.getHeight()) + model.getY();
        Point start = new Point(model.getX(), startY);
        Point end = new Point(model.getX() + model.getWidth(), endY);
        ((DLineModel) model).setStart(start);
        ((DLineModel) model).setEnd(end);
    }
    */
}
