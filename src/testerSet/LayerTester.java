package testerSet;

import java.util.Iterator;

import network.Layer;
import network.Link;
import network.NodePair;

public class LayerTester {
	public static void main(String[] args) {


		Layer optLayer=new Layer("optical", 0, null);
		optLayer.readTopology("E:\\ÆäËû\\RPtopology\\NODE6.csv");
		optLayer.generateNodepairs();
		Iterator<String> it=optLayer.getNodepairlist().keySet().iterator();
		while(it.hasNext()){
			NodePair nodePair=(NodePair)(optLayer.getNodepairlist().get(it.next()));
			System.out.print(nodePair.getName()+"---------");
			System.out.println(nodePair.getSrcNode().getName());
			
		}
		System.out.println("Link:\n");
		Iterator<String> itr=optLayer.getLinklist().keySet().iterator();
		while(itr.hasNext()){
			Link link=(Link)(optLayer.getLinklist().get(itr.next()));
			System.out.println(link.getName());
		}
	}

}
