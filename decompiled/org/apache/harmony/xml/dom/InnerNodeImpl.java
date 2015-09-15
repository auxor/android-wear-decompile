package org.apache.harmony.xml.dom;

import java.util.ArrayList;
import java.util.List;
import libcore.util.Objects;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;

public abstract class InnerNodeImpl extends LeafNodeImpl {
    List<LeafNodeImpl> children;

    protected InnerNodeImpl(DocumentImpl document) {
        super(document);
        this.children = new ArrayList();
    }

    public Node appendChild(Node newChild) throws DOMException {
        return insertChildAt(newChild, this.children.size());
    }

    public NodeList getChildNodes() {
        NodeListImpl list = new NodeListImpl();
        for (NodeImpl node : this.children) {
            list.add(node);
        }
        return list;
    }

    public Node getFirstChild() {
        return !this.children.isEmpty() ? (LeafNodeImpl) this.children.get(0) : null;
    }

    public Node getLastChild() {
        return !this.children.isEmpty() ? (LeafNodeImpl) this.children.get(this.children.size() - 1) : null;
    }

    public Node getNextSibling() {
        if (this.parent == null || this.index + 1 >= this.parent.children.size()) {
            return null;
        }
        return (Node) this.parent.children.get(this.index + 1);
    }

    public boolean hasChildNodes() {
        return this.children.size() != 0;
    }

    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        LeafNodeImpl refChildImpl = (LeafNodeImpl) refChild;
        if (refChildImpl == null) {
            return appendChild(newChild);
        }
        if (refChildImpl.document != this.document) {
            throw new DOMException((short) 4, null);
        } else if (refChildImpl.parent == this) {
            return insertChildAt(newChild, refChildImpl.index);
        } else {
            throw new DOMException((short) 3, null);
        }
    }

    Node insertChildAt(Node newChild, int index) throws DOMException {
        if (newChild instanceof DocumentFragment) {
            NodeList toAdd = newChild.getChildNodes();
            for (int i = 0; i < toAdd.getLength(); i++) {
                insertChildAt(toAdd.item(i), index + i);
            }
        } else {
            LeafNodeImpl toInsert = (LeafNodeImpl) newChild;
            if (toInsert.document != null && this.document != null && toInsert.document != this.document) {
                throw new DOMException((short) 4, null);
            } else if (toInsert.isParentOf(this)) {
                throw new DOMException((short) 3, null);
            } else {
                if (toInsert.parent != null) {
                    int oldIndex = toInsert.index;
                    toInsert.parent.children.remove(oldIndex);
                    toInsert.parent.refreshIndices(oldIndex);
                }
                this.children.add(index, toInsert);
                toInsert.parent = this;
                refreshIndices(index);
            }
        }
        return newChild;
    }

    public boolean isParentOf(Node node) {
        for (LeafNodeImpl nodeImpl = (LeafNodeImpl) node; nodeImpl != null; nodeImpl = nodeImpl.parent) {
            if (nodeImpl == this) {
                return true;
            }
        }
        return false;
    }

    public final void normalize() {
        Node node = getFirstChild();
        while (node != null) {
            Node next = node.getNextSibling();
            node.normalize();
            if (node.getNodeType() == (short) 3) {
                ((TextImpl) node).minimize();
            }
            node = next;
        }
    }

    private void refreshIndices(int fromIndex) {
        for (int i = fromIndex; i < this.children.size(); i++) {
            ((LeafNodeImpl) this.children.get(i)).index = i;
        }
    }

    public Node removeChild(Node oldChild) throws DOMException {
        LeafNodeImpl oldChildImpl = (LeafNodeImpl) oldChild;
        if (oldChildImpl.document != this.document) {
            throw new DOMException((short) 4, null);
        } else if (oldChildImpl.parent != this) {
            throw new DOMException((short) 3, null);
        } else {
            int index = oldChildImpl.index;
            this.children.remove(index);
            oldChildImpl.parent = null;
            refreshIndices(index);
            return oldChild;
        }
    }

    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        int index = ((LeafNodeImpl) oldChild).index;
        removeChild(oldChild);
        insertChildAt(newChild, index);
        return oldChild;
    }

    public String getTextContent() throws DOMException {
        Node child = getFirstChild();
        if (child == null) {
            return XmlPullParser.NO_NAMESPACE;
        }
        if (child.getNextSibling() == null) {
            return hasTextContent(child) ? child.getTextContent() : XmlPullParser.NO_NAMESPACE;
        } else {
            StringBuilder buf = new StringBuilder();
            getTextContent(buf);
            return buf.toString();
        }
    }

    void getTextContent(StringBuilder buf) throws DOMException {
        for (Node child = getFirstChild(); child != null; child = child.getNextSibling()) {
            if (hasTextContent(child)) {
                ((NodeImpl) child).getTextContent(buf);
            }
        }
    }

    final boolean hasTextContent(Node child) {
        return (child.getNodeType() == (short) 8 || child.getNodeType() == (short) 7) ? false : true;
    }

    void getElementsByTagName(NodeListImpl out, String name) {
        for (NodeImpl node : this.children) {
            if (node.getNodeType() == (short) 1) {
                ElementImpl element = (ElementImpl) node;
                if (matchesNameOrWildcard(name, element.getNodeName())) {
                    out.add(element);
                }
                element.getElementsByTagName(out, name);
            }
        }
    }

    void getElementsByTagNameNS(NodeListImpl out, String namespaceURI, String localName) {
        for (NodeImpl node : this.children) {
            if (node.getNodeType() == (short) 1) {
                ElementImpl element = (ElementImpl) node;
                if (matchesNameOrWildcard(namespaceURI, element.getNamespaceURI()) && matchesNameOrWildcard(localName, element.getLocalName())) {
                    out.add(element);
                }
                element.getElementsByTagNameNS(out, namespaceURI, localName);
            }
        }
    }

    private static boolean matchesNameOrWildcard(String pattern, String s) {
        return "*".equals(pattern) || Objects.equal(pattern, s);
    }
}
