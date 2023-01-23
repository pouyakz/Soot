import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import soot.MethodOrMethodContext;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.callgraph.Targets;
import soot.util.dot.DotGraph;
import soot.util.queue.QueueReader;


public class Main {
	
	public static void serializeCallGraph(CallGraph graph, String filename) {
		if (filename == null) {
			filename = soot.SourceLocator.v().getOutputDir();
			if (filename.length() > 0 ) {
				filename = filename + java.io.File.separator;
			}
			filename = filename + "call-graph" + DotGraph.DOT_EXTENSION;
		}
		System.out.println("writing to file " + filename);
		DotGraph canvas = new DotGraph("call-graph");
		QueueReader<Edge> listener = graph.listener();
		while (listener.hasNext()) {
			Edge next = listener.next();
			MethodOrMethodContext src = next.getSrc();
			MethodOrMethodContext tgt = next.getTgt();
			String srcString = src.toString();
			String tgtString = tgt.toString();
			if ( (!srcString.startsWith("<java.") && !srcString.startsWith("<sun.") && !srcString.startsWith("<org.") && !srcString.startsWith("<com.") && !srcString.startsWith("<jdk.") && !srcString.startsWith("<javax."))
				||
				(!tgtString.startsWith("<java.") && !tgtString.startsWith("<sun.") && !tgtString.startsWith("<org.") && !tgtString.startsWith("<com.") && !tgtString.startsWith("<jdk.") && !tgtString.startsWith("<javax."))) {
				canvas.drawNode(src.toString());
				canvas.drawNode(tgt.toString());
				canvas.drawEdge(src.toString(), tgt.toString());
				System.out.println("src = " + srcString);
				System.out.println("tgt = " + tgtString);
			}

		}
		canvas.plot(filename);
		return;
	}
	
	public static void main(String[] args) {
		
        List<String> argsList = new ArrayList<String>(Arrays.asList(args));
        argsList.addAll(Arrays.asList(new String[] { "-w", "-main-class", "testers.CallGraphs", // main-class
                     "testers.CallGraphs", // argument classes
                     "testers.Test1", //
                     "testers.Test2", //
                     "testers.Test3", //

        }));
        String[] args2 = new String[argsList.size()];
        args2 = argsList.toArray(args2);        
        PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", new SceneTransformer() {
               @Override
               protected void internalTransform(String phaseName, Map options) {
                     CHATransformer.v().transform();
                     SootClass a = Scene.v().getSootClass("testers.Test1");
                     SootMethod src = Scene.v().getMainClass().getMethodByName("func");
                     CallGraph cg = Scene.v().getCallGraph();
                     
                     serializeCallGraph(cg, "output" + DotGraph.DOT_EXTENSION);
                     System.out.println("serializeCallGraph completed.");
                     
                     Iterator<MethodOrMethodContext> targets = new Targets(cg.edgesOutOf(src));
                     while (targets.hasNext()) {
                            SootMethod tgt = (SootMethod) targets.next();
                            System.out.println(src + " may call " + tgt);
                     }
               }
        }));
        args = argsList.toArray(new String[0]);
        soot.Main.main(args2);    
	}

}