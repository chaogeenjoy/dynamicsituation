package testerSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import demand.Request;
import general.Constant;
import network.Layer;
import network.NodePair;
import networkDesign.DynamicGrooming;

public class DynamicGroomingTester {
	public static void main(String[] args) {
		double[] trafficLoadList={1,10,15};
		for(int i=0;i<trafficLoadList.length;i++){
			double erlangLoad=trafficLoadList[i];
			System.out.println("**************************\n erlang load ="+erlangLoad+"\n"
					+ "***************************");
			
			Layer optLayer=new Layer("optical", 0, null);
			Layer ipLayer=new Layer("IP",0,null);
			optLayer.readTopology("E:\\����\\RPtopology\\NODE6.csv");
			optLayer.generateNodepairs();
			ipLayer.copyNodes(optLayer);
			ipLayer.generateNodepairs();
			
			
			ArrayList<Request> requestList=new ArrayList<Request>();
			
			
			
			Random randTime=new Random(1);  
			Random randRate=new Random(3);
			Iterator<String> itr=ipLayer.getNodepairlist().keySet().iterator();
			while(itr.hasNext()){
				NodePair nodePair=(NodePair)(ipLayer.getNodepairlist().get(itr.next()));
				double arriveTime=1/erlangLoad*(-Math.log(randTime.nextDouble()));
				int rate=randRate.nextInt(391)+10;//10��400֮��
				Request request=new Request(nodePair, rate, arriveTime, arriveTime-Math.log(randTime.nextDouble()), Constant.ARRIVAL);	
			    insertRequest(requestList, request);
			}
			
			/*
			 * ͳ�ƴﵽ��ҵ���Լ��ܵ�����
			 *      1��  ��������б���ȡ����һ�����������ǰ������Ϊ����������
			 *           A��Ϊ�������½�һ���뿪���󣬲��뵽�����б���
			 *           B��Ϊ����������·�����ʧ����������
			 *           C��Ϊ��ǰ�ڵ���½�һ���������󣬵���ʱ���ǵ�ǰ�ĵ���ʱ�����һ��ʱ�䣬
			 *             �ﵽ���ʲ��̶������뵽�б���
			 *      2�������ǰ����Ϊ�뿪������
			 *           A���ͷ���·�ϵ���Դ
			 *           B�����б����Ƴ���ǰ������
			 */
			int requestNum=0;
			int totalBlockNum=0; 
			int totalRate=0;
			int totalBlockRate=0;
			while((requestNum<=10*Constant.SIM_WAN)&&(requestList.size()!=0)){
				Request currentRequest=requestList.get(0);
				if(currentRequest.getReqType()==Constant.ARRIVAL){
					requestNum++;		
					if(requestNum%(Constant.SIM_WAN)==0){
						System.out.println("��"+requestNum+"����\t�Ѿ������ĸ���Ϊ��  "+totalBlockNum);
					}
					totalRate+=currentRequest.getRequestRate();
					
					Request departRequest=new Request(currentRequest.getNodepair(), currentRequest.getRequestRate(), currentRequest.getArrivalTime(), currentRequest.getDepartTime(), Constant.DEPARTURE);
					insertRequest(requestList, departRequest);
					/*//Ϊ��ǰ��request ����RSA
					DynamicRSA dyRSA=new DynamicRSA();
					dyRSA.SWPbasedRSA(currentRequest, optLayer);*/
					DynamicGrooming dnGrooming=new DynamicGrooming();
					dnGrooming.dynamicGrroming(currentRequest, ipLayer, optLayer);
					
					
					if(currentRequest.getWorkRoute().getLinklist().size()==0){
						totalBlockNum++;
						totalBlockRate+=currentRequest.getRequestRate();
					}else{
						if(currentRequest.getLayer().equals(Constant.OPTICAL)){
							departRequest.setLayer(currentRequest.getLayer());
							departRequest.setSlotNum(currentRequest.getSlotNum());
							departRequest.setStartIndex(currentRequest.getStartIndex());
							departRequest.setWorkRoute(currentRequest.getWorkRoute());
							departRequest.setWorkVLink(currentRequest.getWorkVLink());
						}else{
							departRequest.setLayer(currentRequest.getLayer());
							departRequest.setWorkRoute(currentRequest.getWorkRoute());
							departRequest.setWorkVtLinkList(currentRequest.getWorkVtLinkList());
						}
					}
					
					
					
					//�½��ﵽ
					int newArrivalRate=randRate.nextInt(391)+10;//10��400֮��
					double newArrivalTime=currentRequest.getArrivalTime()-1/erlangLoad*Math.log(randTime.nextDouble());
					double newDepartTime=newArrivalTime-Math.log(randTime.nextDouble());
					Request newArrival=new Request(currentRequest.getNodepair(), newArrivalRate, newArrivalTime, newDepartTime, Constant.ARRIVAL);
					requestList.remove(0);
					insertRequest(requestList, newArrival);
				}else{
					if(currentRequest.getWorkRoute().getLinklist().size()!=0){
						currentRequest.releaseResource(ipLayer);
					}
									
					requestList.remove(0);
					
				}
			}
			
			System.out.println("IP��·�ɳɹ�����������Ϊ��"+Constant.NUM);
			System.out.println("������������Ϊ��"+totalBlockNum+"\tû�����ģ�"+(requestNum-totalBlockNum-1));
			System.out.println("������Ϊ��"+((double)100*totalBlockRate/totalRate)+"%");
		}
	}
	
	
	
	
	
	
	public static void insertRequest(ArrayList<Request> requestList, Request request){
		if(requestList.size()==0){
			requestList.add(0, request);
		}else{
			double occurTime;
			if(request.getReqType()==Constant.ARRIVAL)
				occurTime=request.getArrivalTime();
			else
				occurTime=request.getDepartTime();
			boolean inserted=false;
			for(int i=0;i<requestList.size();i++){
				Request currentRequest=requestList.get(i);
				double compareTime;
				if(currentRequest.getReqType()==Constant.ARRIVAL)
					compareTime=currentRequest.getArrivalTime();
				else
					compareTime=currentRequest.getDepartTime();
				if(occurTime<compareTime){
					requestList.add(i, request);
					inserted=true;
					break;
				}
			}
			if(!inserted){
				requestList.add(request);
			}
		}
	}
	
	

}