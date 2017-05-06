package networkDesign;

import demand.Request;
import general.Constant;
import general.SWP;
import network.Layer;
import network.Link;
import network.NodePair;
import subgraph.LinearRoute;

public class DynamicRSA {
	public void SWPbasedRSA(Request request,Layer optLayer){
		double[] modulationCapacity={37.5,25,12.5};
		int[] transparentLen={1000,2000,4000};
		NodePair nodePair=optLayer.getNodepairlist().get(request.getNodepair().getName());
		LinearRoute newRoute=null;
		int startIndex=0;
		int slotNum=0;
		int j=0;
		Object[] obj=null;
		
		for(int i=0;i<modulationCapacity.length;i++){
			slotNum=(int)(Math.ceil(request.getRequestRate()/modulationCapacity[i]));			
			SWP workSWP=new SWP();
			obj=workSWP.minCost_RSABasedOnSWP_LengthLimited(optLayer, slotNum,nodePair.getSrcNode(),nodePair.getDesNode(), null, transparentLen[i]);
			newRoute=(LinearRoute)obj[0];
			startIndex=(int)obj[1];
			if(newRoute.getLinklist().size()!=0){
				j=i;
				break;
			}
		} 
		
		//如果分配成功，更新链路的频谱占用情况
		if(newRoute.getLinklist().size()!=0){
		
			request.setModulationCapcity(modulationCapacity[j]);
			request.setSlotNum(slotNum);
			request.setStartIndex(startIndex);
			request.setWorkRoute(newRoute);
			request.setLayer(Constant.OPTICAL);
			
			for(Link link:newRoute.getLinklist()){
				for(int i=startIndex;i<startIndex+slotNum;i++){
					link.getSlotsArray().get(i).setStatus(1);
				}
			}
		}
		
	}
	

}
