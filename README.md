### GraphVirtualizer (ALPHA)

---

A few controls to easily allow control 'virtualization' in JavaFX.

Originally made for large graphs (total of millions of nodes and displaying a few thousands),
the controls may be useful for other uses so they are in a different module.

---

### Virtualization

#### 'How do':
* The main class is a `Virtualizer`, which contains a `view` (a JFX `Node`)
on which it places and moves its elements when it is shifted.
* The `view` can be displayed in a JFX scene like any other `Node`. 
An extension of `Region` whose role is to display and control a `Virtualizer`
and its `view` is provided (`AbstractAbleRegion` & `VirtualizerRegion`).
* The `Virtualizer` gets its elements from a `CoordinatesStructure`,
which are data structures that contain elements wrapped in `CoordinatesElement` 
(and thus are linked to a pair of coordinates [x, y]).
* The `CoordinatesStructure` can be handed to a `Layout`, which can
(re)position its elements.

#### Implementation details:
* For a `Node` based `Virtualizer` (`NodeVirtualizer`), which basically places
JFX `Node`s on a `Pane`, the conversion `CoordinatesElement` -> `Node` is done
by a `NodePool` that keeps already created nodes so they can be re-used later.
Then, when the element is no longer in the area shown by the `Virtualizer`'s `view`,
its associated `Node` is given back to the `NodePool` for later use.

---

#### Todo:

* `CoordinatesStructure` extending Java's `Collection`
* Event based `CoordinatesStructure`
* Drawn tiles based implementation of `Virtualizer`
* Graph related stuff will be added when at least event based `CoordinatesStuff` is done