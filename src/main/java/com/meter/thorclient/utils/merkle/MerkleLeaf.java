package com.meter.thorclient.utils.merkle;


import com.meter.thorclient.utils.BytesUtils;
import com.meter.thorclient.utils.Prefix;

import java.security.MessageDigest;

public class MerkleLeaf extends TreeNode {

    private byte[] value;
    private IBinaryTreeNode parent;
    private NodeType type = NodeType.single;

    public MerkleLeaf(final byte[] value, MessageDigest digest){
        if (value == null) {
            throw new IllegalArgumentException( "Merkle leaf's value is null" );
        }
        if (digest != null){
            this.value = digest.digest( value );
            digest.reset();
        }else{
            this.value = value;
        }

    }

    public MerkleLeaf(final byte[] value){
        this(value, null);
    }


    /**
     * Convert to the hex string.
     * @return
     */
    public String toString(){
       return BytesUtils.toHexString( value, Prefix.ZeroLowerX);
    }

    @Override
    public IBinaryTreeNode getParent() {
        return this.parent;
    }

    @Override
    public IBinaryTreeNode getLeftChild() {
        return null;
    }

    @Override
    public IBinaryTreeNode getRightChild() {
        return null;
    }


    @Override
    public void setParent(IBinaryTreeNode parent) {
        this.parent =  parent;
    }

    @Override
    public void addChild(IBinaryTreeNode leftChildTree, IBinaryTreeNode rightChildTree) {
        throw new UnsupportedOperationException( "add child is not supported." );
    }

    @Override
    public byte[] getValue() {
        return value;
    }

    @Override
    public NodeType getType() {
        return this.type;
    }

    @Override
    public void setType(NodeType type) {
        this.type = type;
    }
}
