package general;

import java.util.ArrayList;
import java.util.Iterator;

import network.Link;
import network.VirtualLink;

public class Slot {
	private ArrayList<VirtualLink> virtualLinkList;//�洢ռ�õ�ǰslot�Ĺ���·����ռ�õ�������·���б�
	private int status = 0; // 0Ϊ��ʼ״̬,�����ù�occupiedsize�Ƿ�Ϊ0���ж��Ƿ��Ѿ���ռ�ã���ͨ��status�жϱ�����·�����߱���·��ռ�ã�
							// 1Ϊ����·��ռ��
							// 2Ϊ����·��ռ��

	public Slot() {
		super();
		this.status = 0;
		this.virtualLinkList=new ArrayList<VirtualLink>();
	}
	//�����жϹ���·���Ƿ���������·�ص������ص�����true,���򷵻�false
	public boolean workJoint(ArrayList<VirtualLink> workVList){
		boolean flag=false;
		
		
		Iterator<VirtualLink> itr=virtualLinkList.iterator();
		Here:while(itr.hasNext()){
			VirtualLink vtLink=(VirtualLink) itr.next();
			Iterator<Link> itr1=vtLink.getPhyLinkList().iterator();
			while(itr1.hasNext()){
				Link link=(Link) itr1.next();
				for(int i=0;i<workVList.size();i++){
					if(workVList.get(i).getPhyLinkList().contains(link)){
						flag=true;
						break Here;
					}
				}
			}
		}
		
		return flag;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public ArrayList<VirtualLink> getVirtualLinkList() {
		return virtualLinkList;
	}

	public void setVirtualLinkList(ArrayList<VirtualLink> virtualLinkList) {
		this.virtualLinkList = virtualLinkList;
	}
	
	



}
