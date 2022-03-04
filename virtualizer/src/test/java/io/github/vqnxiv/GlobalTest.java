package io.github.vqnxiv;


import io.github.vqnxiv.node.TimedNodePool;
import io.github.vqnxiv.structure.CoordinatesElement;
import io.github.vqnxiv.node.DecoratedNode;
import io.github.vqnxiv.structure.impl.CoordinatesMatrix;
import io.github.vqnxiv.view.ThrottledNodeVirtualizer;
import io.github.vqnxiv.view.VirtualizerRegion;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class GlobalTest extends Application {

    
    
    private static class Thing {
        private final String name;
        private final int id;
        
        Thing(String name, int id) {
            this.name = name;
            this.id = id;
        }
        
        @Override
        public String toString() {
            // return String.format("[%d] %s", id, name);
            return "[" + id + "]";
        }
    } 
    
    private static class DecoratedNodeLabel<T> implements DecoratedNode<T> {
        
        
        private final Label label;

        private T element;
        
        DecoratedNodeLabel(T t) {
            label = new Label(t.toString());
            element = t;
        }
        
        /**
         * Getter for the node.
         *
         * @return The node.
         */
        @Override
        public Node getNode() {
            return label;
        }

        /**
         * Getter for the decorator.
         *
         * @return The decorator.
         */
        @Override
        public Optional<T> getDecorator() {
            return Optional.ofNullable(element);
        }

        /**
         * Setter for the decorator.
         *
         * @param t New decorator.
         */
        @Override
        public void setDecorator(T t) {
            element = t;
            label.setText(element.toString());
        }

        /**
         * Whether the node is currently decorated.
         *
         * @return {@code true} if this node is decorated.
         */
        @Override
        public boolean isDecorated() {
            return element != null;
        }

        /**
         * Clears the decorator.
         */
        @Override
        public void clearDecoration() {
            element = null;
            label.setText("");
        }
    }
    

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // AnchorPane pane = new AnchorPane();
        StackPane pane = new StackPane();
        
        List<Thing> l = new ArrayList<>(100);
        
        for(int i = 0; i < 2_000_000; i++) {
            l.add(new Thing(Integer.toString(i * 10, 2), i));
        }
        
        int n = 1000;
        double z = 20_000d;
        // var struct = new CoordinatesList<>(l);
        var struct = new CoordinatesMatrix<>(l, z, z, n, n, 1.5f, 1.5f, n, n);
        // var pool = new SetNodePool<>((CoordinatesElement<Thing> t) -> new DecoratedNodeLabel<>(t));
        var pool = new TimedNodePool<>(
            (CoordinatesElement<Thing> t) -> new DecoratedNodeLabel<>(t),
            10_000L,
            20);
        
        
        // var nv = new NodeVirtualizer<>(struct, pool);
        var nv = new ThrottledNodeVirtualizer<>(struct, pool, true);
        var vr = new VirtualizerRegion(nv);
        vr.setPrefSize(300d, 300d);
        
        pane.getChildren().add(vr);

        // AnchorPane.setTopAnchor(vr, 0d);
        // AnchorPane.setBottomAnchor(vr, 0d);
        // AnchorPane.setLeftAnchor(vr, 0d);
        // AnchorPane.setRightAnchor(vr, 0d);
        
        stage.setScene(new Scene(pane));
        stage.show();
    }
}
