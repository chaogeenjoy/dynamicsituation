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
	 * ����������request��������IP��·�ɸ����� 
	 *          1����IP���ÿ����·�����Ҹ���·��������·�е�ʣ������������·
	 *          2��������������ڵ�ǰ���Ե����������򽫶�Ӧ������·��cost��length��Ϣ���Ƶ���·��ȥ 
	 *          3�����򽫸���·�ų�
	 *          4����ʣ���������·�ɸ����� 
	 *          5:���·�ɳɹ�������������·��������Ϣ������Ļ������Ű� 
	 *          6������Ļ����½���·
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
				this.sortVTLink(link);//����ʣ�����������Ǹ������ã���������þ�����
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
		 * ·�ɳɹ��Ļ���
		 *     1������·����ӵ�request��work route��
		 *     2�����õ���������·Ҳһ��һ����ӵ�request��ȥ
		 *     3��������·��ʣ��������Ϣ
		 */
		if(newRoute0.getLinklist().size()!=0){
			request.setWorkRoute(newRoute0);
			request.setLayer(Constant.IP);//�����request����IP���
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
			//��tempvirtuallink����Ϊ�գ��Ա����
			Iterator<String> it0=ipLayer.getLinklist().keySet().iterator();
			while(it0.hasNext()){
				Link link=(Link) (ipLayer.getLinklist().get(it0.next()));
				link.setTempVirtualLink(null);
			}*/
			
		}else{
			/*
			 * ���·��ʧ�ܵĻ������й���RSA��
			 *     1��SWP·�ɳɹ��Ļ������¹��Ƶ��������Ϣ��ͬʱ��slot number, start index �Լ� work·����Ϣ��ӵ�request ��
			 *     2��step1����DynamicRSA����ڲ����
			 *     3���½���·����ӵ�ip ���Լ�request��·�ɣ��½�������·����ӵ��ղŵ�������·�У��Լ�request��������·
			 *     4��step3Ҫע�������·�Ƿ��Ѿ����ڣ�����Ѿ����ڣ��Ͳ���Ҫ�½���·�ˣ�ֻҪ�½�������·�Ϳ�����
			 */
			DynamicRSA dRSA=new DynamicRSA();
			dRSA.SWPbasedRSA(request, optLayer);
			if(request.getWorkRoute().getLinklist().size()!=0){
//				System.out.println("��ǰ�����ڹ��RSA�ɹ�");
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
				
				//����ط�����˵��.txt��12���Ժ�
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
	
	
	
	
	//����·��������·����ʣ��������С����,�Ӵ�С
		public void sortVTLink(Link link){
			ArrayList<VirtualLink> vtLinkList=new ArrayList<VirtualLink>();
			if(link.getVirtualLinkList().size()==0){
				System.out.println("\t�޷�������������·����");
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
