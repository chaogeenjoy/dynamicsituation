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
	 * �������������cost�߽���·�ɺ�Ƶ�׷��� 
	 * ��� ������
	 *        A�����
	 *        B������Ҫ��slot��Ŀ 
	 *        C�����Դ�ڵ��Ŀ�Ľڵ�
	 *        D���洢·�ɵ�LinearRoute���ʵ�� 
	 *        E��·��Ѱ�ҵ�constraint���ʵ�� 
	 *        F��������������
	 *���ڲ�����
	 *        Object���͵����飬��һ�����ص���·�ɣ��ڶ������ص�����ʼFS����
	 * 
	 */
	public Object[] minCost_RSABasedOnSWP_LengthLimited(Layer optLayer, int FSNum, Node srcNode, Node destNode,
			SearchConstraint constraint,int maxLength) {
		LinearRoute newRoute=new LinearRoute("newRoute", 0,"");
		double cost=Double.MAX_VALUE;
		 LinearRoute tempRoute=new LinearRoute("tempRoute", 1,"");
		 int startIndex=0;
		 if(FSNum==0){
			 System.out.println("*************************************************" + "\n����Ƶ��Ҫ��FS����Ϊ0\n"
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
			 System.out.println("*************************************************" + "\n����Ƶ��Ҫ��FS����Ϊ0\n"
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
	 * ������Ѱ��SWP,��SWP��·�����·����ֻҪ�����ɹ��ͽ��� 
	 * ��� ������ 
	 *          A����� 
	 *          B������Ҫ��slot��Ŀ 
	 *          C�����Դ�ڵ��Ŀ�Ľڵ�
	 *          D���洢·�ɵ�LinearRoute���ʵ�� 
	 *          E��·��Ѱ�ҵ�constraint���ʵ�� 
	 *          F:����Ѱ����·�����ԣ�
	 *���ڲ�����
	 *         ���ҵ���·�ɴ洢��LinearRoute���ʵ���� 
	 * 
	 */
	public Object[] firstFit_RSABasedOnSWP_Work_LengthLimited(Layer optLayer, int FSNum, Node srcNode, Node destNode,
			SearchConstraint constraint,int maxLength) {
		/*
		 * ɨ��ÿһ��SWP����·�ɲ�ѯ������һ���ҵ���ͷ���Ƶ�ף�����Ƶ����Դ��Ϣ
		 */
		LinearRoute newRoute=new LinearRoute("newRoute", 0,"");
		int startIndex = 0;
		if (FSNum == 0) {
			System.out.println("*************************************************" + "\n����Ƶ��Ҫ��FS����Ϊ0\n"
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
	 * ��������һ��SWPƽ���ϵ�·�� 
	 * ��ڲ����� 
	 *        A����� 
	 *        B����ʼ�ͽ�β��slot���� 
	 *        C�����Դ�ڵ��Ŀ�Ľڵ� 
	 *        D���洢·�ɵ�LinearRoute���ʵ�� 
	 *        E��·��Ѱ�ҵ�constraint���ʵ�� 
	 *���ڲ�����
	 *        ���ҵ���·�ɴ洢��LinearRoute���ʵ����
	 */

	public static void routeOnOneSWP(Layer optLayer, int startIndex, int endIndex, Node srcNode, Node destNode,
			LinearRoute newRoute, SearchConstraint constraint) {
		/*
		 * �Ȳ��Ҹò��е���·�б�������·�б��е�slotarray�еĴ�start��end��Щslot��ռ�õ���·��ӵ�
		 * ����������ȥ����·�ɣ�����·�ɵ�ʱ��ͻ�������Щ��·�� �����Ժ�����˸���ÿ����·��slotռ�����
		 */
		ArrayList<Link> tempDelList = new ArrayList<Link>();
		HashMap<String, Link> linkList = optLayer.getLinklist();
		Iterator<String> itr = linkList.keySet().iterator();
		while (itr.hasNext()) {
			Link link = (Link) linkList.get(itr.next());
			for (int i = startIndex; i < endIndex; i++) {
				if (link.getSlotsArray().get(i).getStatus() != 0) {
					// ����ռ�õ���·��ӵ�constraint�е���·�б���ȥ
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
	 * ���ڱ���·�����ԣ�slot������״̬��
	 *          A��0������
	 *          B��1��������
	 *          C��2���������ҹ����slot�����б���·������Ӧ�Ĺ�������·���ҵĹ�����·�Ƿ����ص���
	 *             ����У�����ã����򣬲�����
	 */
	public static void routeOnOneSWP_Protection(Layer optLayer, int startIndex, int endIndex, Node srcNode, Node destNode,
			LinearRoute newRoute, SearchConstraint constraint,ArrayList<VirtualLink> workVList) {
		/*
		 * �Ȳ��Ҹò��е���·�б�������·�б��е�slotarray�еĴ�start��end��Щslot��ռ�õ���·��ӵ�
		 * ����������ȥ����·�ɣ�����·�ɵ�ʱ��ͻ�������Щ��·�� �����Ժ�����˸���ÿ����·��slotռ�����
		 */
		ArrayList<Link> tempDelList = new ArrayList<Link>();
		HashMap<String, Link> linkList = optLayer.getLinklist();
		Iterator<String> itr = linkList.keySet().iterator();
		while (itr.hasNext()) {
			Link link = (Link) linkList.get(itr.next());
			for (int i = startIndex; i < endIndex; i++) {
				Slot currentSlot=link.getSlotsArray().get(i);
				if ((currentSlot.getStatus() == 1)||((currentSlot.getStatus()==2)&&(currentSlot.workJoint(workVList)))) {
					// ����ռ�õ���·��ӵ�constraint�е���·�б���ȥ
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
