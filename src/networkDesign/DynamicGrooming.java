package networkDesign;

import java.util.ArrayList;
import java.util.Iterator;

import demand.Request;
import general.Constant;
import graphalgorithms.RouteSearching;
import graphalgorithms.SearchConstraint;
import network.Layer;
import network.Link;
import network.Node;
import network.NodePair;
import network.VirtualLink;
import subgraph.LinearRoute;

public class DynamicGrooming {
	/*
	 * 对于新来的request，首先在IP层路由该请求： 
	 *          1：在IP层的每个链路，查找该链路的虚拟链路中的剩余容量最大的链路
	 *          2：如果该容量大于当前结点对的容量需求，则将对应虚拟链路的cost和length信息复制到链路中去 
	 *          3：否则将该链路排除
	 *          4：在剩余的拓扑上路由该需求 
	 *          5:如果路由成功，更新虚拟链路的容量信息，否则的话，看着办 
	 *          6：否则的话，新建光路
	 * 
	 */
	public void dynamicGrroming(Request request, Layer ipLayer, Layer optLayer) {
		
		NodePair currentnodePair=request.getNodepair();
		Node srcNode=currentnodePair.getSrcNode();
		Node destNode=currentnodePair.getDesNode();
		
		SearchConstraint constraint0=new SearchConstraint();
		Iterator<String> itr0=ipLayer.getLinklist().keySet().iterator();
		while(itr0.hasNext()){
			Link link=(Link) (ipLayer.getLinklist().get(itr0.next()));
			if(link.getVirtualLinkList().size()==0){
				constraint0.getExcludedLinklist().add(link);
			}else{
				this.sortVTLink(link);//查找剩余容量最大的那个给我用，如果不够用就拉到
				if(link.getVirtualLinkList().get(0).getRemanCapacity()>request.getRequestRate()){//if enough remaining
					link.setTempVirtualLink(link.getVirtualLinkList().get(0));
					link.setCost(link.getTempVirtualLink().getCost());
					link.setLength(link.getTempVirtualLink().getLength());
				}else{
					constraint0.getExcludedLinklist().add(link);
				}				
			}			
		}
		
		
		LinearRoute newRoute0=new LinearRoute("IP work route", 0, null);
		RouteSearching rs0=new RouteSearching();
		rs0.Dijkstras(srcNode, destNode, ipLayer, newRoute0, constraint0);
		
		/*
		 * 路由成功的话：
		 *     1：将该路由添加到request的work route中
		 *     2：将用到的虚拟链路也一个一个添加到request中去
		 *     3：更新链路的剩余容量信息
		 */
		if(newRoute0.getLinklist().size()!=0){
			request.setWorkRoute(newRoute0);
			request.setLayer(Constant.IP);//代表该request是在IP层的
			Constant.NUM++;
			/*grooming=true;
//			newRoute0.OutputRoute_node(newRoute0);			
			*/
			
			
			for(Link link:newRoute0.getLinklist()){
				request.getWorkVtLinkList().add(link.getTempVirtualLink());			
				for(VirtualLink vLink:link.getVirtualLinkList()){
					if(vLink.equals(link.getTempVirtualLink())){
						vLink.setRemanCapacity(vLink.getRemanCapacity()-request.getRequestRate());
//						link.setTempVirtualLink(null);
						break;
					}
				}							
			}
		/*	
			//将tempvirtuallink设置为空，以便回收
			Iterator<String> it0=ipLayer.getLinklist().keySet().iterator();
			while(it0.hasNext()){
				Link link=(Link) (ipLayer.getLinklist().get(it0.next()));
				link.setTempVirtualLink(null);
			}*/
			
		}else{
			/*
			 * 如果路由失败的话，进行光层的RSA：
			 *     1：SWP路由成功的话，更新光层频谱利用信息，同时将slot number, start index 以及 work路由信息添加到request 中
			 *     2：step1是在DynamicRSA类的内部完成
			 *     3：新建链路，添加到ip 层以及request的路由，新建虚拟链路，添加到刚才的虚拟链路中，以及request的虚拟链路
			 *     4：step3要注意分析链路是否已经存在，如果已经存在，就不需要新建链路了，只要新建虚拟链路就可以了
			 */
			DynamicRSA dRSA=new DynamicRSA();
			dRSA.SWPbasedRSA(request, optLayer);
			if(request.getWorkRoute().getLinklist().size()!=0){
//				System.out.println("当前需求在光层RSA成功");
				String name=srcNode.getName()+'-'+destNode.getName();
				
				Link existLink=null;
				Iterator<String> itr1=ipLayer.getLinklist().keySet().iterator();
				while(itr1.hasNext()){
					Link link=(Link)(ipLayer.getLinklist().get(itr1.next()));
					if(link.getName().equals(name)){
						existLink=link;
						break;
					}
				}
				
				//这个地方看下说明.txt的12行以后
				if(existLink==null){
					Link newLink=new Link(name, ipLayer.getLinklist().size(), null,ipLayer, srcNode, destNode, 1,1);
					
					
//					new VirtualLink(cost, length, capacity, remanCapacity)nature physiclinklist FSnum startindex
					VirtualLink newVLink=new VirtualLink(name,request.getWorkRoute().getCost(), request.getWorkRoute().getLength(), 
							request.getSlotNum()*request.getModulationCapcity(), request.getSlotNum()*request.getModulationCapcity()-request.getRequestRate());
					newVLink.setNature(Constant.WORK);
					newVLink.setPhyLinkList(request.getWorkRoute().getLinklist());
					newLink.getVirtualLinkList().add(newVLink); 					
					ipLayer.addLink(newLink);
					request.setWorkVLink(newVLink);
				}else{
					VirtualLink newVLink=new VirtualLink(name,request.getWorkRoute().getCost(), request.getWorkRoute().getLength(), 
							request.getSlotNum()*request.getModulationCapcity(), request.getSlotNum()*request.getModulationCapcity()-request.getRequestRate());
					newVLink.setNature(Constant.WORK);
					newVLink.setPhyLinkList(request.getWorkRoute().getLinklist());
					existLink.getVirtualLinkList().add(newVLink);
					request.setWorkVLink(newVLink);
				}				
			}
			
		}
		

	}
	
	
	
	
	//将链路的虚拟链路按照剩余容量大小排序,从大到小
		public void sortVTLink(Link link){
			ArrayList<VirtualLink> vtLinkList=new ArrayList<VirtualLink>();
			if(link.getVirtualLinkList().size()==0){
				System.out.println("\t无法排序，无虚拟链路存在");
			}else{
				for(int i=0;i<link.getVirtualLinkList().size();i++){
					int m=i;
					for(int j=i+1;j<link.getVirtualLinkList().size();j++){

						if(link.getVirtualLinkList().get(m).getRemanCapacity()>link.getVirtualLinkList().get(j).getRemanCapacity()){
							m=j;
						}			
					}
					vtLinkList.add(0,link.getVirtualLinkList().get(m));
				}	
				link.getVirtualLinkList().clear();
				link.setVirtualLinkList(vtLinkList);
			}		
		}
		

}
