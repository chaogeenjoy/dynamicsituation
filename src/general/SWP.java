package general;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import graphalgorithms.RouteSearching;
import graphalgorithms.SearchConstraint;
import network.Layer;
import network.Link;
import network.Node;
import network.VirtualLink;
import subgraph.LinearRoute;

public class SWP {
	/*
	 * 方法：按照最短cost策进行路由和频谱分配 
	 * 入口 参数：
	 *        A：光层
	 *        B：所需要的slot数目 
	 *        C：光层源节点和目的节点
	 *        D：存储路由的LinearRoute类的实例 
	 *        E：路由寻找的constraint类的实例 
	 *        F：传输距离的限制
	 *出口参数：
	 *        Object类型的数组，第一个返回的是路由，第二个返回的是起始FS索引
	 * 
	 */
	public Object[] minCost_RSABasedOnSWP_LengthLimited(Layer optLayer, int FSNum, Node srcNode, Node destNode,
			SearchConstraint constraint,int maxLength) {
		LinearRoute newRoute=new LinearRoute("newRoute", 0,"");
		double cost=Double.MAX_VALUE;
		 LinearRoute tempRoute=new LinearRoute("tempRoute", 1,"");
		 int startIndex=0;
		 if(FSNum==0){
			 System.out.println("*************************************************" + "\n错误！频谱要求FS个数为0\n"
						+ "********************************************************");
		 }else{
			 for(int i=0;i<Constant.F-FSNum+1;i++){
				 routeOnOneSWP(optLayer, i, i+FSNum, srcNode, destNode, tempRoute, constraint);
				if((tempRoute.getLinklist().size()!=0)&&(tempRoute.getLength()<=maxLength)){
					if(tempRoute.getCost()<cost){
						cost=tempRoute.getCost();
						newRoute=tempRoute;
						startIndex=i;
					}
					 
				}
				
			 }
		 }
		 
		 Object[] obj=new Object[]{newRoute,startIndex};
		 return obj;
		 	
	}
	
	
	public Object[] minCost_RSABasedOnSWP_LengthLimited_Protection(Layer optLayer, int FSNum, Node srcNode, Node destNode,
			SearchConstraint constraint,int maxLength) {
		LinearRoute newRoute=new LinearRoute("newRoute", 0,"");
		double cost=Double.MAX_VALUE;
		 LinearRoute tempRoute=new LinearRoute("tempRoute", 1,"");
		 int startIndex=0;
		 if(FSNum==0){
			 System.out.println("*************************************************" + "\n错误！频谱要求FS个数为0\n"
						+ "********************************************************");
		 }else{
			 for(int i=0;i<Constant.F-FSNum+1;i++){
				 routeOnOneSWP(optLayer, i, i+FSNum, srcNode, destNode, tempRoute, constraint);
				if((tempRoute.getLinklist().size()!=0)&&(tempRoute.getLength()<=maxLength)){
					if(tempRoute.getCost()<cost){
						cost=tempRoute.getCost();
						newRoute=tempRoute;
						startIndex=i;
					}
					 
				}
				
			 }
		 }
		 
		 Object[] obj=new Object[]{newRoute,startIndex};
		 return obj;
		 	
	}
	
	/*
	 * 方法：寻找SWP,在SWP上路由最短路径，只要搜索成功就结束 
	 * 入口 参数： 
	 *          A：光层 
	 *          B：所需要的slot数目 
	 *          C：光层源节点和目的节点
	 *          D：存储路由的LinearRoute类的实例 
	 *          E：路由寻找的constraint类的实例 
	 *          F:所搜寻的链路的属性：
	 *出口参数：
	 *         查找到的路由存储在LinearRoute类的实例中 
	 * 
	 */
	public Object[] firstFit_RSABasedOnSWP_Work_LengthLimited(Layer optLayer, int FSNum, Node srcNode, Node destNode,
			SearchConstraint constraint,int maxLength) {
		/*
		 * 扫描每一个SWP进行路由查询，当第一次找到后就分配频谱，更新频谱资源信息
		 */
		LinearRoute newRoute=new LinearRoute("newRoute", 0,"");
		int startIndex = 0;
		if (FSNum == 0) {
			System.out.println("*************************************************" + "\n错误！频谱要求FS个数为0\n"
					+ "********************************************************");
		} else {
			for (int i = 0; i < Constant.F - FSNum + 1; i++) {

				routeOnOneSWP(optLayer, i, i + FSNum, srcNode, destNode, newRoute, constraint);
				if ((newRoute.getLinklist().size() != 0)&&(newRoute.getLength()<maxLength)) {
					startIndex = i;
					break;
				}
			}
		}
		
		Object[] obj=new Object[]{newRoute,startIndex};
		 return obj;

	}

	/*
	 * 方法：在一个SWP平面上的路由 
	 * 入口参数： 
	 *        A：光层 
	 *        B：起始和结尾的slot索引 
	 *        C：光层源节点和目的节点 
	 *        D：存储路由的LinearRoute类的实例 
	 *        E：路由寻找的constraint类的实例 
	 *出口参数：
	 *        查找到的路由存储在LinearRoute类的实例中
	 */

	public static void routeOnOneSWP(Layer optLayer, int startIndex, int endIndex, Node srcNode, Node destNode,
			LinearRoute newRoute, SearchConstraint constraint) {
		/*
		 * 先查找该层中的链路列表，并将链路列表中的slotarray中的从start到end这些slot被占用的链路添加到
		 * 限制条件中去进行路由，这样路由的时候就会跳过这些链路了 完事以后别忘了更新每个链路的slot占用情况
		 */
		ArrayList<Link> tempDelList = new ArrayList<Link>();
		HashMap<String, Link> linkList = optLayer.getLinklist();
		Iterator<String> itr = linkList.keySet().iterator();
		while (itr.hasNext()) {
			Link link = (Link) linkList.get(itr.next());
			for (int i = startIndex; i < endIndex; i++) {
				if (link.getSlotsArray().get(i).getStatus() != 0) {
					// 将被占用的链路添加到constraint中的链路列表中去
					tempDelList.add(link);
					break;
				}
			}
		}
		for (Link link : tempDelList) {
			optLayer.removeLink(link.getName());
		}
		RouteSearching newRS = new RouteSearching();
		newRS.Dijkstras(srcNode, destNode, optLayer, newRoute, constraint);
		for (Link link : tempDelList) {
			optLayer.addLink(link);
		}

	}
	
	
	/*
	 * 对于保护路径而言，slot有三种状态：
	 *          A：0，可用
	 *          B：1，不可用
	 *          C：2，共享，查找共享该slot的所有保护路径所对应的共工作链路和我的工作链路是否有重叠，
	 *             如果有，则可用，否则，不可用
	 */
	public static void routeOnOneSWP_Protection(Layer optLayer, int startIndex, int endIndex, Node srcNode, Node destNode,
			LinearRoute newRoute, SearchConstraint constraint,ArrayList<VirtualLink> workVList) {
		/*
		 * 先查找该层中的链路列表，并将链路列表中的slotarray中的从start到end这些slot被占用的链路添加到
		 * 限制条件中去进行路由，这样路由的时候就会跳过这些链路了 完事以后别忘了更新每个链路的slot占用情况
		 */
		ArrayList<Link> tempDelList = new ArrayList<Link>();
		HashMap<String, Link> linkList = optLayer.getLinklist();
		Iterator<String> itr = linkList.keySet().iterator();
		while (itr.hasNext()) {
			Link link = (Link) linkList.get(itr.next());
			for (int i = startIndex; i < endIndex; i++) {
				Slot currentSlot=link.getSlotsArray().get(i);
				if ((currentSlot.getStatus() == 1)||((currentSlot.getStatus()==2)&&(currentSlot.workJoint(workVList)))) {
					// 将被占用的链路添加到constraint中的链路列表中去
					tempDelList.add(link);
					break;
				}
			}
		}
		for (Link link : tempDelList) {
			optLayer.removeLink(link.getName());
		}
		RouteSearching newRS = new RouteSearching();
		newRS.Dijkstras(srcNode, destNode, optLayer, newRoute, constraint);
		for (Link link : tempDelList) {
			optLayer.addLink(link);
		}

	}
}
