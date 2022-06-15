package io.github.vqnxiv;


import io.github.vqnxiv.layout.RandomLayout;
import io.github.vqnxiv.node.DecoratedNode;
import io.github.vqnxiv.node.TimedNodePool;
import io.github.vqnxiv.structure.CoordinatesElement;
import io.github.vqnxiv.structure.impl.CoordinatesMatrix;
import io.github.vqnxiv.view.NodeVirtualizer;
import io.github.vqnxiv.view.ThrottledNodeVirtualizer;
import io.github.vqnxiv.view.VirtualizerRegion;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;


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

        // private T element;
        private Optional<T> element;
        
        DecoratedNodeLabel(T t) {
            label = new Label(t.toString());
            // element = t;
            element = Optional.of(t);
        }
        
        @Override
        public Node getNode() {
            return label;
        }

        @Override
        public Optional<T> getDecorator() {
            // return Optional.ofNullable(element);
            return element;
        }

        @Override
        public void setDecorator(T t) {
            // element = t;
            element = Optional.of(t);
            label.setText(element.toString());
        }

        @Override
        public void clearDecoration() {
            // element = null;
            element = Optional.empty();
            label.setText("");
        }
    }
    
    
    
    private static class AnotherThing {
        private final Color color;
        private final boolean circle;
        
        AnotherThing() {
            color = Color.rgb(
                ThreadLocalRandom.current().nextInt(255), 
                ThreadLocalRandom.current().nextInt(255), 
                ThreadLocalRandom.current().nextInt(255) 
            );
            circle = ThreadLocalRandom.current().nextBoolean();
        }
    }
    
    
    private static class DecoratedShape implements DecoratedNode<CoordinatesElement<AnotherThing>> {

        // private CoordinatesElement<AnotherThing> anotherThing;
        private Optional<CoordinatesElement<AnotherThing>> anotherThing;
        private Shape shape;
        
        DecoratedShape(CoordinatesElement<AnotherThing> anotherThing) {
            setDecorator(anotherThing);
        }
        
        @Override
        public Node getNode() {
            return shape;
        }

        @Override
        public Optional<CoordinatesElement<AnotherThing>> getDecorator() {
            // return Optional.of(anotherThing);
            return anotherThing;
        }

        @Override
        public void setDecorator(CoordinatesElement<AnotherThing> anotherThing) {
            // this.anotherThing = anotherThing;
            this.anotherThing = Optional.of(anotherThing);
            shape = (anotherThing.getElement().circle) ? new Circle(10) : new Rectangle(30, 15);
            shape.setFill(anotherThing.getElement().color);
        }

        @Override
        public void clearDecoration() {
            // anotherThing = null;
            anotherThing = Optional.empty();
        }
    }
    

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception { ;
        StackPane pane = new StackPane();
        
        List<Thing> l = new ArrayList<>(1_000_000);
        
        for(int i = 0; i < 1_000_000; i++) {
            l.add(new Thing(Integer.toString(i * 10, 2), i));
        }
        
        List<AnotherThing> l2 = new ArrayList<>(500_000);
        
        for(int i = 0; i < 500_000; i++) {
            l2.add(new AnotherThing());
        }
        
        int n = 1_000;
        double z = 200_000d;
        
        // var struct = new CoordinatesList<>(l, s -> new RandomLayout<>(s, 0d, 0d, z, z));
        // var struct = new CoordinatesMatrix<>(l, z, z, n, n, 1.5f, 1.5f, n, n);
        var struct = new CoordinatesMatrix<>(
            l, s -> new RandomLayout<>(s, 0d, 0d, z, z),
            z, z, n, n, 1.5f, 1.5f, n, n
        );
        
        var struct2 = new CoordinatesMatrix<>(
            l2, s -> new RandomLayout<>(s, 0d, 0d, z, z),
            z, z, n, n, 1.5f, 1.5f, n, n
        );

        // var pool = new SetNodePool<>((CoordinatesElement<Thing> t) -> new DecoratedNodeLabel<>(t));
        // var pool = new WeakNodePool<>((CoordinatesElement<Thing> t) -> new DecoratedNodeLabel<>(t));
        // var pool2 = new WeakNodePool<>(DecoratedShape::new);
        
        
        var pool = new TimedNodePool<>(
            (CoordinatesElement<Thing> t) -> new DecoratedNodeLabel<>(t),
            10_000L,
            20
        );
        
        var pool2 = new TimedNodePool<>(
            DecoratedShape::new,
            10_000L,
            20
        );
        
        
        // var nv = new NodeVirtualizer<>(struct, pool);
        var nv = new ThrottledNodeVirtualizer(
            List.of(
                new NodeVirtualizer.StructureToPool<>(struct2, pool2),
                new NodeVirtualizer.StructureToPool<>(struct, pool)
            ), 
            true);
        // var nv = new NodeVirtualizer(List.of(new NodeVirtualizer.StructureToPool<>(struct, pool)));
      
        var vr = new VirtualizerRegion(nv);

        vr.setPrefSize(300d, 300d);
        
        pane.getChildren().add(vr);

        stage.setScene(new Scene(pane));
        stage.show();
    }
}
