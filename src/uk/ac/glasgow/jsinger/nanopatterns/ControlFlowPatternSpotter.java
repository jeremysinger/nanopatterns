// ControlFlowPatternSpotter.java
// Jeremy Singer
// 18 Nov 08

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Label;

import java.util.ArrayList;

/**
 * simple method scanner (asm-speak adapter) to 
 * check whether a method has any object-oriented
 * behaviour (field sets / gets) and new object
 * instantiations.
 * - (statically) - in which case this
 * scanner reports that the class implements the 
 * appropriate micro-patterns
 */

public class ControlFlowPatternSpotter extends MethodAdapter{

    // use numJumpInsns to determine whether this
    // method is straight-line code (no jumps)
    private int numJumpInsns;
    // use backwardsJump to determine whether this
    // method contains loops (back-edge indicates looping
    // construct in general)
    private boolean backwardsJump;
    // use following list to store labels we have already encountered
    // in our linear scan through the method bytecode - this
    // enables us to determine which jumps are backwards - i.e.
    // to already seen labels
    // Java Generics:
    // private ArrayList<String> alreadySeenLabels;
    private ArrayList alreadySeenLabels;

    public ControlFlowPatternSpotter(MethodVisitor mv) {
	super(mv);
	this.numJumpInsns = 0;
	this.backwardsJump = false;
	this.alreadySeenLabels = new ArrayList();
	
    }
    
    //@Override
    public void visitJumpInsn(int opcode,
			      Label label) {
	this.numJumpInsns++;
	// System.out.println("found a jump insn");
	
	// check whether we have already seen
	// this label
	String labelStr = label.toString();
	if (alreadySeenLabels.contains(labelStr)) {
	    // this is a backwards jump
	    this.backwardsJump = true;
	    // System.out.println("found a backwards jump to label " + labelStr);
	}
    }
    
    // @Override
    public void visitLabel(Label label) {
	String labelStr = label.toString();
	if (!alreadySeenLabels.contains(labelStr)) {
	    alreadySeenLabels.add(labelStr);
	    // System.out.println("found new label " + labelStr);
	}
    }

    
    // accessor methods for computed meta-data about method

    public boolean isStraightLineCode() {
	return this.numJumpInsns==0;
    }

    public boolean isLoopingCode() {
	return this.backwardsJump;
    }

}
