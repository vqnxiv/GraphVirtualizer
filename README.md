### GraphVirtualizer (ALPHA)

---

A few controls to easily allow control 'virtualization' in JavaFX.

Originally made for large graphs (total of millions of nodes and displaying a few thousands),
the virtualization part may be useful for other uses so it's in its own module.

---

### Virtualization

#### 'How do':
* The main class is a `Virtualizer`, which contains a `view` (a JFX `Node`)
on which it places and moves its elements when it is shifted.
* The view can be displayed in a JFX scene like any other `Node`. 
An extension of `Region` whose role is to display and control a Virtualizer
and its view is provided (`AbstractAbleRegion` & `VirtualizerRegion`).
* The Virtualizer gets its elements from `CoordinatesStructure`s,
which are data structures that contain elements wrapped in `CoordinatesElement` 
(and thus are linked to a pair of coordinates [x, y]).
* The CoordinatesStructure can be handed to a `Layout`, which can
(re)position its elements.
* CoordinateStructures are 'observable', in the sense that a 'listener' 
can be attached to react to changes made to a structure. This makes it 
so a Virtualizer can automatically update its view when its structure is
changed, and a layout can reposition a structure's elements when it gets
added new elements or another structure is modified.

#### Implementation details:
* For a `Node` based Virtualizer (`NodeVirtualizer`), which basically places
JFX Nodes on a Pane, the conversion CoordinatesElement -> Node is done
by a `NodePool` that keeps already created nodes so they can be re-used later.
Then, when the element is no longer in the area shown by the Virtualizer's view,
its associated Node is given back to the NodePool for later reuse.
* CoordinatesStructure 'listeners' are simply consumers which are called when
the structure is modified. See `StructureChange`, `LayoutableStructure` and 
`MutableStructure`.

---

#### Todo:

* Drawn tiles based implementation of `Virtualizer`