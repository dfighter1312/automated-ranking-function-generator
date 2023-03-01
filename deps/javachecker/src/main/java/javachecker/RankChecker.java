package javachecker;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Interpreter;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;
import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

public class RankChecker {

  // =========================================================================
  // Encoding
  // =========================================================================

  static abstract class Value implements org.objectweb.asm.tree.analysis.Value{
    
    int creationIndex;
    
    public Value(int creationIndex) {
      super();
      this.creationIndex = creationIndex;
    }

    @Override
    public int getSize() {
      return 1;
    }
  }
  
  static class None extends Value {
    public None(int creationIndex) {
      super(creationIndex);
    }

    @Override
    public String toString() {
      return "none";
    }
  }
  
  static class MyString extends Value {
    String val = null;
    
    public MyString(int creationIndex, String val) {
      super(creationIndex);
      this.val = val;
    }

    @Override
    public String toString() {
      return "\"" + val.toString() + "\"";
    }
  }
  
  static class MyStringBuilder extends Value {
    String val = "";
    
    public MyStringBuilder(int creationIndex) {
      super(creationIndex);
    }

    public MyStringBuilder(int creationIndex, String val) {
      super(creationIndex);
      this.val = val;
    }
    
    @Override
    public String toString() {
      return "sb:\"" + val.toString() + "\"";
    }
  }
  
  static class MyClass extends Value {
    Type val = null;
    
    public MyClass(int creationIndex, Type val) {
      super(creationIndex);
      this.val = val;
    }
    
    @Override
    public String toString() {
      return "<" + val.toString() + ">";
    }
  }
  
  static class MyThrowable extends Value {
    Type type = null;
    MyString message = null;
    MyThrowable cause = null;
    
    public MyThrowable(int creationIndex, Type type) {
      super(creationIndex);
      this.type = type;
    }

    public MyThrowable(int creationIndex, Type type, MyString message, MyThrowable cause) {
      super(creationIndex);
      this.type = type;
      this.message = message;
      this.cause = cause;
    }
    
    @Override
    public String toString() {
      if (message != null)
        return "<" + type.getInternalName() + ":" + message.val + ">";
      else
        return "<" + type.getInternalName() + ">";
    }
  }
  
  static class MyPrimitive extends Value {
    IntExpr value;
    
    public MyPrimitive(int creationIndex) {
      super(creationIndex);
    }
 
    public MyPrimitive(int creationIndex, IntExpr value) {
      super(creationIndex);
      this.value = value;
    }
    
    @Override
    public String toString() {
      return value == null ? "uninit" : value.toString();
    }
  }
  
  static class Phi extends Expr {
    boolean fresh;
    int oldIndex;
    
    private Phi(int creationIndex, IntExpr symbol, boolean fresh, int prevIndex) {
      super(creationIndex, symbol);
      this.fresh = fresh;
      this.oldIndex = prevIndex;
    }

    Phi (int creationIndex, IntExpr symbol, int prevIndex) {
      this(creationIndex, symbol, true, prevIndex);
    }
  }
  
  static class Expr extends Value {
    IntExpr expr;

    public Expr(int creationIndex, IntExpr expr) {
      super(creationIndex);
      this.expr = expr;
    }
    
    @Override
    public String toString() {
      return expr.toString();// + "@" + creationIndex;
    }
    
    @Override
    public boolean equals(java.lang.Object obj) {
      if (!(obj instanceof Expr))
        return false;
      
      Expr other = (Expr) obj;
      return other.expr.equals(expr);
    }
    
    @Override
    public int hashCode() {
      return expr.hashCode();
    }
  }
  
  static class Array extends Value {
    Value[] elements;
    
    public Array(int creationIndex, int length) {
      super(creationIndex);
      this.elements = new Value[length];
    }

    @Override
    public String toString() {
      return Arrays.toString(elements);
    }

    @Override
    public boolean equals(java.lang.Object obj) {
      if (!(obj instanceof Array))
        return false;
      
      Array other = (Array) obj;
      return Arrays.deepEquals(this.elements, other.elements);
    }

  }
  
  static class Object extends Value {
    Type type;
    Map<String, Value> fields = new TreeMap<>(); //ordering is important for inlining leafs

    static Integer ids = 0;
    Integer id;
    public Object(int creationIndex, Type type) {
      super(creationIndex);
      this.type = type;
      id = ids++;
    }

    @Override
    public String toString() {
//      String fullName = type.getInternalName().replace('.', '/');
//      return fullName.substring(fullName.lastIndexOf('/') + 1) + "@" + id;
      //return fullName.substring(fullName.lastIndexOf('/') + 1) + fields + "@" + id;
      StringBuilder sb = new StringBuilder();
      sb.append("(");
      fields.forEach((k,v) -> sb.append(v + ","));
      sb.replace(sb.length()-1, sb.length(), ")");
      sb.append("@" + id);
      return sb.toString();
    }
    
    @Override
    public boolean equals(java.lang.Object obj) {
      if (!(obj instanceof Object))
        return false;
      
      Object other = (Object) obj;
      return other.fields.equals(fields);
    }
  }
  
  static class Any extends Value {

    public Any(int creationIndex) {
      super(creationIndex);
    }

    @Override
    public String toString() {
      return "any";
    }

  }

  static class SSAFrame extends Frame<Value> {

    SSAFrame parent;
    Map<String, Map<String, Value>> global;
    
    Frame<Value>[] frames;
    Boolean jump;
    Boolean unreachable;

    Value result = null;
    
    public SSAFrame(SSAFrame parent, int numLocals, int numStack, Frame<Value>[] frames) {
      super(numLocals, numStack);
      assert (parent != null);
      this.parent = new SSAFrame(parent);
      this.global = parent.global;
      this.frames = frames;
      this.jump = null;
      this.unreachable = false;
      assert !parent.unreachable;
    }

    public SSAFrame(Map<String, Map<String, Value>> global, int numLocals, int numStack, Frame<Value>[] frames) {
      super(numLocals, numStack);
      this.parent = null;
      this.global = global;
      this.frames = frames;
      this.jump = null;
      this.unreachable = false;
    }
    
//    public SSAFrame(Map<String, Map<String, Value>> global, int numLocals, int numStack, Frame<Value>[] frames) {
//      super(numLocals, numStack);
//      this.global = global;
//      this.frames = frames;
//      this.jump = null;
//      this.unreachable = false;
//      assert parent == null || !parent.unreachable;
//    }
    
    public SSAFrame(SSAFrame frame) {
      super(frame);
//      if (frame.parent != null) 
//        this.parent = new SSAFrame(frame.parent);
//      this.frames = frame.frames;
//      this.unreachable = frame.unreachable;
    }
    
    @Override
    public Frame<Value> init(Frame<? extends Value> frame) {
      super.init(frame);
      SSAFrame ssaframe = (SSAFrame) frame;
      frames = ssaframe.frames;
      if (ssaframe.parent != null) {
        if (parent != null)
          parent.init(ssaframe.parent);
        else
          parent = new SSAFrame(ssaframe.parent);
      } 
      global = ssaframe.global;
      jump = null;
      unreachable = ssaframe.unreachable;
      return this;
    }
    
    @Override
    public void initJumpTarget(int opcode, LabelNode target) {
      //assert IntStream.range(0, getLocals()).allMatch(var -> getLocal(var).var >= 0);
      //assert IntStream.range(0, getLocals()).allMatch(var -> getLocal(var).mergeIndex >= 0);

//      if (target == null) {
//        for (int var = 0, end = getLocals(); var != end; var++)
//          setLocal(var, getLocal(var).mkContinue());
//      } else {
//        for (int var = 0, end = getLocals(); var != end; var++)
//          setLocal(var, getLocal(var).mkJump(target));
//      }
      
      if (jump != null) {
        unreachable = jump == (target == null);
      } else {
        unreachable = false;
      }
    }
    
    private int pos = -1;
    int pos() {
      if (pos >= 0)
        return pos;
      
      for (pos = 0; pos < frames.length; pos++) {
        if (frames[pos] == this)
          break;
      }
      
      if (pos == frames.length)
        pos = -1;
      
      return pos;
    }
    
    @Override
    public String toString() {
      return (unreachable ? "emp" : super.toString());
    }
    
    boolean reaches(Value src, Set<Object> good, Set<Object> bad) {
      if (src instanceof Object) {
        if (good.contains(src)) {
          return true;
        } else if (bad.contains(src)) {
          return false;
        } else {
          boolean reached = false;
          for (Value child : ((Object)src).fields.values()) {
            reached = reached || reaches(child, good, bad);
          }
          
          if (reached)
            good.add((Object) src);
          else
            bad.add((Object) src);
          
          return reached;
        }
      } else {
        return false;
      }
    }
    
    Object root(Object dst) {
      Object root = dst;
      
      Set<Object> bad = new HashSet<>();

      SSAFrame frame = this;
      while (frame != null) {
        for (int i = 0; i < frame.getLocals(); i++) {
          if (frame.getLocal(i) instanceof Object) {
            Set<Object> good = new HashSet<>(Set.of(root));
            Object local = (Object) frame.getLocal(i);
            if (reaches(local, good, bad))
              root = local;
          }
        }
        for (int i = 0; i < frame.getStackSize(); i++) {
          if (frame.getStack(i) instanceof Object) {
            Set<Object> good = new HashSet<>(Set.of(root));
            Object local = (Object) frame.getStack(i);
            if (reaches(local, good, bad))
              root = local;
          }
        }
        frame = frame.parent;
      }
      
      return root;
    } 
    
    static Set<Object> allChildredObject(Object obj) {
      Set<Object> result = new HashSet<>();
      for (Value val : obj.fields.values()) {
        if (val instanceof Object) {
          result.add((Object) val);
          result.addAll(allChildredObject((Object) val));
        }
      }
      return result;
    }
    
    Set<Object> allObjects() {
      Set<Object> result = new HashSet<>();
      for (int i = 0; i < getLocals(); i++) {
        if (getLocal(i) instanceof Object) {
          result.add((Object) getLocal(i));
          result.addAll(allChildredObject((Object) getLocal(i)));
        }
      }
      for (int i = 0; i < getStackSize(); i++) {
        if (getStack(i) instanceof Object) {
          result.add((Object) getStack(i));
          result.addAll(allChildredObject((Object) getStack(i)));
        }
      }
      if (parent != null)
        result.addAll(parent.allObjects());
      return result;
    }
    
    Object putField(int index, Object root, Object node, String field, Value value) {
      Object result = null;
      boolean changed = false;
      
      if (root == node) {
        result = new Object(index, node.type);
        for (Map.Entry<String, Value> entry : root.fields.entrySet()) {
          if (entry.getKey().equals(field)) 
            result.fields.put(field, value);
          else
            result.fields.put(entry.getKey(), entry.getValue());
        }
        assert result.fields.keySet().equals(root.fields.keySet());
        changed = true;
      } else {
        Map<String, Value> children = new HashMap<>();
        for (Map.Entry<String, Value> entry : root.fields.entrySet()) {
          if (entry.getValue() instanceof Object) {
            Object child = putField(index, (Object) entry.getValue(), node, field, value);
            if (child != entry.getValue())
              changed = true;
            children.put(entry.getKey(), child);
          } else {
            children.put(entry.getKey(), entry.getValue());
          }
        }
        assert children.keySet().equals(root.fields.keySet());
        
        if (changed) {
          result = new Object(index, root.type);
          result.fields = children;
        }
      }
      
      SSAFrame frame = this;
      while (changed && frame != null) {
        for (int i = 0; i < frame.getLocals(); i++) {
          if (root == frame.getLocal(i))
            frame.setLocal(i, result);
        }
        
        for (int i = 0; i < frame.getStackSize(); i++) {
          if (root == frame.getStack(i))
            frame.setStack(i, result);
        }
        
        frame = frame.parent;
      }
      
      assert changed == (result != null);
      
      return changed ? result : root;
    }
    
//    Value reindexAndPutField(int index, Value root, Value leaf, Value value) {
//      Value result = null;
//
//      if (root instanceof Object) { 
//        Object oroot = (Object) (root == leaf ? value : root);
//        Object oresult = new Object(index, oroot.type);
//        for (Map.Entry<String, Value> entry : oroot.fields.entrySet()) {
//          Value child = reindexAndPutField(index, entry.getValue(), leaf, value);
//          if (child instanceof Object) {
//            assert !(entry.getValue() instanceof Object) || ((Object)entry.getValue()).parent == root;
//            Object newChild = (Object) child;
//            newChild.parent = oresult;
//            newChild.name = entry.getKey();
//            oresult.fields.put(entry.getKey(), newChild);
//          } else {
//            oresult.fields.put(entry.getKey(), child);
//          }
//        }
//        result = oresult;
//      } else if (value instanceof Object){
//        if (root == leaf) {
//          Object obj = new Object(index, ((Object) value).type);
//          for (Map.Entry<String, Value> entry : ((Object) value).fields.entrySet()) {
//            Value val = reindexAndPutField(index, entry.getValue(), leaf, value);
//            if (val instanceof Object) {
//              Object oval = (Object) val;
//              oval.parent = obj;
//              oval.name = entry.getKey();
//            }
//            obj.fields.put(entry.getKey(), val);
//          }
//          result = obj;
//        } else {
//          result = root;
//        }
//      } else {
//        result = (root == leaf ? value : root).reindex(index);
//      }
//      
//      SSAFrame frame = this;
//      while (frame != null) {
//        for (int i = 0; i < frame.getLocals(); i++) {
//          if ((root == leaf ? value : root) == frame.getLocal(i))
//            frame.setLocal(i, result);
//        }
//        
//        for (int i = 0; i < frame.getStackSize(); i++) {
//          if ((root == leaf ? value : root) == frame.getStack(i))
//            frame.setStack(i, result);
//        }
//        
//        frame = frame.parent;
//      }
//      
//      return result;
//    }

    void initClass(int index, String className, SSAInterpreter interpreter) throws ClassNotFoundException, AnalyzerException {
      Map<String, Map<String, Value>> global = new HashMap<>(getGlobal());
      assert !getGlobal().containsKey(className);

      ClassNode n = interpreter.reader.getClassNode(className);
      HashMap<String, Value> fields = new HashMap<>();
      for (FieldNode f : n.fields) {
        if ((f.access & Opcodes.ACC_STATIC) != 0) {
          if (f.value == null)
            fields.put(f.name, interpreter.mkDefault(index, f.desc));
          else if (f.value instanceof Long) 
            fields.put(f.name, interpreter.mkConst(((Long) f.value).intValue(), index));
          else if (f.value instanceof Integer) 
            fields.put(f.name, interpreter.mkConst(((Integer) f.value).intValue(), index));
          else if (f.value instanceof String) 
            fields.put(f.name, new MyString(index, (String) f.value));
          else assert false;
        }
      }
      global.put(className, fields);
      
      MethodNode method = n.methods.stream()
          .filter(m -> m.name.equals("<clinit>")).findFirst().orElse(null);
      
      if (method != null) {
        SSAInterpreter initInterpreter = new SSAInterpreter(interpreter.reader, 
            interpreter.ctx, new Value[0], null, interpreter.endOffset, Collections.emptyList());
        SSAEncoder encoder = new SSAEncoder(initInterpreter, initInterpreter.reader, global);

        // LinkedList<String> trace = new LinkedList<>(this.trace);
        // trace.addFirst(owner + "." + name + " " + desc + "\n");

        encoder.analyze(className, method);

        interpreter.encoders.put(interpreter.endOffset, encoder);
        interpreter.endOffset = initInterpreter.endOffset;
        
        List<SSAFrame> outputs = encoder.getReturned();
        assert outputs.size() == 1;
        setGlobal(outputs.iterator().next().global);
      } else {
        setGlobal(global);
      }

    }
    
    Map<String, Map<String, Value>> getGlobal() {
      return global;
    }
    
    void setGlobal(Map<String, Map<String, Value>> global) {
      SSAFrame f = this;
      while(f != null) {
        f.global = global;
        f = f.parent;
      }
    }
    
    @Override
    public void execute(AbstractInsnNode insn, Interpreter<Value> interpreter)
        throws AnalyzerException {
      if (unreachable) 
        return;
      
      assert allObjects().stream().filter(o -> o.type.getInternalName().endsWith("ListItr")).count() <= 1;
      
      SSAInterpreter ssaInterpreter = (SSAInterpreter) interpreter;
      int insnIndex = ssaInterpreter.instructions.indexOf(insn);

      String className = null;
      switch (insn.getOpcode()) {
        case Opcodes.NEW:
          className = ((TypeInsnNode)insn).desc;
          break;
        case Opcodes.GETSTATIC:
        case Opcodes.PUTSTATIC:
          className = ((FieldInsnNode)insn).owner;
          break;
        case Opcodes.INVOKESTATIC:
          className = ((MethodInsnNode)insn).owner;
          break;
      }
      if (className != null && !className.equals("java/lang/Throwable") &&
          !getGlobal().containsKey(className) && !className.equals("java/lang/Integer")) {
        try {
          initClass(insnIndex, className, ssaInterpreter);
        } catch (ClassNotFoundException e) {
          throw new AnalyzerException(insn, e.getMessage(), e);
        }
      }

      switch (insn.getOpcode()) {
        case Opcodes.PUTFIELD:
          ssaInterpreter.setFrame(this);
          super.execute(insn, interpreter);
          
           
          if (ssaInterpreter.putFieldNode == null)
            break;
          Object root = root(ssaInterpreter.putFieldNode);
          
//          reindexAndPutField(insnIndex, root, 
//              ssaInterpreter.putFieldNode.fields.get(ssaInterpreter.putFieldName), 
//              ssaInterpreter.putFieldValue);
          
          putField(insnIndex + ssaInterpreter.beginOffset, root, ssaInterpreter.putFieldNode, ssaInterpreter.putFieldName, 
              ssaInterpreter.putFieldValue);
          
          ssaInterpreter.putFieldNode = null;
          ssaInterpreter.putFieldIndex = -1;
          ssaInterpreter.putFieldName = null;
          
          break;
        case Opcodes.INVOKEVIRTUAL:
        case Opcodes.INVOKESPECIAL:
        case Opcodes.INVOKESTATIC:
        case Opcodes.INVOKEINTERFACE: {
          ssaInterpreter.setFrame(this);
          super.execute(insn, interpreter);

          Value result = null;
          
          if (Type.getReturnType(((MethodInsnNode)insn).desc) != Type.VOID_TYPE) 
            result = pop();

          assert result == null;
          
          assert ssaInterpreter.invokeOutput != null;
          init(ssaInterpreter.invokeOutput.get(0).parent);
          if (ssaInterpreter.invokeOutput.get(0).result != null)
            push(ssaInterpreter.invokeOutput.get(0).result);
          else
            assert Type.getReturnType(((MethodInsnNode)insn).desc) == Type.VOID_TYPE;
          
          Iterator<SSAFrame> it = ssaInterpreter.invokeOutput.listIterator(1);
          while (it.hasNext()) {
            SSAFrame next = it.next();
            Value res = next.result;
            next = next.parent;
            if (res != null)
              next.push(res);
            merge(next, ssaInterpreter);
          }
          ssaInterpreter.invokeOutput = null;
          
          if (Type.getReturnType(((MethodInsnNode)insn).desc) != Type.VOID_TYPE) {
            result = pop();
            assert result != null;
          }
          
          if (ssaInterpreter.callEncoder[insnIndex] != null) {
            ssaInterpreter.callInput[insnIndex] = ssaInterpreter.callEncoder[insnIndex].getInput();
            ssaInterpreter.callOutput[insnIndex] = SSAEncoder.frameToVector(this);
            ssaInterpreter.callResult[insnIndex] = result;
          }

          if (result != null)
            push(result);
          
          break;
        }
        case Opcodes.PUTSTATIC: {
          Map<String, Map<String, Value>> global = new HashMap<>(getGlobal());
          FieldInsnNode fn = (FieldInsnNode) insn;
          assert global.containsKey(fn.owner);
          Map<String, Value> fields = new HashMap<>(global.get(fn.owner));
          assert fields.containsKey(fn.name);
          fields.put(fn.name, pop());
          global.put(className, fields);
          setGlobal(global);
        }
        break;
        case Opcodes.RETURN: {
          SSAFrame out = new SSAFrame(this);
          out.result = null;
          ssaInterpreter.returned[insnIndex] = List.of(out);
          break;
        }
        default:
          ssaInterpreter.setFrame(this);
          super.execute(insn, interpreter);
      }
      
      ssaInterpreter.output[insnIndex] = new SSAFrame(this);
      
      jump = ssaInterpreter.jump;
      ssaInterpreter.jump = null;
    }

    Expr merge(Expr oldValue, Expr newValue, SSAInterpreter interpreter, String prefix) {
//      if (target)
        return interpreter.merge(oldValue, newValue, pos(), prefix);
//      else
//        return interpreter.merge(oldValue, newValue);
    }
    
    Value merge(Value oldValue, Value newValue, SSAInterpreter interpreter, String prefix) {
      assert (oldValue instanceof Expr) == (newValue instanceof Expr) ||
          (oldValue instanceof None && newValue instanceof Expr);
      assert (oldValue instanceof Object) == (newValue instanceof Object);
      assert (oldValue instanceof Array) == (newValue instanceof Array);
   
      if (oldValue instanceof Any) {
        return oldValue;
      } else if (newValue instanceof Any) {
        //assert false;
        return new Any(pos());
      } else if (oldValue instanceof None && newValue instanceof None) {
        return oldValue;
      } else if (oldValue instanceof None && newValue instanceof Expr) {
        return interpreter.merge((None)oldValue, (Expr)newValue, pos(), prefix);
      } else if (oldValue instanceof Expr) {
        return merge((Expr)oldValue, (Expr)newValue, interpreter, prefix);//may have name clash
      } else if (oldValue instanceof Array) {
        Array oldArray = (Array) oldValue, newArray = (Array) newValue;
        assert oldArray.elements.length == newArray.elements.length;
        if (oldArray.equals(newArray)) {
          return oldArray;
        } else {
          Array result = new Array(-1, oldArray.elements.length);
          for (int i = 0; i < result.elements.length; i++) {
            result.elements[i] = merge(oldArray.elements[i], newArray.elements[i], interpreter, prefix);
          }
          return result;
        }
      } else {
        Set<String> keys = ((Object)oldValue).fields.keySet();
        HashMap<String, Value> fields = new HashMap<>();
        boolean changed = false;
        for (String key : keys) {
          Value oldChild = ((Object)oldValue).fields.get(key);
          Value newChild = ((Object)newValue).fields.get(key);
          Value v = merge(oldChild, newChild, interpreter, key);
          fields.put(key, v);
          if (v != oldChild) 
            changed  = true;
        }
        
        if (changed) {
          Object result = new Object(pos(), ((Object)oldValue).type);
          result.fields.putAll(fields);
          
          SSAFrame frame = this;
          while (frame != null) {
            for (int i = 0; i < frame.getLocals(); i++) {
              if (oldValue == frame.getLocal(i))
                frame.setLocal(i, result);
            }
            
            for (int i = 0; i < frame.getStackSize(); i++) {
              if (oldValue == frame.getStack(i))
                frame.setStack(i, result);
            }
            
            frame = frame.parent;
          }
          return result;
        } else {
          return oldValue;
        }
      }
    }
    
    @Override
    public boolean merge(Frame<? extends Value> frame, Interpreter<Value> interpreter)
        throws AnalyzerException {
      return merge((SSAFrame)frame, (SSAInterpreter)interpreter);
    }
    
    public boolean merge(SSAFrame frame, SSAInterpreter interpreter)
        throws AnalyzerException {
      if (unreachable && frame.unreachable) { 
        return false;
      } else if (unreachable && !frame.unreachable) {
        for (int i = 0; i < frame.getLocals(); i++) {
          setLocal(i, frame.getLocal(i));
        }
        for (int i = 0; i < frame.getStackSize(); i++) {
          if (i < getStackSize())
            setStack(i, frame.getStack(i));
          else 
            push(frame.getStack(i));
        }
        unreachable = false;
        jump = null;
        if (parent != null)
          parent.init(frame.parent);
        setGlobal(frame.global);
        return true;
      } else if (!unreachable && frame.unreachable) {
        return false;
      } 
      
      Map<Object, Object> roots = new HashMap<>();

      boolean changed = false;
      assert getLocals() == frame.getLocals();
      for (int i = 0; i < getLocals(); i++) {
        assert (getLocal(i) instanceof Expr) == (frame.getLocal(i) instanceof Expr) ||
            getLocal(i) instanceof None;
        assert (getLocal(i) instanceof Object) == (frame.getLocal(i) instanceof Object);
        if (getLocal(i) instanceof Object) {
          Object oldRoot = root((Object) getLocal(i)), newRoot = frame.root((Object) frame.getLocal(i));
          assert oldRoot.type.equals(newRoot.type);
          assert !roots.containsKey(oldRoot) || newRoot == roots.get(oldRoot);
          roots.put(oldRoot, newRoot);
        } else {
          Value v = merge(getLocal(i), frame.getLocal(i), interpreter, interpreter.varNames[i]);
          if (!v.equals(getLocal(i))) {
            setLocal(i, v);
            changed = true;
          }
        }
      }
      
      assert getStackSize() == frame.getStackSize();
      for (int i = 0; i < getStackSize(); i++) {
        assert (getStack(i) instanceof Expr) == (frame.getStack(i) instanceof Expr);
        assert (getStack(i) instanceof Object) == (frame.getStack(i) instanceof Object);
        if (getStack(i) instanceof Object) {
          Object oldRoot = root((Object) getStack(i)), newRoot = frame.root((Object) frame.getStack(i));
          assert oldRoot.type.equals(newRoot.type);
          assert !roots.containsKey(oldRoot) || newRoot == roots.get(oldRoot);
          roots.put(oldRoot, newRoot);
        } else {
          Value v = merge(getStack(i), frame.getStack(i), interpreter, null);
          if (!v.equals(getStack(i))) {
            setStack(i, v);
            changed = true;
          }
        }
      }
      
      for (Map.Entry<Object, Object> entry : roots.entrySet()) {
        Value res = merge(entry.getKey(), entry.getValue(), interpreter, null);
        if (res != entry.getKey())
          changed = true;
      }
      
      return changed;
    }
    
  };

  static class SSAInterpreter extends Interpreter<Value> {

    final ClassReader reader;
    final Context ctx;
    final Value[] args;
    final SSAFrame input;
    
    SSAFrame current = null;
    
    String[] varNames;
    String[] varDescriptors;
    InsnList instructions = null;

    Expr[] assignmentSymbol;
    IntExpr[] assignmentExpr;

    BoolExpr[] jumpSymbol;
    BoolExpr[] jumpCondition;

    Value[][] callInput, callOutput;
    SSAEncoder[] callEncoder;
    Value[] callResult;
    List<SSAFrame>[] callThrows;
    
    Map<String, Set<String>>[] merges; // debug only
    Map<Integer, SSAEncoder> encoders = new TreeMap<>();
    
    int beginOffset;
    int endOffset;
    
    List<String> trace;
    
    Boolean jump = null;

    List<SSAFrame>[] thrown;
    List<SSAFrame>[] returned;
    
    SSAFrame[] output;
    
    public SSAInterpreter(ClassReader reader, Context ctx, Value[] args, SSAFrame input, 
        int offset, List<String> trace) {
      super(Opcodes.ASM8);
      this.reader = reader;
      this.ctx = ctx;
      this.args = args;
      this.beginOffset = this.endOffset = offset;
      this.input = input;
      this.trace = trace;
    }

    void setFrame(SSAFrame frame) {
      current = frame;
    }

    void init(MethodNode method) {
      varNames = new String[method.maxLocals];
      varDescriptors = new String[method.maxLocals];
      for (LocalVariableNode v : method.localVariables) {
        if (varNames[v.index] == null) {
          varNames[v.index] = v.name;
          varDescriptors[v.index] = v.desc;
        } 
      }
      //method.localVariables.stream().map(n -> n.name).toArray(String[]::new);
      instructions = method.instructions;

      assert StreamSupport.stream(instructions.spliterator(), false).allMatch(insn -> instructions.indexOf(insn) >= 0);

      assignmentSymbol = new Expr[instructions.size()];
      assignmentExpr = new IntExpr[instructions.size()];
      jumpSymbol = new BoolExpr[instructions.size()];
      jumpCondition = new BoolExpr[instructions.size()];
      
      callEncoder = new SSAEncoder[instructions.size()];
      callResult = new Value[instructions.size()];
      callInput = new Value[instructions.size()][];
      callOutput = new Value[instructions.size()][];
      callThrows = new List[instructions.size()];
          
      returned = new List[instructions.size()];
      thrown = new List[instructions.size()];
      output = new SSAFrame[instructions.size()];
      
      endOffset += instructions.size();
      
      merges = new Map[instructions.size()];
    }
    
    void initVarNames(String[] varNames) {
      assert this.varNames == null;
      this.varNames = varNames;
    }
    
    BoolExpr mkJumpSymbol(int insnIndex) {
      return ctx.mkBoolConst("J" + (insnIndex + beginOffset));
    }

    Value mkConst(int val, int insnIndex) {
      return new Expr(insnIndex + beginOffset, ctx.mkInt(val));
    }
    
    Value mkConst(boolean val, int insnIndex) {
      return new Expr(insnIndex + beginOffset, ctx.mkInt(val ? 1 : 0));
    }
    
    void mkBranch(BoolExpr expr, int insnIndex) {
      expr = (BoolExpr) expr.simplify();

      // create symbol
      if (jumpSymbol[insnIndex] == null)
        jumpSymbol[insnIndex] = mkJumpSymbol(insnIndex);

      // update condition
      jumpCondition[insnIndex] = expr;
      
      if (expr.isConst())
        jump = Boolean.valueOf(expr.getBoolValue().toInt() == 1);
      else 
        jump = null;
    }

    void mkAnyBranch(int insnIndex) {
      // create symbol
      if (jumpSymbol[insnIndex] == null)
        jumpSymbol[insnIndex] = mkJumpSymbol(insnIndex);

      // update condition
      jumpCondition[insnIndex] = null;
    }
    
    Expr mkAssignment(String prefix, IntExpr expr, int insnIndex) {
      // create symbol
      if (assignmentSymbol[insnIndex] == null) {
        assignmentSymbol[insnIndex] = (Expr) mkFreshModel(ctx, "I", prefix, "!"+Integer.toString(insnIndex + beginOffset), insnIndex + beginOffset);
      }
      // update assignment
      assignmentExpr[insnIndex] = expr = (IntExpr) expr.simplify();

      if (expr.isIntNum()) {
        return new Expr(insnIndex + beginOffset, expr);
      } else {
        return assignmentSymbol[insnIndex];
      }
    }
    
    Expr mkMerge(Value oldValue, Expr newValue, int insnIndex, String prefix) {
      assert oldValue instanceof Expr || oldValue instanceof None;
      
      // temporary; must find a safer naming system
      if (prefix == null)
        prefix = "expr";
      
      Phi merge = new Phi(insnIndex + beginOffset, ctx.mkIntConst(prefix + "!M" + (insnIndex + beginOffset)), oldValue.creationIndex);
      // store merges here
      if (merges[insnIndex] == null)
        merges[insnIndex] = new HashMap<>();
      assert !merges[insnIndex].containsKey(merge.toString());
      merges[insnIndex].put(merge.toString(), new HashSet<>(Arrays.asList(oldValue.toString(), newValue.toString())));

      return merge;
    }

    @Override
    public Value newEmptyValue(int local) {
      return new None(0);
    }

    @Override
    public Value newParameterValue(boolean isInstanceMethod, int local, Type type) {
      assert type.getSize() == 1;
      assert local < args.length;
      //return mkArg(args[local], local);
      return args[local];
    }

    @Override
    public Value newReturnTypeValue(Type type) {
      //assert false;
      return null;
    }

    @Override
    public Value newValue(Type type) {
      assert false;
      return null;
    }

    @Override
    public Value newExceptionValue(TryCatchBlockNode tryCatchBlockNode, Frame<Value> handlerFrame,
        Type exceptionType) {
      SSAFrame frame = (SSAFrame) handlerFrame;
      assert frame.result != null;
      return frame.result;
    }
    
    Value mkDefault(int creationIndex, String desc) {
      if (desc.length() == 1)
        return mkConst(0, creationIndex + beginOffset);
      else if (desc.startsWith("L"))
        return new None(creationIndex + beginOffset);
      else if (desc.startsWith("["))
        return new None(creationIndex + beginOffset);
      else
        assert false;
      return null;
    }
    
    public MyThrowable mkThrowable(int creationIndex, ClassNode classNode) throws ClassNotFoundException {
      return new MyThrowable(creationIndex, Type.getObjectType(classNode.name));
    }
    
    public Value mkNew(int creationIndex, String name) throws ClassNotFoundException {
      if (name.equals("java.lang.StringBuilder")) {
        return new MyStringBuilder(creationIndex + beginOffset);
      } else if (name.equals("java.lang.Integer")) {
        return new MyPrimitive(creationIndex + beginOffset);
      }
      
      assert !name.contains("/");
      
      ClassNode cn = reader.getClassNode(name);
      
      Class<?> clazz = reader.cl.loadClass(name);
      if (Throwable.class.isAssignableFrom(clazz)) {
        return mkThrowable(creationIndex, cn);
      }
      
      Object obj = new Object(creationIndex + beginOffset, Type.getObjectType(name));

      while (cn != null) {
        for (FieldNode fnode : cn.fields) {
          if ((fnode.access & Opcodes.ACC_STATIC) == 0)
            obj.fields.put(fnode.name, mkDefault(creationIndex, fnode.desc));
        }
        cn = cn.superName != null ? reader.getClassNode(cn.superName) : null;
      }
      return obj;
    }

    @Override
    public Value newOperation(AbstractInsnNode insn) throws AnalyzerException {
      final int insnIndex = instructions.indexOf(insn);
      switch (insn.getOpcode()) {
        case Opcodes.BIPUSH:
          return mkConst(((IntInsnNode) insn).operand, insnIndex);
        case Opcodes.SIPUSH:
          return mkConst(((IntInsnNode) insn).operand, insnIndex);
        case Opcodes.ICONST_0:
          return mkConst(0, insnIndex);
        case Opcodes.ICONST_1:
          return mkConst(1, insnIndex);
        case Opcodes.ICONST_2:
          return mkConst(2, insnIndex);
        case Opcodes.ICONST_3:
          return mkConst(3, insnIndex);
        case Opcodes.ICONST_4:
          return mkConst(4, insnIndex);
        case Opcodes.ICONST_5:
          return mkConst(5, insnIndex);
        case Opcodes.ACONST_NULL:
          return new None(insnIndex);
        case Opcodes.NEW:
          try {
            return mkNew(insnIndex, ((TypeInsnNode)insn).desc.replace("/", "."));
          } catch (ClassNotFoundException e) {
            throw new AnalyzerException(insn, e.getMessage(), e);
          }
        case Opcodes.LDC:
        {
          LdcInsnNode ldcn = (LdcInsnNode) insn;
          if (ldcn.cst instanceof String) 
            return new MyString(insnIndex, (String) ldcn.cst);
          else if (ldcn.cst instanceof Type)
            return new MyClass(insnIndex, (Type) ldcn.cst);
          else if (ldcn.cst instanceof Integer)
            return mkConst((Integer) ldcn.cst, insnIndex);
          else assert false;
        }
          
        case Opcodes.GETSTATIC: {
            FieldInsnNode f = (FieldInsnNode) insn;
            switch (f.owner) {
              case "java/lang/String":
                switch(f.name) {
                  case "COMPACT_STRINGS":
                    return new Expr(insnIndex, ctx.mkInt(1));
                  default:
                    assert false;
                } 
                break;
            default:
              assert current.global.containsKey(f.owner);
              assert current.global.get(f.owner).containsKey(f.name);
              return current.global.get(f.owner).get(f.name);
          }   
        }
        default:
          assert false;
      }
      return null;
    }

    @Override
    public Value copyOperation(AbstractInsnNode insn, Value value)
        throws AnalyzerException {
      final int insnIndex = instructions.indexOf(insn);
      switch (insn.getOpcode()) {
        case Opcodes.ISTORE:
          if (value instanceof MyString)
            return value;
          return mkAssignment(varNames[((VarInsnNode) insn).var], ((Expr)value).expr, insnIndex);
        case Opcodes.ILOAD:
          return value;
        case Opcodes.ALOAD:
          return value;
        case Opcodes.DUP:
          return value;
        case Opcodes.ASTORE:
          return value;
        default:
          assert false;
          return null;
      }
    }
    
    @Override
    public Value unaryOperation(AbstractInsnNode insn, Value value) throws AnalyzerException {
      final int insnIndex = instructions.indexOf(insn);
      
      if (value instanceof Any && insn instanceof JumpInsnNode) {
        mkAnyBranch(insnIndex);
        return null;
      }
      
      switch (insn.getOpcode()) {
        case Opcodes.IINC: {
          IincInsnNode iincInsn = (IincInsnNode) insn;
          return mkAssignment(varNames[iincInsn.var], (IntExpr) ctx.mkAdd(((Expr)value).expr, ctx.mkInt(iincInsn.incr)),
              insnIndex);
        }
        case Opcodes.IFGT:
          mkBranch(ctx.mkGt(((Expr)value).expr, ctx.mkInt(0)), insnIndex);
          return null;
        case Opcodes.IFLT:
          mkBranch(ctx.mkLt(((Expr)value).expr, ctx.mkInt(0)), insnIndex);
          return null;
        case Opcodes.IFGE:
          mkBranch(ctx.mkGe(((Expr)value).expr, ctx.mkInt(0)), insnIndex);
          return null;
        case Opcodes.IFLE:
          mkBranch(ctx.mkLe(((Expr)value).expr, ctx.mkInt(0)), insnIndex);
          return null;
        case Opcodes.IFEQ:
          mkBranch(ctx.mkEq(((Expr)value).expr, ctx.mkInt(0)), insnIndex);
          return null;
        case Opcodes.IFNE:
          mkBranch(ctx.mkNot(ctx.mkEq(((Expr)value).expr, ctx.mkInt(0))), insnIndex);
          return null;
        case Opcodes.INEG:
          return new Expr(insnIndex, (IntExpr) ctx.mkUnaryMinus(((Expr)value).expr));
        case Opcodes.ARRAYLENGTH:
          return mkConst(insnIndex, ((Array)value).elements.length);
        case Opcodes.GETFIELD:
          if (value instanceof Object) {
            assert ((Object)value).fields.containsKey(((FieldInsnNode)insn).name);
            return ((Object)value).fields.get(((FieldInsnNode)insn).name);
          } else if (value instanceof Any) {
            return new Any(insnIndex);
          } else assert false;
          return null;
        case Opcodes.NEWARRAY:
        case Opcodes.ANEWARRAY:
          {
            IntExpr length = ((Expr) value).expr;
            assert length.isIntNum();
            Array array = new Array(insnIndex, Integer.valueOf(length.toString()));
            for (int i = 0; i < array.elements.length; i++) 
              array.elements[i] = mkDefault(insnIndex, ((TypeInsnNode)insn).desc);
            return array;
          }
        case Opcodes.IFNONNULL:
          mkBranch(ctx.mkBool(!(value instanceof None)), insnIndex);
          return null;
        case Opcodes.ATHROW: {
          SSAFrame out = new SSAFrame(current);
          out.result = value;
          assert value instanceof MyThrowable;
          thrown[insnIndex] = List.of(out);
          return null;
        }
        case Opcodes.IRETURN:
        case Opcodes.ARETURN: {
          SSAFrame out = new SSAFrame(current);
          out.result = value;
          returned[insnIndex] = List.of(out);
          return null;
        }
        case Opcodes.CHECKCAST: {
          if (value instanceof Object) {
            String expectedName = Type.getObjectType(((TypeInsnNode)insn).desc).getInternalName().replace("/", ".");
            String actualName = ((Object)value).type.getInternalName();
            try {
              Class<?> expected = reader.cl.loadClass(expectedName);
              Class<?> actual = reader.cl.loadClass(expectedName);
              if (!expected.isAssignableFrom(actual)) {
                // TODO: throw
                assert false;
              } 
              return value;
            } catch (ClassNotFoundException e) {
              throw new AnalyzerException(insn, e.getMessage(), e);
            }
          } else if (value instanceof Any) {
            // TODO: conditional throw
            return value;
          } else if (value instanceof None) {
            return value;
          }
          assert false;
          return null;
        }
        default:
          assert false;
          return null;
      }
    }

    // side effect
    int putFieldIndex;
    Object putFieldNode;
    String putFieldName;
    Value putFieldValue;
    
    void mkPutField(int index, Object object, String field, Value value) {
      assert object.fields.containsKey(field);
      //assert object.fields.get(field) instanceof None || object.fields.get(field) instanceof Expr;
      
      putFieldIndex = index + beginOffset;
      putFieldNode = object;
      putFieldName = field;
      if (value instanceof Expr)
        putFieldValue = mkAssignment(field, ((Expr)value).expr, index);
      else if (value instanceof Object)
        putFieldValue = value;
      else if (value instanceof Array)
        putFieldValue = value;
      else if (value instanceof Any || value instanceof None)
        putFieldValue = value;
      else assert false;
    }
    
    @Override
    public Value binaryOperation(AbstractInsnNode insn, Value value1, Value value2) throws AnalyzerException {
      final int insnIndex = instructions.indexOf(insn);
      
      if (value1 instanceof Any || value2 instanceof Any) {
        if (insn instanceof JumpInsnNode) {
          mkAnyBranch(insnIndex);
          return null;
        } 
      }
      
      switch (insn.getOpcode()) {
        case Opcodes.IF_ICMPLE:
          mkBranch(ctx.mkLe(((Expr)value1).expr, ((Expr)value2).expr), insnIndex);
          return null;
        case Opcodes.IF_ICMPGE:
          mkBranch(ctx.mkGe(((Expr)value1).expr, ((Expr)value2).expr), insnIndex);
          return null;
        case Opcodes.IF_ICMPGT:
          mkBranch(ctx.mkGt(((Expr)value1).expr, ((Expr)value2).expr), insnIndex);
          return null;
        case Opcodes.IF_ICMPLT:
          mkBranch(ctx.mkLt(((Expr)value1).expr, ((Expr)value2).expr), insnIndex);
          return null;
        case Opcodes.IF_ICMPEQ:
          mkBranch(ctx.mkEq(((Expr)value1).expr, ((Expr)value2).expr), insnIndex);
          return null;
        case Opcodes.IF_ICMPNE:
          mkBranch(ctx.mkNot(ctx.mkEq(((Expr)value1).expr, ((Expr)value2).expr)), insnIndex);
          return null;
        case Opcodes.ISUB:
          return new Expr(insnIndex, (IntExpr) ctx.mkSub(((Expr)value1).expr, ((Expr)value2).expr));
        case Opcodes.IADD:
          return new Expr(insnIndex, (IntExpr) ctx.mkAdd(((Expr)value1).expr, ((Expr)value2).expr));
        case Opcodes.IMUL:
          return new Expr(insnIndex, (IntExpr) ctx.mkMul(((Expr)value1).expr, ((Expr)value2).expr));
        case Opcodes.IDIV:
          return new Expr(insnIndex, (IntExpr) ctx.mkDiv(((Expr)value1).expr, ((Expr)value2).expr));
        case Opcodes.IREM:
          return new Expr(insnIndex, (IntExpr) ctx.mkRem(((Expr)value1).expr, ((Expr)value2).expr));
        case Opcodes.IALOAD:
          return new Any(insnIndex);
        case Opcodes.PUTFIELD:
          if (value1 instanceof Any && value2 instanceof Any || 
              value1 instanceof Any && value2 instanceof None)
            return null;
          assert value1 instanceof Object;
          //assert value2 instanceof Expr;
          mkPutField(insnIndex, (Object) value1, ((FieldInsnNode)insn).name, value2);
          return null;
        case Opcodes.ISHL:
          assert value2 instanceof Expr && ((Expr)value2).expr.equals(ctx.mkInt(1));
          return new Expr(insnIndex, (IntExpr) ctx.mkDiv(((Expr)value1).expr, ctx.mkInt(2)));
        case Opcodes.ISHR:
          assert value2 instanceof Expr && ((Expr)value2).expr.isIntNum();
          {
            int i = Integer.valueOf(((Expr)value2).expr.toString());
            IntExpr res = ((Expr)value1).expr;
            while (i != 0) {
              res = (IntExpr) ctx.mkDiv(res, ctx.mkInt(2));
              i--;
            }
            return new Expr(insnIndex, res);
          }
        default:
          assert false;
          return null;
      }
    }

    @Override
    public Value ternaryOperation(AbstractInsnNode insn, Value value1,
        Value value2, Value value3) throws AnalyzerException {
      assert false;
      return null;
    }

    CallNode openCall(List<String> trace, MethodInsnNode insn) {
      CallNode node = RankChecker.calls.get(insn);
      if (node == null) {
        LinkedList<String> newtrace = new LinkedList<String>(trace);
        newtrace.add(insn.name);
        node = new CallNode(newtrace);
        node.offset = endOffset;
        RankChecker.calls.put(insn, node);
        return node;
      } else {
        assert !node.open;
        node.open = true;
        return node;
      }
    }
    
    static Set<String> langCalls = new HashSet<>();

    List<SSAFrame> invokeOutput = null; 

    private SSAEncoder analyzeInvoke(int insnIndex, String owner, MethodNode methodNode,
        List<? extends Value> args, int offset) throws AnalyzerException {

//      if (owner.replace('/', '.').startsWith("java.lang")) {
         langCalls.add(owner + "." + methodNode.name + " " + methodNode.desc + "\n");
//        return null;
//      }

 //     assert args.stream().allMatch(v -> !(v instanceof Object) || ((Object)v).parent == null);
      Value[] inputs = args.stream().toArray(Value[]::new);

      List<String> newtrace = new LinkedList<>(trace);
      newtrace.add(methodNode.name);
      SSAInterpreter interpreter = new SSAInterpreter(reader, ctx, inputs, current, offset, newtrace);
      SSAEncoder analyzer = new SSAEncoder(interpreter, reader);

      // LinkedList<String> trace = new LinkedList<>(this.trace);
      // trace.addFirst(owner + "." + name + " " + desc + "\n");

      assert (methodNode.access & (Opcodes.ACC_NATIVE | Opcodes.ACC_ABSTRACT)) == 0;
      analyzer.analyze(owner, methodNode);
      
      encoders.put(offset, analyzer);
      
      assert invokeOutput == null;
      List<SSAFrame> outputs = analyzer.getReturned();
      invokeOutput = outputs;
      
      callThrows[insnIndex] = analyzer.getThrown().stream().map(f ->
          { SSAFrame g = new SSAFrame(f.parent); 
              g.result = f.result;
              return g;
            }).collect(Collectors.toList());

      return analyzer;
    }

    public Value interpretConst(int insnIndex, MethodInsnNode minsn, List<? extends Value> values) {
      assert minsn.owner.equals("java/lang/String");
      
      if (minsn.name.equals("getBytes")) {
        return null;
      }
      
      String str = (String) ((MyString)values.get(0)).val;
      Type[] argTypes = Type.getArgumentTypes(minsn.desc);
      Class<?>[] argClasses = new Class<?>[argTypes.length];
      java.lang.Object[] args = new java.lang.Object[argTypes.length];
      for (int i = 0; i < argClasses.length; i++) {
        try {
          switch(argTypes[i].getSort()) {
            case Type.OBJECT:
              argClasses[i] = reader.cl.loadClass(argTypes[i].getClassName());
              break;
            case Type.BYTE:
              argClasses[i] = byte.class;
              break;
            case Type.INT:
              argClasses[i] = int.class;
              break;
            case Type.BOOLEAN:
              argClasses[i] = boolean.class;
              break;
            default:
              assert false;
          }
        } catch (ClassNotFoundException e) {
          assert false;
        }
        assert values.get(i) instanceof MyString;
        args[i] = ((MyString)values.get(i)).val;
      }
      
      Class<?> c = str.getClass();
      try {
        Method m = c.getDeclaredMethod(minsn.name, argClasses);
        m.setAccessible(true);
        java.lang.Object res = m.invoke(str, args);
        if (res == null)
          return new None(insnIndex);
        else if (res instanceof Integer) {
          return mkConst((Integer)res, insnIndex);
        } else if (res instanceof Byte) {
          return mkConst((Byte)res, insnIndex);
        } else
          return new MyString(insnIndex, (String) res);
      } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        assert false;
      }
      
      assert false;
      
      return null;
    }
    
    Value interpretStringBuilder(int insnIndex, MethodInsnNode minsn, List<? extends Value> values) {
      assert invokeOutput == null;
      
      MyStringBuilder prev = (MyStringBuilder) values.get(0), next = prev;
      SSAFrame output = new SSAFrame(current, values.size(), 0, null);
      for (int i = 0; i < values.size(); i++) {
        output.setLocal(i, values.get(i));
      }
      
      output.result = null;
      
      switch(minsn.name) {
        case "<init>":
          assert values.size() == 1;
          break;
        case "append":
          assert values.size() == 2;
          if (values.get(1) instanceof MyString) {
            MyString str = (MyString) values.get(1);
            output.result = next = new MyStringBuilder(insnIndex, prev.val.concat(str.val));
          } else if (values.get(1) instanceof Expr) {
            Expr expr = (Expr) values.get(1);
            output.result = next = new MyStringBuilder(insnIndex, prev.val.concat(expr.expr.toString()));
          }
          break;
        case "toString":
          assert values.size() == 1;
          output.result = new MyString(insnIndex, prev.val);
          break;
        default:
          assert false;
      }
      
      SSAFrame frame = output;
      while (frame != null) {
        for (int i = 0; i < frame.getLocals(); i++) {
          if (frame.getLocal(i) == prev) 
            frame.setLocal(i, next);
        }
        for (int i = 0; i < frame.getMaxStackSize(); i++) {
          if (frame.getStack(i) == prev) 
            frame.setStack(i, next);
        }
        frame = frame.parent;
      }
      
      invokeOutput = List.of(output);
      
      return null;
    }
    
    Value interpretClass(int insnIndex, MethodInsnNode minsn, List<? extends Value> values) {
      MyClass clazz = (MyClass) values.get(0);
      SSAFrame output = new SSAFrame(current, values.size(), 0, null);
      for (int i = 0; i < values.size(); i++) {
        output.setLocal(i, values.get(i));
      }
      
      output.result = null;
      
      switch(minsn.name) {
        case "desiredAssertionStatus":
          try {
            Class<?> c = reader.cl.loadClass(clazz.val.getClassName());
            output.result = mkConst(c.desiredAssertionStatus(), insnIndex);
            break;
          } catch (ClassNotFoundException e) {
            assert false; 
          }
        default:
          assert false;
      }
      
      invokeOutput = List.of(output);
      
      return null;
    }
    
    Value interpretThrowable(int insnIndex, MethodInsnNode minsn, List<? extends Value> values) {
      assert invokeOutput == null;
      
      MyThrowable prev = (MyThrowable) values.get(0), next = prev;
      
      SSAFrame output = new SSAFrame(current, values.size(), 0, null);
      for (int i = 0; i < values.size(); i++) {
        output.setLocal(i, values.get(i));
      }
      
      output.result = null;
      
      switch (minsn.name) {
        case "<init>": {
          Type[] argTypes = Type.getArgumentTypes(minsn.desc);
          if (argTypes.length == 0) {
            next = new MyThrowable(insnIndex, prev.type, new MyString(insnIndex, ""), null);
          } else if (argTypes.length == 1 && argTypes[0].getInternalName().equals("java/lang/String")) {
            assert values.size() == 2;
            next = new MyThrowable(insnIndex, prev.type, (MyString) values.get(1), null);
          } else
            assert false;
          
          break;
        }
        default:
          assert false;
      }
      
      invokeOutput = List.of(output);
      SSAFrame frame = output;
      while (frame != null) {
        for (int i = 0; i < frame.getLocals(); i++) {
          if (frame.getLocal(i) == prev) 
            frame.setLocal(i, next);
        }
        for (int i = 0; i < frame.getMaxStackSize(); i++) {
          if (frame.getStack(i) == prev) 
            frame.setStack(i, next);
        }
        frame = frame.parent;
      }
      
      return null;
    }
    
    Value interpretAny(int insnIndex, MethodInsnNode minsn, List<? extends Value> values) {
      assert values.stream().noneMatch(v -> v instanceof Object);

      Type returnType = Type.getReturnType(minsn.desc);
      switch(returnType.getSort()) {
        case Type.INT:
          if (assignmentSymbol[insnIndex] == null)
            assignmentSymbol[insnIndex] = (Expr) mkFreshModel(ctx, "I", minsn.name, Integer.toString(insnIndex + beginOffset), insnIndex + beginOffset);
          break;
        case Type.VOID:
          return null;
        case Type.OBJECT:
        default:
          assert false;
      }
      
      assert assignmentSymbol[insnIndex] != null;
      
      SSAFrame output = new SSAFrame(current, values.size(), 0, null);
      for (int i = 0; i < values.size(); i++) {
        output.setLocal(i, values.get(i));
      }
      
      output.result = assignmentSymbol[insnIndex];
      invokeOutput = List.of(output);
      
      return null;
    }
    
    Value interpretInteger(int insnIndex, MethodInsnNode minsn, List<? extends Value> values) {
      assert invokeOutput == null;
      
      MyPrimitive prev = (MyPrimitive) values.get(0), next = prev;
      
      SSAFrame output = new SSAFrame(current, values.size(), 0, null);
      for (int i = 0; i < values.size(); i++) {
        output.setLocal(i, values.get(i));
      }
      
      output.result = null;
      
      switch (minsn.name) {
        case "<init>": {
          Type[] argTypes = Type.getArgumentTypes(minsn.desc);
          if (argTypes.length == 1 && argTypes[0] == Type.INT_TYPE) {
            assert values.size() == 2;
            next = new MyPrimitive(insnIndex, ((Expr) values.get(1)).expr);
          } else
            assert false;
          
          break;
        }
        default:
          assert false;
      }
      
      invokeOutput = List.of(output);
      SSAFrame frame = output;
      while (frame != null) {
        for (int i = 0; i < frame.getLocals(); i++) {
          if (frame.getLocal(i) == prev) 
            frame.setLocal(i, next);
        }
        for (int i = 0; i < frame.getMaxStackSize(); i++) {
          if (frame.getStack(i) == prev) 
            frame.setStack(i, next);
        }
        frame = frame.parent;
      }
      
      return null;
    }
    
    @Override
    public Value naryOperation(AbstractInsnNode insn, List<? extends Value> values) throws AnalyzerException {
      final int insnIndex = instructions.indexOf(insn);
      assert insn instanceof MethodInsnNode;
      MethodInsnNode minsn = (MethodInsnNode) insn;
      
      if (minsn.owner.equals("java/lang/StringBuilder")) 
        return interpretStringBuilder(insnIndex, minsn, values);
      else if (minsn.owner.equals("java/lang/String"))
        return interpretConst(insnIndex, minsn, values);
      else if (minsn.owner.equals("java/lang/Class"))
        return interpretClass(insnIndex, minsn, values);
//      else if (minsn.owner.equals("java/lang/Integer"))
//        return interpretInteger(insnIndex, minsn, values);
      
      String className = null;
      switch (insn.getOpcode()) {
        case Opcodes.INVOKESTATIC:
        case Opcodes.INVOKESPECIAL: 
          className = minsn.owner;
          break;
        case Opcodes.INVOKEVIRTUAL: 
        case Opcodes.INVOKEINTERFACE:
          assert values.size() > 0;
          if (values.get(0) instanceof Any) {
            return interpretAny(insnIndex, minsn, values);
          }
          assert values.get(0) instanceof Object;
          className = ((Object)values.get(0)).type.getInternalName();
          break;
        default:
          assert false;
      }

      ClassNode classNode;
      MethodNode methodNode;
      try {
        classNode = reader.resolveOwner(className, minsn.name, minsn.desc);
        methodNode = reader.resolveMethodNode(classNode.name, minsn.name, minsn.desc);
      } catch (ClassNotFoundException | NoSuchMethodException e) {
        throw new AnalyzerException(insn, e.getMessage(), e);
      }
    
      {
        Class<?> clazz;
        try {
          clazz = reader.cl.loadClass(className.replace("/", "."));
        } catch (ClassNotFoundException e) {
          throw new AnalyzerException(insn, e.getMessage(), e);
        }
        if (Throwable.class.isAssignableFrom(clazz)) {
          return interpretThrowable(insnIndex, minsn, values);
        } 
      }
      
      CallNode call = openCall(trace, minsn);
      SSAEncoder p = analyzeInvoke(insnIndex, className, methodNode, values, call.offset);
      call.close(p);
      if (p != null && endOffset == call.offset)
        endOffset = p.interpreter.endOffset;

      callEncoder[insnIndex] = p;
      
      List<SSAFrame> results = (p != null) ? p.getReturned() : null;
      assert Type.getReturnType(minsn.desc) == Type.VOID_TYPE || results.size() >= 1;

      return null;
    }

    @Override
    public void returnOperation(AbstractInsnNode insn, Value value, Value expected)
        throws AnalyzerException {
      assert value != null;
      assert expected == null;
    }

    public Expr merge(Value oldValue, Expr newValue, int pos, String prefix) {
      // extend an existing merge and stop
      if (oldValue instanceof Phi && oldValue.creationIndex == pos + beginOffset) { 
//        assert label != null;
//        assert instructions.indexOf(label) == oldValue.creationIndex;
        merges[oldValue.creationIndex - beginOffset].get(oldValue.toString()).add(newValue.toString());
        return (Expr)oldValue;
      }
      
      // propagate fresh constants forward
      if (oldValue.creationIndex == newValue.creationIndex)
        return newValue;
      
      // propagate fresh merges forward
      if (newValue instanceof Phi && oldValue.creationIndex == ((Phi) newValue).oldIndex)
        return newValue;
      
      // create fresh merge
      return mkMerge(oldValue, newValue, pos, prefix);
    }
    
    public Expr merge(Expr oldValue, Expr newValue) {
      assert oldValue != null && newValue != null;
      assert oldValue.creationIndex == newValue.creationIndex ||
          newValue instanceof Phi && oldValue.creationIndex == ((Phi) newValue).oldIndex;

      return newValue;
    }

    public Array merge(Array oldValue, Array newValue) {
      assert false;
      return null;
      //return new Array(oldValue.creationIndex, merge(oldValue.length, newValue.length));
    }
    
    @Override
    public Value merge(Value oldValue, Value newValue) {
      assert oldValue != null && newValue != null;
      assert (oldValue instanceof Expr) == (newValue instanceof Expr);
      assert (oldValue instanceof Object) == (newValue instanceof Object);
      assert (oldValue instanceof Array) == (newValue instanceof Array);
      assert (oldValue instanceof Any) == (newValue instanceof Any);
      assert (oldValue instanceof None) == (newValue instanceof None);
      assert false;
      
      if (oldValue instanceof Expr) {
        return merge((Expr)oldValue, (Expr) newValue);
      } else if (oldValue instanceof Object) {
        return merge((Object)oldValue, (Object) newValue);
      } else if (oldValue instanceof Array){
        return merge((Array)oldValue, (Array) newValue);
      } else if (oldValue instanceof Any) {
        return oldValue;
      } else {
        assert false;
        return null;
      }
    }

  };

  static class Jump {
    BoolExpr condition;
    Value[] output;
    int dst;

    public Jump(BoolExpr condition, Value[] output, int dst) {
      this.condition = condition;
      this.output = output;
      this.dst = dst;
    }

    @Override
    public String toString() {
      return "jump " + dst + " : " + condition + " -> " + Arrays.toString(output);
    }
  }
  
  static class Call {
    int offset;
    Value[] input, output;
    Value result;
    List<Throw> exceptions;
    
    public Call(int offset, Value[] input, Value[] output, Value result, List<Throw> exceptions) {
      this.offset = offset;
      this.input = input;
      this.output = output;
      this.result = result;
      this.exceptions = exceptions;
    }
    
    @Override
    public String toString() {
      return "call " + offset + " : " + Arrays.toString(input) + 
          " -> " + Arrays.toString(output) + ", " + result + 
          " throws " + exceptions.stream().map(e -> e.exception).collect(Collectors.toList());
    }
  }
  
  static class Return {
    BoolExpr condition;
    Value[] output;
    Value result;
    
    public Return(BoolExpr condition, Value[] output, Value result) {
      this.condition = condition;
      this.output = output;
      this.result = result;
    }

    @Override
    public String toString() {
      return "return " + condition + " -> " + Arrays.toString(output) + ", " + result;
    }
  }
  
  static class Throw {
    BoolExpr condition;
    Value[] output;
    MyThrowable exception;
    
    public Throw(BoolExpr condition, Value[] output, MyThrowable result) {
      this.condition = condition;
      this.output = output;
      this.exception = result;
    }

    @Override
    public String toString() {
      return "throw " + condition + " -> " + Arrays.toString(output) + ", " + exception;
    }
  }
  
  static class Block {
    String title;
    Value[] input;
    LinkedList<BoolExpr> statements = new LinkedList<>();
    LinkedList<Jump> jumps = new LinkedList<>();
    LinkedList<Call> calls = new LinkedList<>();
    LinkedList<Return> returned = new LinkedList<>();
    LinkedList<Throw> thrown = new LinkedList<>();
    Map<Throw, Integer> catches = new HashMap<>();
    
    public Block(String title, Value[] input) {
      this.title = title;
      this.input = input;
    }
    
    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(title + "\n" + Arrays.toString(input) + "\n");
      statements.forEach(a -> sb.append("  " + a + "\n"));
      calls.forEach(a -> sb.append("  " + a + "\n"));
      jumps.forEach(a -> sb.append("  " + a + "\n"));
      returned.forEach(a -> sb.append("  " + a + "\n"));
      thrown.forEach(a -> sb.append("  " + a + "\n"));
      catches.forEach((a,b) -> sb.append("  catch " + a + "->" + b + "\n"));
      return sb.toString();
    }
  }

  static class Procedure {
    Value[] args;
    ArrayList<Block> controlFlow;
    
    int [] insnIndexToBlock;
    String[] varNames;
    String[] varDescriptors;

    
    public Procedure(Value[] args, ArrayList<Block> controlFlow, int [] insnIndexToBlock) {
      this.args = args;
      this.controlFlow = controlFlow;
      this.insnIndexToBlock = insnIndexToBlock;
    }
    
    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < controlFlow.size(); i++) {
        sb.append(i + ": " + controlFlow.get(i) + "\n");
      }
      return sb.toString();
    }
  }
  
  static class Program {
    Call main;
    Map<Call, Procedure> linkage;
    
    public Program(Call main, Map<Call, Procedure> linkage) {
      this.main = main;
      this.linkage = linkage;
    }
  }
  
  static class SSAEncoder extends Analyzer<Value> {
    
    final SSAInterpreter interpreter;
    final Context ctx;
    final Map<String, Map<String, Value>> initGlobal;
    
    InsnList instructions = null;
    
    boolean[] breakPoint;
    boolean[] continues;
    int[] jumpTarget;

    static Set<Integer> havocIndices = new HashSet<>();
    
    MethodNode node;
    ClassReader reader;
    String owner;
    
    public SSAEncoder(SSAInterpreter interpreter, ClassReader reader) {
      super(interpreter);
      this.interpreter = interpreter;
      this.ctx = interpreter.ctx;
      this.reader = reader;
      this.initGlobal = Collections.emptyMap();
    }

    public SSAEncoder(SSAInterpreter interpreter, ClassReader reader, Map<String, Map<String, Value>> initGlobal) {
      super(interpreter);
      this.interpreter = interpreter;
      this.ctx = interpreter.ctx;
      this.reader = reader;
      this.initGlobal = initGlobal;
    }
    
    @Override
    protected Frame<Value> newFrame(int numLocals, int numStack) {
      if (interpreter.input != null)
        return new SSAFrame(interpreter.input, numLocals, numStack, getFrames());
      else
        return new SSAFrame(initGlobal, numLocals, numStack, getFrames());
    }

    @Override
    protected Frame<Value> newFrame(Frame<? extends Value> _prev) {
      if (caught != null) {
        SSAFrame res = caught;
        caught = null;
        return res;
      }
      assert _prev instanceof SSAFrame;
      SSAFrame prev = (SSAFrame) _prev;
      SSAFrame result = new SSAFrame(prev);
      return result;
    }

    TryCatchBlockNode getTightest(List<TryCatchBlockNode> handlers, Type exception) {
      try {
        Class<?> clazz = reader.cl.loadClass(exception.getInternalName().replace("/", "."));
        TryCatchBlockNode tightest = null;
        Class<?> tightestClass = null;
        for (TryCatchBlockNode handler : handlers) {
          Class<?> catchClass = handler.type != null ? reader.cl.loadClass(handler.type.replace("/", ".")) : Throwable.class;
          if (catchClass.isAssignableFrom(clazz)) {
            if (tightestClass != null) {
              if (tightestClass.isAssignableFrom(catchClass)) {
                tightestClass = catchClass;
                tightest = handler;
              }
            } else {
              tightestClass = catchClass;
              tightest = handler;
            }
          }
        }
        return tightest;
      } catch (ClassNotFoundException e) {
        assert false;
        return null;
      }
    }
    
    SSAFrame caught = null;
    
    @Override
    protected boolean newControlFlowExceptionEdge(int insnIndex, TryCatchBlockNode tryCatchBlock) {
      List<SSAFrame> thrown = interpreter.thrown[insnIndex];
      if (thrown != null) {
        List<SSAFrame> caught = new LinkedList<>();
        for (SSAFrame frame : thrown) {
          assert frame.result != null;
          MyThrowable exception = (MyThrowable) frame.result;
          TryCatchBlockNode tightest = getTightest(getHandlers(insnIndex), exception.type);
          if (tightest == tryCatchBlock) {
            caught.add(frame);
          }
        }
        assert caught.size() <= 1;
        if (caught.isEmpty()) {
          return false;
        } else {
          this.caught = caught.get(0); 
          return true;
        }
      } else {
        return false;
      }
    }
    
    @Override
    protected void init(String owner, MethodNode method) throws AnalyzerException {
      instructions = method.instructions;
      interpreter.init(method);
      this.node = method;
      this.owner = owner;
      
      breakPoint = new boolean[instructions.size()];
      continues = new boolean[instructions.size()];
      jumpTarget = new int[instructions.size()];
      
      Arrays.fill(breakPoint, false);
      Arrays.fill(continues, false);
      Arrays.fill(jumpTarget, -1);
     
//      Frame<Value>[] frames = getFrames();
//      for (int i = 0; i < instructions.size(); i++) {
//        if (havocIndices.contains(i)) {
//          Frame<Value> f = frames[i] = newFrame(method.maxLocals, method.maxStack);
//          for (int var = 0, end = method.maxLocals; var != end; var++)
//            f.setLocal(var, mkEmpty(var, i));
//        }
//      }
    }

    @Override
    protected void newControlFlowEdge(int insnIndex, int successorIndex) {
      if (successorIndex == insnIndex + 1) {
        continues[insnIndex] = true;
      } else {
        jumpTarget[insnIndex] = successorIndex;
        if (successorIndex > 0)
          breakPoint[successorIndex - 1] = true;
        breakPoint[insnIndex] = true;
      }
    }

    Value[] mkInsnInput(int insnIndex) {
//      Frame<Value> frame = getFrames()[insnIndex];
//      Value[] input = new Value[frame.getLocals() + frame.getStackSize()];
//      int i = 0;
//      for (int j = 0; j < frame.getLocals(); i++, j++) {
//        input[i] = frame.getLocal(j);
//      }
//      
//      return input;
      return frameToVector((SSAFrame) getFrames()[insnIndex]);
    }

    static void addLeafs(List<Value> l, Value v) {
      if (v instanceof Object) {
        for (Value u : ((Object)v).fields.values()) {
          addLeafs(l, u);
        }
      } else {
        l.add(v);
      }
    }
    
    static Value[] frameToVector(SSAFrame frame) {
      Stack<SSAFrame> frames = new Stack<>();
      frames.push(frame);
      while (frame.parent != null) {
        frame = frame.parent;
        frames.push(frame);
      }
      
//      LinkedList<Value> leafs = new LinkedList<>();
//      while (!frames.isEmpty()) {
//        frame = frames.pop();
//        for (int var = 0; var < frame.getLocals(); var++) {
//          addLeafs(leafs, frame.getLocal(var));
//        }
//        for (int var = 0; var < frame.getStackSize(); var++) {
//          addLeafs(leafs, frame.getStack(var));
//        }
//      }
//      return leafs.toArray(new Value[leafs.size()]);

      LinkedList<Value> vals = new LinkedList<>();
      while (!frames.isEmpty()) {
        frame = frames.pop();
        for (int var = 0; var < frame.getLocals(); var++) {
          vals.add(frame.getLocal(var));
        }
        for (int var = 0; var < frame.getStackSize(); var++) {
          vals.add(frame.getStack(var));
        }
      }
      return vals.toArray(new Value[vals.size()]);
    }
    
    Value[] mkInsnOutput(int insnIndex) {
//      Value[] output = mkInsnInput(insnIndex);
//      if (interpreter.assignmentVar[insnIndex] >= 0)
//        output[interpreter.assignmentVar[insnIndex]] = interpreter.assignmentSymbol[insnIndex];
//      
      return frameToVector(interpreter.output[insnIndex]);
    }

//    Call mkCall(int insnIndex) {
//      MethodInsnNode callNode = (MethodInsnNode) instructions.get(insnIndex);
//      return new Call(callNode.owner, callNode.name, callNode.desc, interpreter.callArgs[insnIndex], interpreter.callResult[insnIndex]);
//    }
    
    boolean mayBeReachable(int index) {
      return !((SSAFrame)getFrames()[index]).unreachable;
    }
    
    Block mkBasicBlock(int entryIndex, int exitIndex, int[] insnToBlock) {
      Block b = new Block(this.owner + "." + node.name + ":" + entryIndex, mkInsnInput(entryIndex));
      
      for (int index = entryIndex; index <= exitIndex; index++) {
        assert index == exitIndex || jumpTarget[index] < 0;
        //assert index == exitIndex || interpreter.result[index] == null;
        
        // temporary solution; the interpreter should be responsible of making statements while the encoder just collects them
        if (interpreter.assignmentExpr[index] != null)
          b.statements.add(ctx.mkEq(interpreter.assignmentSymbol[index].expr, interpreter.assignmentExpr[index]));
//        if (interpreter.callArgs[index] != null)
//          b.calls.add(mkCall(index));
        if (interpreter.jumpCondition[index] != null)
          b.statements.add(ctx.mkEq(interpreter.jumpSymbol[index], interpreter.jumpCondition[index]));
      }
      
      Value[] output = mkInsnOutput(exitIndex);

      if (continues[exitIndex] && jumpTarget[exitIndex] >= 0) {
        if (mayBeReachable(exitIndex + 1))
          b.jumps.add(new Jump(ctx.mkNot(interpreter.jumpSymbol[exitIndex]), output, insnToBlock[exitIndex + 1]));
        if (mayBeReachable(jumpTarget[exitIndex]))
          b.jumps.add(new Jump(interpreter.jumpSymbol[exitIndex], output, insnToBlock[jumpTarget[exitIndex]]));
      } else if (continues[exitIndex]) {
        if (mayBeReachable(exitIndex + 1))
          b.jumps.add(new Jump(ctx.mkTrue(), output, insnToBlock[exitIndex + 1]));
      } else if (jumpTarget[exitIndex] >= 0) {
        if (mayBeReachable(jumpTarget[exitIndex]))
          b.jumps.add(new Jump(ctx.mkTrue(), output, insnToBlock[jumpTarget[exitIndex]]));
      } else if (interpreter.returned[exitIndex] != null) {
        assert interpreter.returned[exitIndex].size() == 1;
        b.returned.add(new Return(ctx.mkTrue(), output, interpreter.returned[exitIndex].get(0).result));
      } else if (interpreter.returned[exitIndex - 1] != null) { 
        assert instructions.get(exitIndex).getType() == AbstractInsnNode.LABEL;
        assert interpreter.returned[exitIndex-1].size() == 1;
        b.returned.add(new Return(ctx.mkTrue(), output, interpreter.returned[exitIndex-1].get(0).result));
      } else if (interpreter.thrown[exitIndex] != null){
        for (SSAFrame thrown : interpreter.thrown[exitIndex]) {
          assert thrown.result instanceof MyThrowable;
          b.thrown.add(new Throw(ctx.mkTrue(), output, (MyThrowable) thrown.result));
        }
      } else {
        assert false;
        b.returned.add(new Return(ctx.mkTrue(), output, null)); //void
      }

      return b;
    }

    int[] mkInsnIndexToBlock(int offset) {
      int [] insnToBlock = new int[instructions.size()];
      int blockIndex = offset;
      for (int i = 0; i != instructions.size(); i++) {
        insnToBlock[i] = blockIndex;
        if (breakPoint[i])
          blockIndex++;
      }
      return insnToBlock;
    }
    
    int nBlocks() {
      int n = 1;
      for (int i = 0; i != instructions.size(); i++) {
        if (breakPoint[i])
          n++;
      }
      for (int i = 0; i != instructions.size(); i++) {
        if (interpreter.callEncoder[i] != null)
          n += interpreter.callEncoder[i].nBlocks();
      }
      return n;
    }
    
    void mkProcedure(Block[] cfg, int offset) {
      int [] insnToBlock = mkInsnIndexToBlock(offset);
      
      int blockIndex = offset;
      int entryIndex = 0;
      for (int i = 1; i != instructions.size(); i++) {
        if (breakPoint[i]) {
          if (!((SSAFrame) getFrames()[i]).unreachable) {
            cfg[blockIndex] = mkBasicBlock(entryIndex, i, insnToBlock);
          }
          entryIndex = i + 1;
          blockIndex++;
        }
      }
      int exitIndex = instructions.size() - 1;
      if (getFrames()[exitIndex] == null) exitIndex--;
      cfg[blockIndex++] = mkBasicBlock(entryIndex, exitIndex, insnToBlock);
      
      for (int i = 1; i != instructions.size(); i++) {
        if (interpreter.callEncoder[i] != null) {
          interpreter.callEncoder[i].mkProcedure(cfg, blockIndex);
          List<Throw> exceptions = interpreter.callThrows[i].stream()
              .map(f -> new Throw(ctx.mkTrue(), frameToVector(f), (MyThrowable) f.result)).collect(Collectors.toList());
          cfg[insnToBlock[i]].calls.add(new Call(blockIndex, interpreter.callInput[i], interpreter.callOutput[i], 
              interpreter.callResult[i], exceptions));
          blockIndex += interpreter.callEncoder[i].nBlocks();
        }
      }
    }
    
    Procedure mkProcedure() {
      int [] insnToBlock = mkInsnIndexToBlock(0);
      
      Block[] cfg = new Block[nBlocks()];
      
      mkProcedure(cfg, 0);
      
      return new Procedure(interpreter.args, new ArrayList<>(Arrays.asList(cfg)), insnToBlock);
    }
    
//    Set<Value> getResult() {
//      Set<Value> result = new HashSet<>();
//      for (int i = 0; i < instructions.size(); i++) {
//        if (interpreter.result[i] != null)
//          result.add(interpreter.result[i]);
//      }
//      return result;
//    }
    
//    Set<SSAFrame> getOutput() {
//      Set<SSAFrame> result = new HashSet<>();
//      for (int i = 0; i < instructions.size(); i++) {
//        if (breakPoint[i] && !continues[i] && jumpTarget[i] < 0) {
//          SSAFrame ssaframe = (SSAFrame) getFrames()[i];
//          if (!ssaframe.unreachable)
//            result.add((SSAFrame) getFrames()[i]);
//        }
//      }
//      int exitIndex = instructions.size() - 1;
//      if (getFrames()[exitIndex] == null) exitIndex--;
//      result.add((SSAFrame) getFrames()[exitIndex]);
//      return result;
//    }
    
    List<SSAFrame> getReturned() {
      List<SSAFrame> result = new LinkedList<>();
      for (int i = 0; i < instructions.size(); i++) {
        if (interpreter.returned[i] != null) {
          for (SSAFrame returned : interpreter.returned[i]) {
            result.add(returned);
          }
        }
      }
      return result;
    }
    
    List<SSAFrame> getThrown() {
      List<SSAFrame> result = new LinkedList<>();
      for (int i = 0; i < instructions.size(); i++) {
        if (interpreter.thrown[i] != null) {
          for (SSAFrame thrown : interpreter.thrown[i]) {
            result.add(thrown);
          }
        }
        if (interpreter.callThrows[i] != null) {
          result.addAll(interpreter.callThrows[i]);
        }
        // remove catches here
      }
      return result;
    }
    
    Value[] getInput() {
      return mkInsnInput(0);
    }
    
    @Override
    public String toString() {
      try {
        ClassReader newReader = new ClassReader(reader.cl);
        final MethodNode node = newReader.resolveMethodNode(owner, this.node.name, this.node.desc);

        Textifier textifier = new Textifier(Opcodes.ASM8) {
          Map<Label, Integer> labelToIndex = IntStream.range(0, node.instructions.size()).boxed()
              .filter(i -> node.instructions.get(i).getType() == AbstractInsnNode.LABEL)
              .collect(Collectors.toMap(i -> ((LabelNode) node.instructions.get(i)).getLabel(),
                  Function.identity()));

          @Override
          protected void appendLabel(Label l) {
            if (labelNames == null)
              labelNames = new HashMap<>();
            String name = labelNames.get(l);
            if (name == null) {
              name = "L" + labelToIndex.get(l);
              labelNames.put(l, name);
            }
            super.appendLabel(l);
          }
        };
        TraceMethodVisitor traceMethodVisitor = new TraceMethodVisitor(textifier);
        node.accept(traceMethodVisitor);

        Frame<Value>[] frames = getFrames();

        StringBuilder sb = new StringBuilder();
        sb.append(owner + "." + this.node.name + "\n");
        for (int i = 0; i < node.instructions.size(); ++i) {
          sb.append(i + interpreter.beginOffset + "|" + i);
          String is = insnToStr(textifier, traceMethodVisitor, node.instructions.get(i), newReader);
          sb.append(is);
          sb.append(" ".repeat(Math.max(30 - sb.length(), 1)));
          sb.append(frames[i]);
          sb.append(' ');
          if (interpreter.assignmentSymbol[i] != null) {
            sb.append(interpreter.assignmentSymbol[i] + "=" + interpreter.assignmentExpr[i] + " ");
          }
          if (interpreter.jumpCondition[i] != null) {
            sb.append(interpreter.jumpSymbol[i] + "=" + interpreter.jumpCondition[i]);
          }
          if (interpreter.merges[i] != null)
            interpreter.merges[i].forEach((k, v) -> sb.append(k + "=" + v + " "));
          // if (interpreter.callArgs[i] != null)
          // sb.append(Arrays.toString(interpreter.callArgs[i]));
          // if (interpreter.callResult[i] != null)
          // sb.append(" -> " + interpreter.callResult[i]);
          sb.append("\n");
        }
        for (Map.Entry<Integer, SSAEncoder> entry : interpreter.encoders.entrySet()) {
          sb.append("\n");
          sb.append(entry.getValue().toString());
        }
        return sb.toString();
      } catch (ClassNotFoundException | NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
    }
  };

  static void addEncodeEqIf(Context ctx, Value dst, Value src, BoolExpr condition, List<BoolExpr> list, Set<Value> done) {
    if (done.contains(dst))
      return;
    
    if (dst instanceof Expr && src instanceof Expr)
      list.add(ctx.mkImplies(condition, ctx.mkEq(((Expr)dst).expr, ((Expr)src).expr)));
    else if (dst instanceof Array && src instanceof Array)
      assert false;
      //return ctx.mkImplies(condition, ctx.mkEq(((Array)output).length.expr, ((Array)input).length.expr));
    else if (dst instanceof Any) 
      return;
    else if (src instanceof None)
      return;
    else if (dst instanceof Object && src instanceof Object) {
      Object ooutput = (Object) dst, oinput = (Object) src;
      assert ooutput.type.equals(oinput.type);
      for (Map.Entry<String, Value> e : ooutput.fields.entrySet()) {
        Value lVar = e.getValue(), rVar = oinput.fields.get(e.getKey());
        if (!lVar.equals(rVar)) {
          addEncodeEqIf(ctx, lVar, rVar, condition, list, done);
        }
      }
    } else assert false;
    
    done.add(dst);
  }
  
  static LinkedList<BoolExpr> encodeEqIf(Context ctx, Value[] dst, Value[] src, BoolExpr condition) {
    assert dst.length <= src.length;
    
    HashSet<Value> done = new HashSet<>();
    LinkedList<BoolExpr> stmt = new LinkedList<>();
    for (int var = 0, nVars = dst.length; var < nVars; var++) {
      Value lVar = dst[var];
      Value rVar = src[var];
      if (!lVar.equals(rVar)) {
        addEncodeEqIf(ctx, lVar, rVar, condition, stmt, done);
      }
    }
    
    return stmt;
  }
  
  static List<BoolExpr> encodeEqIf(Context ctx, Value dst, Value src, BoolExpr condition) {
    LinkedList<BoolExpr> stmt = new LinkedList<>();
    HashSet<Value> done = new HashSet<>();
    if (!dst.equals(src))
      addEncodeEqIf(ctx, dst, src, condition, stmt, done);
    return stmt;
  }
  
  static void summarize(Context ctx, List<Block> cfg, List<Integer> sortedBlockIndices, 
      List<BoolExpr> statements, List<Jump> jumps, List<Return> returned, List<Throw> thrown) {
    assert !sortedBlockIndices.isEmpty();
 
    Map<Integer, LinkedList<Jump>> inputs = sortedBlockIndices.stream()
        .collect(Collectors.toMap(Function.identity(), i -> new LinkedList<Jump>()));
        
    int stackBase = cfg.get(sortedBlockIndices.get(0)).input.length;
    
    for (int blockIndex : sortedBlockIndices) {
      Block block = cfg.get(blockIndex);

      ArrayList<BoolExpr> entryDisjuncts = new ArrayList<>();
      while (!inputs.get(blockIndex).isEmpty()) {
        Jump input = inputs.get(blockIndex).removeFirst();
        
        assert input.output.length == block.input.length;
        for (BoolExpr eqIf : encodeEqIf(ctx, block.input, input.output, input.condition))
          statements.add(eqIf);
        
        entryDisjuncts.add(input.condition);
      }
      assert !entryDisjuncts.isEmpty() || blockIndex == sortedBlockIndices.get(0);
      
      // make entry condition
      BoolExpr entryCondition = null;
      switch(entryDisjuncts.size()) {
        case 0:
          //statements.add(ctx.mkEq(entryCondition, ctx.mkTrue()));
          entryCondition = ctx.mkTrue();
          break;
        case 1:
          //statements.add(ctx.mkEq(entryCondition, entryDisjuncts.get(0)));
          entryCondition = entryDisjuncts.get(0);
          break;
        default: {
          BoolExpr bigOr = ctx.mkOr(entryDisjuncts.toArray(BoolExpr[]::new));
          bigOr = (BoolExpr) bigOr.simplify();
          if (bigOr.isTrue())
            entryCondition = bigOr;
          else {
            entryCondition = ctx.mkBoolConst("B" + blockIndex);
            statements.add(ctx.mkEq(entryCondition, bigOr));
          }
        } 
      }
  
      // inherit statements and calls
      statements.addAll(block.statements);
      for (Call call : block.calls) {
        LinkedList<Return> callReturns = new LinkedList<>();
        LinkedList<Throw> callThrown = new LinkedList<>();
        summarize(ctx, cfg, topologicalSorting(cfg, call.offset), statements, Collections.emptyList(), callReturns, callThrown);
        
        for (Return ret : callReturns) {
          statements.addAll(encodeEqIf(ctx, call.output, ret.output, ret.condition));
          if (call.result != null) 
            statements.addAll(encodeEqIf(ctx, call.result, ret.result, ret.condition));
        }
        
        assert block.catches.isEmpty();
        for (Throw thr : callThrown) {
          thrown.add(new Throw(ctx.mkAnd(thr.condition, entryCondition), Arrays.copyOfRange(thr.output, 0, stackBase), thr.exception));          
        }
      }
      
      // push exit conditions
      for (Jump outJump : block.jumps) {
        BoolExpr jumpCondition = ctx.mkAnd(entryCondition, outJump.condition);
        jumpCondition = (BoolExpr) jumpCondition.simplify();
        
        if (inputs.containsKey(outJump.dst))
          inputs.get(outJump.dst).add(new Jump(jumpCondition, outJump.output, outJump.dst));
        else
          jumps.add(new Jump(jumpCondition, outJump.output, outJump.dst));
      }
      
      for (Return ret : block.returned) 
        returned.add(new Return(ctx.mkAnd(ret.condition, entryCondition), ret.output, ret.result));
      
      for (Throw thr : block.thrown)
        thrown.add(new Throw(ctx.mkAnd(thr.condition, entryCondition), thr.output, thr.exception));
    }
    
    assert inputs.values().stream().allMatch(l -> l.stream().allMatch(j -> j.dst == sortedBlockIndices.get(0)));
    
    for (LinkedList<Jump> lasso : inputs.values()) {
      if (!lasso.isEmpty())
        jumps.addAll(lasso);
    }
    
    assert jumps.stream().allMatch(o -> o.output.length == stackBase);
    assert returned.stream().allMatch(o -> o.output.length == stackBase);
    assert thrown.stream().allMatch(o -> o.output.length == stackBase);
  }
  
  static Block summarize(Context ctx, List<Block> cfg, List<Integer> sortedBlockIndices) {
    assert !sortedBlockIndices.isEmpty();
    int src = sortedBlockIndices.get(0);
    
    Block summary = new Block("summary", cfg.get(src).input);
    
    summarize(ctx, cfg, sortedBlockIndices, summary.statements, summary.jumps, summary.returned, summary.thrown);
    
    return summary;
  }

  static Block summarize(Context ctx, List<Block> cfg, int src, int dst, Set<Integer> noDst) {
    return summarize(ctx, cfg, topologicallySortedMaxDAG(cfg, src, dst, noDst));
  }
 
  static Procedure encodeProcedure(Context ctx, ClassReader reader, 
      String owner, String name, String desc, Value[] args) throws ClassNotFoundException, NoSuchMethodException {
    MethodNode methodNode = reader.resolveMethodNode(owner, name, desc);
    
    SSAInterpreter interpreter = new SSAInterpreter(reader, ctx, args, null, 0, List.of(name));
    SSAEncoder encoder = new SSAEncoder(interpreter, reader);
    try {
      encoder.analyze(owner, methodNode);
    } catch (AnalyzerException e) {
      throw new RuntimeException(e);
    }
//    printEncoder(methodNode, encoder, reader);
    
    Procedure proc = encoder.mkProcedure();
    proc.varNames = interpreter.varNames;
    proc.varDescriptors = interpreter.varDescriptors;

    return proc;
  }

  static Value mkFreshModel(Context ctx, String desc, String prefix, String postfix, int creationIndex) {
    switch(desc) {
      case "I":
        return new Expr(creationIndex, ctx.mkIntConst(prefix + postfix));
      case "[I":
        assert false;
        return null;
        //return new Array(creationIndex, new Expr(creationIndex, ctx.mkIntConst(prefix + "length!" + Integer.toString(creationIndex))));
      case "Ljava/util/LinkedList;":
        Object obj = new Object(creationIndex, Type.getType(desc));
        obj.fields.put("size", new Expr(creationIndex, ctx.mkIntConst("size!" + postfix)));
        obj.fields.put("modCount", new Expr(creationIndex, ctx.mkIntConst("modCount!" + postfix)));
        obj.fields.put("first", new Any(creationIndex));
        obj.fields.put("last", new Any(creationIndex));
        return obj;
      case "[Ljava/lang/String;":
        return new None(0);
      default:
        assert false;
        return null;
    }
  }
  
  static Value[] encodeArgs(Context ctx, MethodNode methodNode) {
    Type[] argumentTypes = Type.getArgumentTypes(methodNode.desc);
    Value[] args = new Value[argumentTypes.length]; 
    for (int i = 0; i < args.length; i++) {
      args[i] = mkFreshModel(ctx, argumentTypes[i].getDescriptor(), methodNode.localVariables.get(i).name, "!0", 0);
    }

    return args;
  }
  
  static Procedure encodeProgram(Context ctx, ClassReader reader, String className, String methodName) throws NoSuchMethodException, ClassNotFoundException {

    MethodNode methodNode = reader.resolveMethodNode(className, methodName);
    Value[] args = encodeArgs(ctx, methodNode);

    Procedure procedure = encodeProcedure(ctx, reader, className, methodName, methodNode.desc, args);

    return procedure;
  }
  
  // =========================================================================
  // CFG utils
  // =========================================================================

  static boolean isAcyclic(Block[] cfg, boolean[] view, int entry) {
    assert view.length == cfg.length;

    int[] color = new int[cfg.length];
    Arrays.fill(color, 0);

    Stack<Integer> next = new Stack<>();
    next.push(2 * entry);
    while (!next.isEmpty()) {
      int index = next.pop();
      // if odd mark that the subgraph has been visited (that's color 2)
      if (index % 2 > 0) {
        color[index / 2] = 2;
        continue;
      }
      index = index / 2;
      
      if (color[index] == 2)
        continue;
      if (color[index] == 1)
        return false;

      // mark that the visit has began (that's color 1) 
      color[index] = 1;
      next.push(2 * index + 1);
      for (Jump edge : cfg[index].jumps) {
        if (view[edge.dst])
          next.push(2 * edge.dst);
      }
    }
    
    return true;
  }

  static List<Integer> topologicalSorting(List<Block> cfg, int entry) {
    LinkedList<Integer> sorting = new LinkedList<>();
        
    int[] color = new int[cfg.size()];
    Arrays.fill(color, 0);

    Stack<Integer> next = new Stack<>();
    next.push(2 * entry);
    while (!next.isEmpty()) {
      int indexPlusBit = next.pop();
      int index = indexPlusBit / 2; 
      // if odd mark that the subgraph has been visited (that's color 2)
      if (indexPlusBit % 2 > 0) {
        color[index] = 2;
        sorting.addFirst(index);
        continue;
      }

      if (color[index] == 2)
        continue;
      
      assert color[index] != 1 : "Sorting a cyclic CFG";

      // mark that the visit has began (that's color 1)
      color[index] = 1;
      next.push(2 * index + 1);
      for (Jump edge : cfg.get(index).jumps) {
        next.push(2 * edge.dst);
      }
    }

//    assert sorting.stream().distinct().count() == cfg.length;
    
    return sorting;
  }
  
  static int maxDAG(Block[] cfg, int src, int dst, int[] color) {
    if (color[src] != 0)
      return color[src];
    
    color[src] = 1;
    int endColor = 0;
    for (Jump edge : cfg[src].jumps) {
      int adjColor = edge.dst != dst ? maxDAG(cfg, edge.dst, dst, color) : 2;
      endColor = Math.max(endColor, adjColor);
    }
    
    return color[src] = endColor;
  }
  
  /* 
   * max DAG between src and dst (dst excluded) 
   */
  static boolean[] maxDAG(Block[] cfg, int src, int dst) {
    int[] color = new int[cfg.length];
    Arrays.fill(color, 0);
    maxDAG(cfg, src, dst, color);
    
    boolean[] res = new boolean[cfg.length];
    for (int i = 0; i < cfg.length; ++i)
      res[i] = color[i] > 1 ? true : false;
      
    return res;
  }
  
  static void topologicallySortedMaxDAG(List<Block> cfg, int src, int dst, Set<Integer> noDst, int[] color, LinkedList<Integer> sorting) {
    assert color[src] == 0;
    
    color[src] = 1;
    int endColor = 0;
    for (Jump edge : cfg.get(src).jumps) {
      if (edge.dst == dst) {
        endColor = 2;
      } else if (color[edge.dst] == 0 && !noDst.contains(edge.dst)) {
        topologicallySortedMaxDAG(cfg, edge.dst, dst, noDst, color, sorting);
      }
      endColor = Math.max(endColor, color[edge.dst]);
    }
    
    if (cfg.get(src).jumps.isEmpty() && dst < 0)
      endColor = 2;
    
    if (endColor == 2) 
      sorting.addFirst(src);
    
    color[src] = endColor;
  }
  
  static List<Integer> topologicallySortedMaxDAG(List<Block> cfg, int src, int dst, Set<Integer> noDst) {
    int[] color = new int[cfg.size()];
    Arrays.fill(color, 0);
    LinkedList<Integer> sorting = new LinkedList<>();
    topologicallySortedMaxDAG(cfg, src, dst, noDst, color, sorting);
    assert sorting.stream().distinct().count() == sorting.size();
    return sorting;
  }
  
  // =========================================================================
  // Printer (debug stuff)
  // =========================================================================

  static String insnToStr(Textifier textifier, TraceMethodVisitor traceMethodVisitor, AbstractInsnNode insn, ClassReader reader) {
    textifier.getText().clear();
    insn.accept(traceMethodVisitor);
    StringWriter sw = new StringWriter();
    textifier.print(new PrintWriter(sw));
    if (insn instanceof LabelNode) {
      LabelNode l = (LabelNode) insn;
      sw.append(": " + reader.getOffset(l.getLabel()));
    }
    String str = sw.toString();
    return str.replaceFirst("\n", "");
  }

  static String printEncoder(MethodNode methodNode, SSAEncoder encoder, ClassReader reader) {
    InsnList instructions = encoder.instructions;
    
    Textifier textifier = new Textifier(Opcodes.ASM8) {
      Map<Label, Integer> labelToIndex = IntStream.range(0, instructions.size()).boxed()
          .filter(i -> instructions.get(i).getType() == AbstractInsnNode.LABEL).collect(Collectors
              .toMap(i -> ((LabelNode) instructions.get(i)).getLabel(), Function.identity()));
      
      @Override
      protected void appendLabel(Label l) {
        if (labelNames == null)
          labelNames = new HashMap<>();
        String name = labelNames.get(l);
        if (name == null) {
          name = "L" + labelToIndex.get(l);
          labelNames.put(l, name);
        }
        super.appendLabel(l);
      }
    };
    TraceMethodVisitor traceMethodVisitor = new TraceMethodVisitor(textifier);
    methodNode.accept(traceMethodVisitor);
    
    Frame<Value>[] frames = encoder.getFrames();
    SSAInterpreter interpreter = encoder.interpreter;
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < instructions.size(); ++i) {
      sb.append(i);
      String is = insnToStr(textifier, traceMethodVisitor, instructions.get(i), reader);
      sb.append(is);
      sb.append(" ".repeat(Math.max(30 - sb.length(), 1)));
      sb.append(frames[i]);
      sb.append(' ');
      if (interpreter.assignmentSymbol[i] != null) {
        sb.append(interpreter.assignmentSymbol[i] + "=" + interpreter.assignmentExpr[i] + " ");
      }
      if (interpreter.jumpCondition[i] != null) {
        sb.append(interpreter.jumpSymbol[i] + "=" + interpreter.jumpCondition[i]);
      }
      if (interpreter.merges[i] != null)
        interpreter.merges[i].forEach((k, v) -> sb.append(k + "=" + v + " "));
//      if (interpreter.callArgs[i] != null)
//        sb.append(Arrays.toString(interpreter.callArgs[i]));
//      if (interpreter.callResult[i] != null)
//        sb.append(" -> " + interpreter.callResult[i]);
      sb.append("\n");
    }
    return sb.toString();
  }

  // =========================================================================
  // Main (dirty code down here)
  // =========================================================================

  static ArithExpr linearCombination(Context ctx, List<LocalVariableNode> vars, Iterable<String> args, 
      Iterable<Integer> coefficients, Value[] frame) {
    ArithExpr expr = ctx.mkInt(0);
    Iterator<Integer> i = coefficients.iterator();
    Iterator<String> j = args.iterator();
    assert i.hasNext();
    while (j.hasNext()) {
      assert j.hasNext();
      Integer coeff = i.next();
      String arg = j.next();
      if (coeff != 0) {
        IntExpr var = getField(vars, frame, arg.split("\\."));
        expr = ctx.mkAdd(expr, ctx.mkMul(ctx.mkInt(coeff), var));
      }
    }
    expr = ctx.mkAdd(expr, ctx.mkInt(i.next()));
    
    assert !i.hasNext();
    
    return (ArithExpr) expr.simplify();
  }
  
  static ArithExpr relu(Context ctx, ArithExpr arg) {
    return (ArithExpr) ctx.mkITE(ctx.mkGe(arg, ctx.mkInt(0)), arg, ctx.mkInt(0));
  }
  
  static ArithExpr weightedSumOfRelus(Context ctx, List<LocalVariableNode> vars, Iterable<String> args, 
      List<Integer> out,
      List<List<Integer>> hidden, Value[] frame) {
    assert out.size() == hidden.size();
    
    ArithExpr res = ctx.mkInt(0);
    Iterator<Integer> out_i = out.iterator();
    for (List<Integer> w : hidden) {
      ArithExpr summand = relu(ctx, linearCombination(ctx, vars, args, w, frame));
      res = ctx.mkAdd(res, ctx.mkMul(ctx.mkInt(out_i.next()), summand));
    }
    return res;
  }

  static class CallNode {
    boolean open = true;
    SSAEncoder procedure;
    List<String> trace;
    int offset;
    
    public CallNode(List<String> trace) {
      this.trace = trace;
    }
    
    void close(SSAEncoder procedure) {
      open = false;
      this.procedure = procedure;
    }
  }
  
  static Map<MethodInsnNode, CallNode> calls = new HashMap<>();
  
  public static Boolean[] check(String className, String methodName, 
      Integer offset, List<String> args, List<Integer> coeffs) throws Exception {
    return check(ClassLoader.getSystemClassLoader(), className, methodName, offset, args, coeffs);
  }
  
  static Value merge(Context ctx, List<Value> values, String prefix, String postfix) {

    if (values.get(0) instanceof Expr) {
      assert values.stream().allMatch(v -> v instanceof Expr);
      if (values.stream().allMatch(v -> values.get(0).equals(v)))
        return values.get(0);
      else 
        return mkFreshModel(ctx, "I", prefix, postfix, -1);
    } else if (values.get(0) instanceof Object) {
      Object result = new Object(-1, ((Object)values.get(0)).type);
      for (String key : ((Object)values.get(0)).fields.keySet()) {
        List<Value> next = new LinkedList<>();
        for (Value val : values) 
          next.add(((Object)val).fields.get(key));
        // parent and name missing
        result.fields.put(key, merge(ctx, next, prefix + "." + key, postfix));
      }
      return result;
    } else if (values.get(0) instanceof Array) {
//      List<Value> next = new LinkedList<>();
//      for (Value val : values) 
//        next.add(((Array)val).length);
//      return new Array(-1, (Expr)merge(ctx, next, prefix + ".length"));
      assert false;
      return null;
    } else if (values.stream().anyMatch(v -> v instanceof Any)) {
      return new Any(-1);
    } else if (values.get(0) instanceof None) {
      assert values.stream().allMatch(v -> v instanceof None);
      return new None(-1);
    } else {
      assert false;
      return null;
    }
  }
  
  static Value[] mkSummaryVars(Context ctx, List<Value[]> values, String prefix) {
    Value[] result = new Value[values.get(0).length];
    for (int i = 0; i < result.length; i++) {
      LinkedList<Value> valuesi = new LinkedList<>();
      for (Value[] v : values)
        valuesi.add(v[i]);
      result[i] = merge(ctx, valuesi, prefix + i, "");
    }
    return result;
  }
  
  static IntExpr getField(Object val, String[] path, int depth) {
    assert depth < path.length;
    Value child = val.fields.get(path[depth]);
    if (child instanceof Object) {
      return getField((Object) child, path, depth + 1);
    } else {
      assert depth == path.length - 1;
      assert child instanceof Expr;
      return ((Expr) child).expr;
    }
  }
   
  static IntExpr getField(List<LocalVariableNode> vars, Value[] frame, String[] path) {
    LocalVariableNode var = vars.stream().filter(v -> v.name.equals(path[0])).findFirst().orElseThrow();
    if (frame[var.index] instanceof Object) {
      return getField((Object) frame[var.index], path, 1);
    } else {
      assert path.length == 1;
      assert frame[var.index] instanceof Expr;
      return ((Expr)frame[var.index]).expr;
    }
  }
  
  static LinkedList<BoolExpr>[] preconditionAsInvar(Context ctx, Procedure procedure, int head, 
      Block block, Value[] output) {
    LinkedList<BoolExpr> invarBefore = new LinkedList<BoolExpr>();
    LinkedList<BoolExpr> invarAfter = new LinkedList<BoolExpr>(); 
    
    // encode invariants
    if (Arrays.stream(block.input).allMatch(v -> (v instanceof Expr))){
      Block precondition = summarize(ctx, procedure.controlFlow, 0, head, Collections.emptySet());
      if (precondition.jumps.stream().filter(j -> j.dst == head).count() == 1) {
        Jump preJump = precondition.jumps.stream().filter(j -> j.dst == head).findFirst().orElseThrow();
        // com.microsoft.z3.Expr[] pre = Arrays.stream(preJump.output).map(Expr.class::cast).map(e
        // -> e.expr).toArray(com.microsoft.z3.Expr[]::new);

        Set<com.microsoft.z3.Expr> symbols = new HashSet<>();
        Stack<com.microsoft.z3.Expr> expressions = new Stack<>();
        expressions.addAll(precondition.statements);
        while (!expressions.isEmpty()) {
          com.microsoft.z3.Expr e = expressions.pop();
          if (e.getNumArgs() == 0) {
            if (e.isConst())
              symbols.add(e);
          } else {
            expressions.addAll(Arrays.asList(e.getArgs()));
          }
        }
        com.microsoft.z3.Expr[] sym = symbols.toArray(com.microsoft.z3.Expr[]::new);
        com.microsoft.z3.Expr[] inSym = new com.microsoft.z3.Expr[sym.length];
        com.microsoft.z3.Expr[] outSym = new com.microsoft.z3.Expr[sym.length];
        for (int i = 0; i < sym.length; i++) {
          for (int j = 0; j < preJump.output.length; j++) {
            if (preJump.output[j] instanceof Expr
                && ((Expr) preJump.output[j]).expr.equals(sym[i])) {
              inSym[i] = ((Expr) block.input[j]).expr;
              outSym[i] = ((Expr) output[j]).expr;
            }
          }
          if (outSym[i] == null) {
            assert sym[i].isConst();
            if (sym[i].isBool()) {
              inSym[i] = ctx.mkBoolConst("inB" + i);
              outSym[i] = ctx.mkBoolConst("outB" + i);
            } else if (sym[i].isInt()) {
              inSym[i] = ctx.mkIntConst("inI" + i);
              outSym[i] = ctx.mkIntConst("outI" + i);
            }
          }
        }
        for (BoolExpr stmt : precondition.statements) {
          invarBefore.add((BoolExpr) stmt.substitute(sym, inSym));
          invarAfter.add((BoolExpr) stmt.substitute(sym, outSym));
        }
        invarBefore.add((BoolExpr) preJump.condition.substitute(sym, inSym));
        invarAfter.add(ctx.mkNot((BoolExpr) preJump.condition.substitute(sym, outSym)));
      }
    }
    
    return new LinkedList[]{invarBefore, invarAfter};
  }
  
  static LinkedList<LinkedList<BoolExpr>[]> builtInInvars(Context ctx, Value[] input, Value[] output) {
    assert input.length == output.length;
    LinkedList<LinkedList<BoolExpr>[]> res = new LinkedList<>();
    
    //x > 0
    for (int i = 0; i < input.length; ++i){
      LinkedList<BoolExpr> invarBefore = new LinkedList<BoolExpr>();
      LinkedList<BoolExpr> invarAfter = new LinkedList<BoolExpr>(); 
      
      if (input[i] instanceof Expr && output[i] instanceof Expr) {
        invarBefore.add(ctx.mkGt(((Expr)input[i]).expr, ctx.mkInt(0)));
        invarAfter.add(ctx.mkLe(((Expr)output[i]).expr, ctx.mkInt(0)));
      }
      
      res.add(new LinkedList[]{invarBefore, invarAfter});
    }
    
    //x >= 0
    for (int i = 0; i < input.length; ++i){
      LinkedList<BoolExpr> invarBefore = new LinkedList<BoolExpr>();
      LinkedList<BoolExpr> invarAfter = new LinkedList<BoolExpr>(); 
      
      if (input[i] instanceof Expr && output[i] instanceof Expr) {
        invarBefore.add(ctx.mkGe(((Expr)input[i]).expr, ctx.mkInt(0)));
        invarAfter.add(ctx.mkLt(((Expr)output[i]).expr, ctx.mkInt(0)));
      }
      
      res.add(new LinkedList[]{invarBefore, invarAfter});
    }
    
    return res;
  }
  
  class Obligation {
    int head, tail;
    List<String> args;
    
    List<List<Integer>> ranking;
    List<List<Integer>> unaffecting;
  }
  
  private static void encodeAndSummarise(Context ctx, Solver solver, Block block, int dst, Value[] summaryVars) {
    for (BoolExpr stmt : block.statements)
      solver.add(stmt);
    
    LinkedList<BoolExpr> exitDisjuncts = new LinkedList<>();
    for (Jump out : block.jumps) {
      if (out.dst == dst) {
        exitDisjuncts.add(out.condition);
      } 
    }
    
    for (Jump out : block.jumps) {
      if (out.dst == dst) {
        for (BoolExpr eqIf : encodeEqIf(ctx, summaryVars, out.output, out.condition))
          solver.add(eqIf);
      }
    }
    solver.add(ctx.mkOr(exitDisjuncts.toArray(BoolExpr[]::new)));

    for (Throw thr : block.thrown) {
      solver.add(ctx.mkNot(thr.condition));
    }
  }
  
  private static void getCex(Context ctx, Solver solver, Procedure procedure, Block loop, int head_i, int head, Set<Integer> outer, Map<String, Integer> cex) {
    if (head_i == 0){
      Model cexModel = null;
      if (head == 0) {
        cexModel = solver.getModel();
      } else {
        Model model = solver.getModel();
        Solver cexSolver = ctx.mkSolver();
         Block tail = summarize(ctx, procedure.controlFlow, 0, head, outer);
      
      
         Value[] tailOutout = null;
         for (Jump j : tail.jumps) {
           if (j.dst == head)
             tailOutout = j.output;
         }
         assert tailOutout != null;
         assert tailOutout.length == loop.input.length;
      
         for (int i = 0; i < loop.input.length; ++i) {
           if (tailOutout[i] instanceof None) {
          
           } else {
             assert tailOutout[i] instanceof Expr;
             IntExpr val = (IntExpr) model.getConstInterp(((Expr)loop.input[i]).expr);
             if (val != null)
               cexSolver.add(ctx.mkEq(((Expr)tailOutout[i]).expr, val));
           }
         }
         for (BoolExpr stmt : tail.statements)
           cexSolver.add(stmt);
      
         Status status = cexSolver.check();
         if ( status == Status.SATISFIABLE) 
           cexModel = cexSolver.getModel();
      }
      
      if (cexModel != null) {
        for (Value arg : procedure.controlFlow.get(0).input) {
          if (arg instanceof Expr) {
            IntExpr val = (IntExpr) cexModel.getConstInterp(((Expr)arg).expr);
            if (val != null) {
              String str = ((Expr)arg).expr.toString();
              cex.put(str.substring(0, str.indexOf("!")), Integer.valueOf(val.toString()));
            }
          }
        }
      }
    }
  }
  
  private static Boolean[] check(Context ctx, List<LocalVariableNode> vars, Procedure procedure, List<Integer> heads, 
      List<String> args, List<List<Integer>> lexicographicOut, List<List<List<Integer>>> lexicographicHidden, boolean areRelus, Map<String, Integer> cex) throws Exception {
    //assert procedure.varNames.length + 1 == rank.size();
    assert lexicographicOut.size() == heads.size();
    assert lexicographicHidden.size() == heads.size();
    
    Boolean[] result = {false, areRelus, false}; // dec, bnd, invar
    Set<Integer> outer = new HashSet<Integer>(heads);
    for (int head_i = heads.size() - 1; head_i >= 0; head_i--) {
      int head = heads.get(head_i);
      outer.remove(head);
      
      Block loop = summarize(ctx, procedure.controlFlow, head, head, outer);

      Solver solver = ctx.mkSolver();

      LinkedList<Value[]> exitValues = new LinkedList<>();
      for (Jump out : loop.jumps) {
        exitValues.add(out.output);
      }
      Value[] summaryVars = mkSummaryVars(ctx, exitValues, "sum" + head + "var");
      
      encodeAndSummarise(ctx, solver, loop, head, summaryVars);
      
      Status status;
      
      
      LinkedList<LinkedList<BoolExpr>[]> cadidateInvars = new LinkedList<>();
      cadidateInvars.add(preconditionAsInvar(ctx, procedure, head, loop, summaryVars));
      cadidateInvars.addAll(builtInInvars(ctx, loop.input, summaryVars));
      Iterator<LinkedList<BoolExpr>[]> it = cadidateInvars.iterator();
      while (it.hasNext()) {
        LinkedList<BoolExpr>[] candidate = it.next();
        // check precondition
        {
          Block tail = summarize(ctx, procedure.controlFlow, 0, head, Collections.emptySet());
          Solver preSolver = ctx.mkSolver();
          encodeAndSummarise(ctx, preSolver, tail, head, summaryVars);
          
          assert Stream.of(preSolver.getAssertions()).reduce(ctx.mkSolver(), (s, a) -> {
            s.add(a);
            return s;
          }, (s, t) -> s).check() == Status.SATISFIABLE;
          
          for (BoolExpr stmt : candidate[1])
            preSolver.add(stmt); //already negated
          
          status = preSolver.check();
          if (status == Status.SATISFIABLE) {
            it.remove();
            continue;
          }
        }
        // check invariant
        {
          Solver preSolver = ctx.mkSolver();
          encodeAndSummarise(ctx, preSolver, loop, head, summaryVars);
          
          assert Stream.of(preSolver.getAssertions()).reduce(ctx.mkSolver(), (s, a) -> {
            s.add(a);
            return s;
          }, (s, t) -> s).check() == Status.SATISFIABLE;
          
          for (BoolExpr stmt : candidate[0])
            preSolver.add(stmt);
          
          for (BoolExpr stmt : candidate[1])
            preSolver.add(stmt);
          
          status = preSolver.check();
          if (status == Status.SATISFIABLE) {
            it.remove();
            continue;
          }
        }
      }
      
      IntExpr before = (IntExpr) ctx.mkFreshConst("before", ctx.mkIntSort());
      IntExpr after = (IntExpr) ctx.mkFreshConst("after", ctx.mkIntSort());
      IntExpr before2 = (IntExpr) ctx.mkFreshConst("before2", ctx.mkIntSort());
      IntExpr after2 = (IntExpr) ctx.mkFreshConst("after2", ctx.mkIntSort());
      
      // begin lexicgraphic check
      for (int lexi_i = 0; lexi_i <= head_i; lexi_i++) {
        List<Integer> outWeights = lexicographicOut.get(lexi_i);
        List<List<Integer>> hiddenWeights = lexicographicHidden.get(lexi_i);

        solver.push(); // before and after definition
        
        if (areRelus) {
          solver.add(ctx.mkEq(before2, weightedSumOfRelus(ctx, vars, args, outWeights, hiddenWeights, loop.input)));
          solver.add(ctx.mkEq(after2, weightedSumOfRelus(ctx, vars, args, outWeights, hiddenWeights, summaryVars)));
          solver.add(ctx.mkImplies(ctx.mkGe(before2, ctx.mkInt(0)), ctx.mkEq(before, before2)));
          solver.add(ctx.mkImplies(ctx.mkLt(before2, ctx.mkInt(0)), ctx.mkEq(before, ctx.mkInt(0))));
          solver.add(ctx.mkImplies(ctx.mkGe(after2, ctx.mkInt(0)), ctx.mkEq(after, after2)));
          solver.add(ctx.mkImplies(ctx.mkLt(after2, ctx.mkInt(0)), ctx.mkEq(after, ctx.mkInt(0))));
        } else {
          assert outWeights.size() == 1;
          assert outWeights.get(0) == 1;
          assert hiddenWeights.size() == 1;
          solver.add(ctx.mkEq(before, linearCombination(ctx, vars, args, hiddenWeights.get(0), loop.input)));
          solver.add(ctx.mkEq(after, linearCombination(ctx, vars, args, hiddenWeights.get(0), summaryVars)));
        }
        
        solver.push(); // decrease w/0 invar
        
        if (lexi_i == head_i) {
          solver.add(ctx.mkGe(after, before));
        } else {
          solver.add(ctx.mkGt(after, before));
        }
        
        // checking that it decreases w/o invar
        status = solver.check();
        switch (status) {
          case UNSATISFIABLE:
            // System.out.println("Cool, it decreses."); ;
            // return true;
            result[0] = true;
            break;
          case SATISFIABLE:
            // System.out.println("I am afraid that doesn't decrease.");
            result[0] = false;
            break;
          case UNKNOWN:
          default:
            throw new Exception("I couldn't check it. Sorry about that.");
        }

        if (result[0] == false) {
          // checking that it decreases with invar
          for (LinkedList<BoolExpr>[] invar : cadidateInvars) {
            for (BoolExpr stmt : invar[0])
              solver.add(stmt);
          }
          status = solver.check();
          switch (status) {
            case UNSATISFIABLE:
              // System.out.println("Cool, it decreses."); ;
              // return true;
              result[0] = true;
              result[2] = true;
              break;
            case SATISFIABLE:
              // System.out.println("I am afraid that doesn't decrease.");
              // System.out.println(solver.getModel());
            
              getCex(ctx, solver, procedure, loop, head_i, head, outer, cex);
              result[0] = false;
              return result;
            case UNKNOWN:
            default:
              throw new Exception("I couldn't check it. Sorry about that.");
          }
        }
        
        solver.pop(); // decrease w/0 invar
        
        if (areRelus) {
          result[1] = true;
        } else {
          solver.push(); // bounded with invar
          for (LinkedList<BoolExpr>[] invar : cadidateInvars) {
            for (BoolExpr stmt : invar[0])
              solver.add(stmt);
          }
          solver.add(ctx.mkLt(before, ctx.mkInt(0)));
  
          status = solver.check();
          switch (status) {
            case UNSATISFIABLE:
              // System.out.println("It's also well founded. Well ranked!");
              result[1] = true;
              break;
            case SATISFIABLE:
              // System.out.println("I am afraid that's not well founded, though.");
              result[1] = false;
              return result;
            case UNKNOWN:
            default:
              throw new Exception("I couldn't check it. Sorry about that.");
          }
          solver.pop();// bounded with invar
        }
        
        solver.pop(); // before and after definition
      }
      // end lexicgraphic check
      
      for (Block b : procedure.controlFlow) {
        if (b == null)
          continue;
        for (Jump j : b.jumps) {
          if (j.dst == head) {
            j.output = loop.input;
          }
        }
      }

    }
    return result;
  }
  
  public static Boolean[] check(ClassLoader cl, String className, String methodName, 
      List<Integer> heads, List<String> args, List<List<Integer>> out, List<List<List<Integer>>> hidden, boolean areRelus, Map<String, Integer> cex) throws Exception {

    Context ctx = new Context();
    ClassReader reader = new ClassReader(cl);
    
    Procedure proc = encodeProgram(ctx, reader, className, methodName);
    
    MethodNode methodNode = reader.resolveMethodNode(className, methodName);
    
//    if (ranks.values().stream().anyMatch(r -> r.size() != methodNode.maxLocals + 1)) 
//      throw new IllegalArgumentException("Invalid ranking function size"); 

    int[] insnToBlock = proc.insnIndexToBlock;

    // find line number
    List<Integer> headBlocks = new LinkedList<>();
    for (int offset : heads) {
      LabelNode rankLabel = null;
      for (AbstractInsnNode insn : methodNode.instructions) {
        if (!(insn instanceof LabelNode))
          continue;
  
        LabelNode ln = (LabelNode) insn;
  
        if (reader.getOffset(ln.getLabel()).equals(offset)) {
          rankLabel = ln;
          break;
        }
      }
      if (rankLabel == null)
        throw new IllegalArgumentException(offset + " is an invalid offset");
      headBlocks.add(insnToBlock[methodNode.instructions.indexOf(rankLabel)]);
    }

    return check(ctx, methodNode.localVariables, proc, headBlocks, args, out, hidden, areRelus, cex);
  }

  // we add this because pyjnius complains with polymorphism
  public static Boolean[] _check(ClassLoader cl, String className, String methodName, Integer offset, List<String> args, List<Integer> coeffs) throws Exception {
    return check(cl, className, methodName, List.of(offset), args, List.of(List.of(1)), List.of(List.of(coeffs)), false, new HashMap<>());
  }
  
  public static Boolean[] check(ClassLoader cl, String className, String methodName, Integer offset, List<String> args, List<Integer> coeffs) throws Exception {
    return check(cl, className, methodName, List.of(offset), args, List.of(List.of(1)), List.of(List.of(coeffs)), false, new HashMap<>());
  }
  
  public static Boolean[] checkRelu(ClassLoader cl, String className, String methodName, Integer offset, List<String> args, List<List<Integer>> coeffs) throws Exception {
    return check(cl, className, methodName, List.of(offset), args, List.of(Collections.nCopies(coeffs.size(), 1)), List.of(coeffs), true, new HashMap<>());
  }
  
  public static Boolean[] checkRelu(String className, String methodName, Integer offset, List<String> args, List<List<Integer>> coeffs) throws Exception {
    return check(ClassLoader.getSystemClassLoader(), className, methodName, List.of(offset), args, List.of(Collections.nCopies(coeffs.size(), 1)), List.of(coeffs), true, new HashMap<>());
  }
  
  public static Boolean[] checkRelu(String className, String methodName, Integer offset, List<String> args, List<Integer> out, List<List<Integer>> hidden) throws Exception {
    return check(ClassLoader.getSystemClassLoader(), className, methodName, List.of(offset), args, List.of(out), List.of(hidden), true, new HashMap<>());
  }
  
  public static Boolean[] _checkRelu(ClassLoader cl, String className, String methodName, Integer offset, List<String> args, List<List<Integer>> coeffs) throws Exception {
    return check(cl, className, methodName, List.of(offset), args, List.of(Collections.nCopies(coeffs.size(), 1)), List.of(coeffs), true, new HashMap<>());
  }
  
  public static Boolean[] checkLexiRelu(String className, String methodName, List<Integer> heads, List<String> args, List<List<List<Integer>>> coeffs) throws Exception {
    return check(ClassLoader.getSystemClassLoader(), className, methodName, heads, args, 
        coeffs.stream().map(l -> Collections.nCopies(l.size(), 1)).collect(Collectors.toList()), 
        coeffs, true, new HashMap<>());
  }
  
  public static Boolean[] _checkLexiRelu(ClassLoader cl, String className, String methodName, List<Integer> heads, List<String> args, List<List<List<Integer>>> coeffs) throws Exception {
    return check(cl, className, methodName, heads, args, 
        coeffs.stream().map(l -> Collections.nCopies(l.size(), 1)).collect(Collectors.toList()), 
        coeffs, true, new HashMap<>());
  }
  
  public static Boolean[] _checkLexiReluOrCex(ClassLoader cl, String className, String methodName, List<Integer> heads, List<String> args, List<List<List<Integer>>> coeffs, Map<String,Integer> cex) throws Exception {
    return check(cl, className, methodName, heads, args, 
        coeffs.stream().map(l -> Collections.nCopies(l.size(), 1)).collect(Collectors.toList()), 
        coeffs, true, cex);
  }
  
  public static Boolean[] _checkLexiReluOrCex2(ClassLoader cl, String className, String methodName, List<Integer> heads, List<String> args, List<List<Integer>> out, List<List<List<Integer>>> hidden, Map<String,Integer> cex) throws Exception {
    return check(cl, className, methodName, heads, args, out, hidden, true, cex);
  }
}
