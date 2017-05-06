package testerSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import demand.Request;
import general.Constant;
import network.Layer;
import network.NodePair;
import networkDesign.DynamicGrooming;
import networkDesign.DynamicRSA;

public class DymamicGroomingTester {
	public static void main(String[] args) {
		double[] trafficLoadList={1};
		int ipNum=0;
		for(int i=0;i<trafficLoadList.length;i++){
			double erlangLoad=trafficLoadList[i];
			System.out.println("**************************\n erlang load ="+erlangLoad+"\n"
					+ "***************************");
			
			Layer optLayer=new Layer("optical", 0, null);
			Layer ipLayer=new Layer("IP Layer",1,null);
			optLayer.readTopology("E:\\����\\RPtopology\\NODE11_1.csv");
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


			
			
			int requestNum=0;
			int totalBlockNum=0; 
			int totalRate=0;
			int totalBlockRate=0;
			while((requestNum<=Constant.SIM_WAN)&&(requestList.size()!=0)){
				Request currentRequest=requestList.get(0);
				if(currentRequest.getReqType()==Constant.ARRIVAL){
					requestNum++;		
					if(requestNum%(Constant.SIM_WAN/5)==0){
						System.out.println("��"+requestNum+"����\t�Ѿ������ĸ���Ϊ��  "+totalBlockNum);
					}
					totalRate+=currentRequest.getRequestRate();
					
					Request departRequest=new Request(currentRequest.getNodepair(), currentRequest.getRequestRate(), currentRequest.getArrivalTime(), currentRequest.getDepartTime(), Constant.DEPARTURE);
					insertRequest(requestList, departRequest);
					
					/*
					 * Grooming process��
					 *       1.Grooming the request in the IP layer,if failed turn to step 2
					 *       2.Create the new light path , if failed turn to step 3
					 *       3.Current request block
					 * 
					 */ 
					DynamicGrooming dnGrooming=new DynamicGrooming();
					boolean grooming=dnGrooming.dynamicGrroming(currentRequest, ipLayer, optLayer);
										
					if(grooming){
						ipNum++;
						if(currentRequest.getLayer().equals(Constant.IP)){
							departRequest.setLayer(currentRequest.getLayer());
							departRequest.setWorkRoute(currentRequest.getWorkRoute());
							departRequest.setWorkVtLinkList(currentRequest.getWorkVtLinkList());
						}else if(currentRequest.getLayer().equals(Constant.OPTICAL)){
							departRequest.setLayer(currentRequest.getLayer());
							departRequest.setStartIndex(currentRequest.getSlotNum());
							departRequest.setSlotNum(currentRequest.getSlotNum());
							departRequest.setWorkRoute(currentRequest.getWorkRoute());
							departRequest.setWorkVLink(currentRequest.getWorkVLink());
						}
					}else{
						totalBlockNum++;
						totalBlockRate+=currentRequest.getRequestRate();
					}
					
					//�½��ﵽ
					int newArrivalRate=randRate.nextInt(391)+10;//10��400֮��
					double newArrivalTime=currentRequest.getArrivalTime()-1/erlangLoad*Math.log(randTime.nextDouble());
					double newDepartTime=newArrivalTime-Math.log(randTime.nextDouble());
					Request newArrival=new Request(currentRequest.getNodepair(), newArrivalRate, newArrivalTime, newDepartTime, Constant.ARRIVAL);
					requestList.remove(0);
					insertRequest(requestList, newArrival);
				}else {
						currentRequest.releaseResource(ipLayer);
					
					
					
					requestList.remove(0);
					
				}
			}
			
			System.out.println("��IP��·�ɳɹ��ĸ���Ϊ��"+ipNum);
			System.out.println("������������Ϊ��"+totalBlockNum+"û�����ģ�"+(requestNum-totalBlockNum-1));
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