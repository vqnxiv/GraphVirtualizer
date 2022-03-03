/**
 * Module which contains the base virtualization tools.
 */
module io.github.vqnxiv.virtualizer {
    requires javafx.graphics;
    requires javafx.controls;
    
    exports io.github.vqnxiv.structure;
    exports io.github.vqnxiv.view;
    exports io.github.vqnxiv.misc;
    exports io.github.vqnxiv.node;
    exports io.github.vqnxiv.layout;
    exports io.github.vqnxiv.structure.impl;
}