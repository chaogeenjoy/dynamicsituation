package demand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import general.Constant;
import network.Layer;
import network.Link;
import network.NodePair;
import network.VirtualLink;
import subgraph.LinearRoute;

/*
 * 说明：
 *     如果layer指出该request是在IP层实现的额，那么workroute protectionroute 存储的是IP层的链路，
 *     而对应的虚拟链路列表里存储了改workroute里每个link使用的是哪条虚拟链路
 *     
 *     如果是在光层的话，work route 存储的是物理链路，不需要虚拟链路列表
 */
public class Request {

	private NodePair nodepair;
	private double bandwidth;
	private int requestRate;
	private int slotNum;
	private int startIndex;
	private LinearRoute workRoute;
	private LinearRoute protectRoute;
	private ArrayList<VirtualLink> workVtLinkList;
	private ArrayList<VirtualLink> protectVtLinkList;
	private VirtualLink workVLink=null;
	private VirtualLink protectVLink=null;
	private double arrivalTime;
	private double departTime;
	private char reqType;
	private String layer=new String();// 记录当前的请求是在光层实现的还是在IP层实现的
	private double modulationCapcity = 0;

	public Request(NodePair nodepair, int requestRate, double arrTime, double departTime, char reqType) {
		this.setNodepair(nodepair);
		this.setRequestRate(requestRate);
		this.setArrivalTime(arrTime);
		this.setDepartTime(departTime);
		this.setReqType(reqType);
		this.workRoute = new LinearRoute("work route", 0, null);
		this.protectRoute = new LinearRoute("protect route", 0, null);
		this.workVtLinkList = new ArrayList<VirtualLink>();
		this.protectVtLinkList = new ArrayList<VirtualLink>();
	}

	public Request() {
		super();
		// TODO Auto-generated constructor stub

	}

	public static ArrayList<NodePair> sortNodePair(Layer iplayer) {
		ArrayList<NodePair> nodePairList = new ArrayList<NodePair>();
		for (int i = 0; i < iplayer.getNodepair_num(); i++) {
			HashMap<String, NodePair> map = iplayer.getNodepairlist();

			Iterator<String> itr = map.keySet().iterator();
			NodePair tempNodePair = new NodePair("", 0, "", null, null, null);
			tempNodePair.setTrafficdemand(10000000);
			while (itr.hasNext()) {
				NodePair nodePair = (NodePair) (map.get(itr.next()));
				if (nodePair.getArrange_status() == Constant.UNORDER) {
					if (nodePair.getTrafficdemand() < tempNodePair.getTrafficdemand()) {
						tempNodePair = nodePair;
					}
				}
			}
			nodePairList.add(0, tempNodePair);
			HashMap<String, NodePair> map1 = iplayer.getNodepairlist();
			Iterator<String> itr1 = map1.keySet().iterator();
			while (itr1.hasNext()) {
				NodePair nodePair1 = (NodePair) (map1.get(itr1.next()));
				if (nodePair1 == tempNodePair) {
					nodePair1.setArrange_status(Constant.ORDERED);
				}
			}

		}
		return nodePairList;
	}

	public static ArrayList<Integer> spectrumAllocationOneRoute_ReqList(LinearRoute route) {
		ArrayList<Link> routelink = route.getLinklist();// 路由中存储的链路列表
		/**
		 * 增强型遍历：
		 */
		if (route.getSlotsnum() == 0) {
			System.out.println("no slots");
		} else {
			for (Link link : routelink) {
				link.getSlotsIndexInOneSW().clear(); //
				for (int r = 0; r <= link.getSlotsArray().size() - route.getSlotsnum(); r++) {
					int s = 1;
					for (int k = r; k < route.getSlotsnum() + r; k++) {
						if (link.getSlotsArray().get(k).getStatus() != 0) {// 只要一个SW中有一个slot被占用，s=0，意味着该SW不能用
							s = 0;
							break;
						}
					}
					if (s != 0) // s!=0意味着SW中的F个slot都是可用的
						link.getSlotsIndexInOneSW().add(r);// 将可用的SW的初始索引添加到Slotsindex()集合中去
				}
			}
		}
		/**
		 * 遍历结束
		 */

		Link link = routelink.get(0);
		ArrayList<Integer> sameindex = new ArrayList<Integer>();
		sameindex.clear();
		/*
		 * 下面这段循环遍历路径链路1中的所有可用的SW的初始索引 将该索引和路由中的其他链路进行查找，判断是否包含该索引，
		 * 
		 * 目的是为了得到其他链路都包含的索引（即可用的SW）
		 */
		for (int i = 0; i < link.getSlotsIndexInOneSW().size(); i++) {
			int index = link.getSlotsIndexInOneSW().get(i);
			int flag = 1;
			for (Link link2 : routelink) {
				if (!link2.getSlotsIndexInOneSW().contains(index)) {
					flag = 0;
					break;
				}
			}
			if (flag != 0) {
				sameindex.add(link.getSlotsIndexInOneSW().get(i));
			}
		}
		return sameindex;
	}

	public ArrayList<Integer> spectrumAllocationOneRoute(LinearRoute route) {
		ArrayList<Link> routelink = route.getLinklist();
		for (Link link : routelink) {
			if (route.getSlotsnum() == 0) {
				System.out.println("noslots");
				break;
			}
			link.getSlotsIndexInOneSW().clear();

			for (int i = 0; i <= link.getSlotsArray().size() - route.getSlotsnum(); i++) {
				if (link.getSlotsArray().get(i) == null) {
					int s = 1;
					for (int k = i; k < route.getSlotsnum() + i; k++) {

						if (link.getSlotsArray().get(k) != null) {
							s = 0;
							break;
						}

					}
					if (s != 0) {
						link.getSlotsIndexInOneSW().add(i);

					}
				}
			}
		}
		Link link = routelink.get(0);
		ArrayList<Integer> sameindex = new ArrayList<Integer>();
		sameindex.clear();
		for (int i = 0; i < link.getSlotsIndexInOneSW().size(); i++) {
			int index = link.getSlotsIndexInOneSW().get(i);
			int flag = 1;
			for (Link link2 : routelink) {
				if (!link2.getSlotsIndexInOneSW().contains(index)) {
					flag = 0;
					break;
				}
			}
			if (flag != 0) {
				sameindex.add(link.getSlotsIndexInOneSW().get(i));
			}
		}
		return sameindex;
	}

	public ArrayList<Integer> spectrumAllocationOneRoute_index(LinearRoute route, int original_index) {
		ArrayList<Link> routelink = route.getLinklist();
		for (Link link : routelink) {
			if (route.getSlotsnum() == 0) {
				System.out.println("noslots");
				break;
			}
			link.getSlotsIndexInOneSW().clear();

			for (int i = 0; i <= original_index; i++) {
				if (link.getSlotsArray().get(i) == null) {
					int s = 1;
					for (int k = i; k < route.getSlotsnum() + i; k++) {

						if (link.getSlotsArray().get(k) != null) {
							s = 0;
							break;
						}

					}
					if (s != 0) {
						link.getSlotsIndexInOneSW().add(i);

					}
				}
			}
		}
		Link link = routelink.get(0);
		ArrayList<Integer> sameindex = new ArrayList<Integer>();
		sameindex.clear();
		for (int i = 0; i < link.getSlotsIndexInOneSW().size(); i++) {
			int index = link.getSlotsIndexInOneSW().get(i);
			int flag = 1;
			for (Link link2 : routelink) {
				if (!link2.getSlotsIndexInOneSW().contains(index)) {
					flag = 0;
					break;
				}
			}
			if (flag != 0) {
				sameindex.add(link.getSlotsIndexInOneSW().get(i));
			}
		}
		return sameindex;

	}

	public void setNodepair(NodePair nodepair) {
		this.nodepair = nodepair;
	}

	public NodePair getNodepair() {
		return nodepair;
	}

	public void setBandwidth(double bandwith) {
		this.bandwidth = bandwith;
	}

	public double getBandwidth() {
		return bandwidth;
	}

	public LinearRoute getWorkRoute() {
		return workRoute;
	}

	public void setWorkRoute(LinearRoute workRoute) {
		this.workRoute = workRoute;
	}

	public LinearRoute getProtectRoute() {
		return protectRoute;
	}

	public void setProtectRoute(LinearRoute protectRoute) {
		this.protectRoute = protectRoute;
	}

	public int getRequestRate() {
		return requestRate;
	}

	public void setRequestRate(int requestRate) {
		this.requestRate = requestRate;
	}

	public int getSlotNum() {
		return slotNum;
	}

	public void setSlotNum(int slotNum) {
		this.slotNum = slotNum;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public ArrayList<VirtualLink> getWorkVtLinkList() {
		return workVtLinkList;
	}

	public void setWorkVtLinkList(ArrayList<VirtualLink> workVtLinkList) {
		this.workVtLinkList = workVtLinkList;
	}

	public ArrayList<VirtualLink> getProtectVtLinkList() {
		return protectVtLinkList;
	}

	public void setProtectVtLinkList(ArrayList<VirtualLink> protectVtLinkList) {
		this.protectVtLinkList = protectVtLinkList;
	}
	
	

	public VirtualLink getWorkVLink() {
		return workVLink;
	}

	public void setWorkVLink(VirtualLink workVLink) {
		this.workVLink = workVLink;
	}

	public VirtualLink getProtectVLink() {
		return protectVLink;
	}

	public void setProtectVLink(VirtualLink protectVLink) {
		this.protectVLink = protectVLink;
	}

	public double getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(double arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public double getDepartTime() {
		return departTime;
	}

	public void setDepartTime(double departTime) {
		this.departTime = departTime;
	}

	public char getReqType() {
		return reqType;
	}

	public void setReqType(char reqType) {
		this.reqType = reqType;
	}

	public String getLayer() {
		return layer;
	}

	public void setLayer(String layer) {
		this.layer = layer;
	}

	public double getModulationCapcity() {
		return modulationCapcity;
	}

	public void setModulationCapcity(double modulationCapcity) {
		this.modulationCapcity = modulationCapcity;
	}

	public void releaseResource() {
		// TODO Auto-generated method stub

		for (Link link : this.getWorkRoute().getLinklist()) {
			for (int i = this.getStartIndex(); i < this.getStartIndex() + this.getSlotNum(); i++) {		
				link.getSlotsArray().get(i).setStatus(0);
			}
		}

	}

	public void releaseResource(Layer ipLayer) {
		// TODO Auto-generated method stub
		if (this.getLayer().equals(Constant.OPTICAL)) {
			for (Link link : this.getWorkRoute().getLinklist()) {
				for (int i = this.getStartIndex(); i < this.getStartIndex() + this.getSlotNum(); i++) {					
					link.getSlotsArray().get(i).setStatus(0);			
				}
			}
			Iterator<String> itr = ipLayer.getLinklist().keySet().iterator();
			while (itr.hasNext()) {
				Link link = (Link) (ipLayer.getLinklist().get(itr.next()));
				if (link.getName().equals(this.getNodepair().getName())) {
					link.getVirtualLinkList().remove(this.getWorkVLink());
					break;
				}
			}

		} else if (this.getLayer().equals(Constant.IP)) {
			for(Link link:this.getWorkRoute().getLinklist()){
				for(VirtualLink vLink:link.getVirtualLinkList()){
					if(this.getWorkVtLinkList().contains(vLink)){
						vLink.setRemanCapacity(vLink.getRemanCapacity()+this.getRequestRate());
						break;
					}
				}
			}
		}

	}

}
