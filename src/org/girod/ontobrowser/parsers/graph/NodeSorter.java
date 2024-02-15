/*
Copyright (c) 2024 Hervé Girod
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Alternatively if you have any questions about this project, you can visit
the project website at the project page on https://github.com/hervegirod/ontologyBrowser
 */
package org.girod.ontobrowser.parsers.graph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.girod.jgraphml.model.GraphMLNode;
import org.girod.jgraphml.model.IGraphMLNode;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlProperty;
import org.girod.ontobrowser.model.OwlSchema;

/**
 * A clas which sorts nodes to determine their relative positions.
 *
 * @since 0.11
 */
public class NodeSorter {

    private static final short LEVEL_SAME = 0;
    private static final short LEVEL_SOUTH = 1;
    private static final short LEVEL_NORTH = 2;
    private final OwlSchema schema;
    private float distance = 10;
    private boolean hasSuperClassLayout = true;
    private boolean hasChildrenLayout = false;
    private int maximumSteps = 4;
    private final Map<ElementKey, IGraphMLNode> elementToNode;
    private final Map<ElementKey, Map<ElementKey, Short>> elementLevels = new HashMap<>();

    public NodeSorter(OwlSchema schema, Map<ElementKey, IGraphMLNode> elementToNode) {
        this.schema = schema;
        this.elementToNode = elementToNode;
    }

    public void setMaximumSteps(int maximumSteps) {
        this.maximumSteps = maximumSteps;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void setLayoutOption(short layoutOptions) {
        switch (layoutOptions) {
            case LayoutOptions.ANY_POSITION:
                this.hasSuperClassLayout = false;
                this.hasChildrenLayout = false;
                break;
            case LayoutOptions.SUBCLASS_SOUTH_POSITION:
                this.hasSuperClassLayout = true;
                this.hasChildrenLayout = false;
                break;
            case LayoutOptions.CHILDREN_SOUTH_POSITION:
                this.hasSuperClassLayout = true;
                this.hasChildrenLayout = true;
                break;
            default:
                this.hasSuperClassLayout = false;
                this.hasChildrenLayout = false;
                break;
        }
    }

    public void apply() {
        addToLevelsMap();
        for (int i = 0; i < maximumSteps; i++) {
            boolean isModified = computePositions();
            if (!isModified) {
                break;
            }
        }
    }

    private boolean computePositions() {
        boolean isModified = false;
        Iterator<Entry<ElementKey, Map<ElementKey, Short>>> it = elementLevels.entrySet().iterator();
        while (it.hasNext()) {
            Entry<ElementKey, Map<ElementKey, Short>> entry = it.next();
            ElementKey key = entry.getKey();
            GraphMLNode node = (GraphMLNode) elementToNode.get(key);
            float theHeight = node.getHeight();
            float yPos = node.getY();
            Map<ElementKey, Short> map = entry.getValue();
            Iterator<Entry<ElementKey, Short>> it2 = map.entrySet().iterator();
            while (it2.hasNext()) {
                Entry<ElementKey, Short> entry2 = it2.next();
                ElementKey key2 = entry2.getKey();
                GraphMLNode node2 = (GraphMLNode) elementToNode.get(key2);
                short level = entry2.getValue();
                float yPos2 = node2.getY();
                float theHeight2 = node2.getHeight();
                if (level == LEVEL_SOUTH) {
                    if (yPos2 < yPos + theHeight + distance) {
                        node2.setY(yPos + theHeight + distance + theHeight2);
                        isModified = true;
                    }
                } else if (level == LEVEL_NORTH) {
                    if (yPos2 > yPos - theHeight - distance) {
                        node2.setY(yPos - theHeight - distance - theHeight2);
                        isModified = true;
                    }
                }
            }
        }
        return isModified;
    }

    private void addToLevelsMap() {
        Map<ElementKey, OwlClass> owlClasses = schema.getOwlClasses();
        Iterator<Entry<ElementKey, OwlClass>> it = owlClasses.entrySet().iterator();
        while (it.hasNext()) {
            Entry<ElementKey, OwlClass> entry = it.next();
            ElementKey fromKey = entry.getKey();
            if (elementToNode.containsKey(fromKey)) {
                OwlClass theClass = entry.getValue();
                if (includeClass(theClass)) {
                    // super classes
                    Iterator<OwlClass> it2 = theClass.getSuperClasses().values().iterator();
                    while (it2.hasNext()) {
                        OwlClass superClass = it2.next();
                        if (includeClass(theClass, superClass)) {
                            ElementKey toKey = superClass.getKey();
                            if (elementToNode.containsKey(toKey)) {
                                addClassToLevel(fromKey, toKey, LEVEL_NORTH);
                                addClassToLevel(toKey, fromKey, LEVEL_SOUTH);
                            }
                        }
                    }
                    // data properties
                    if (hasChildrenLayout) {
                        Iterator<OwlProperty> it3 = theClass.getDomainOwlProperties().values().iterator();
                        while (it3.hasNext()) {
                            OwlProperty property = it3.next();
                            if (property.isDatatypeProperty()) {
                                ElementKey propKey = property.getKey();
                                if (elementToNode.containsKey(propKey)) {
                                    addClassToLevel(fromKey, propKey, LEVEL_SOUTH);
                                    addClassToLevel(propKey, fromKey, LEVEL_NORTH);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean includeClass(OwlClass superClass, OwlClass theClass) {
        if (!includeClass(superClass)) {
            return false;
        } else {
            if (!superClass.isInPackage() && !theClass.isInPackage()) {
                return true;
            } else if (superClass.isInPackage() != theClass.isInPackage()) {
                return false;
            } else if (superClass.isInUniquePackage() != theClass.isInUniquePackage()) {
                return false;
            } else {
                return superClass.getPackage().equals(theClass.getPackage());
            }
        }
    }

    private boolean includeClass(OwlClass theClass) {
        if (theClass.hasSuperClasses()) {
            if (!theClass.isPackage()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void addClassToLevel(ElementKey fromKey, ElementKey toKey, short level) {
        Map<ElementKey, Short> map;
        if (elementLevels.containsKey(fromKey)) {
            map = elementLevels.get(fromKey);
        } else {
            map = new HashMap<>();
            elementLevels.put(fromKey, map);
        }
        map.put(toKey, level);
    }
}
