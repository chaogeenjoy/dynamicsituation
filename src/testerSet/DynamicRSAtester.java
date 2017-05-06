package testerSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import demand.Request;
import general.Constant;
import network.Layer;
import network.NodePair;
import networkDesign.DynamicRSA;

public class DynamicRSAtester {
	public static void main(String[] args) {
		double[] trafficLoadList={16};
		for(int i=0;i<trafficLoadList.length;i++){
			double erlangLoad=trafficLoadList[i];
			System.out.println("**************************\n erlang load ="+erlangLoad+"\n"
					+ "***************************");
			
			Layer optLayer=new Layer("optical", 0, null);
			optLayer.readTopology("E:\\其他\\RPtopology\\NODE11_1.csv");
			optLayer.generateNodepairs();
			
			
			ArrayList<Request> requestList=new ArrayList<Request>();
			
			
			
			Random randTime=new Random(1);  
			Random randRate=new Random(3);
			Iterator<String> itr=optLayer.getNodepairlist().keySet().iterator();
			while(itr.hasNext()){
				NodePair nodePair=(NodePair)(optLayer.getNodepairlist().get(itr.next()));
				double arriveTime=1/erlangLoad*(-Math.log(randTime.nextDouble()));
				int rate=randRate.nextInt(391)+10;//10到400之间
				Request request=new Request(nodePair, rate, arriveTime, arriveTime-Math.log(randTime.nextDouble()), Constant.ARRIVAL);	
			    insertRequest(requestList, request);
			}
			
			/*
			 * 统计达到的业务以及总的阻塞
			 *      1：  从需求的列表中取出第一条需求，如果当前的需求为到达需求则：
			 *           A：为该需求新建一个离开需求，插入到需求列表中
			 *           B：为该需求建立光路，如果失败则阻塞掉
			 *           C：为当前节点对新建一个到达请求，到达时间是当前的到达时间加上一个时间，
			 *             达到概率不固定，插入到列表中
			 *      2：如果当前需求为离开需求，则：
			 *           A：释放链路上的资源
			 *           B：从列表中移除当前的请求
			 */
			int requestNum=0;
			int totalBlockNum=0; 
			int totalRate=0;
			int totalBlockRate=0;
			while((requestNum<=50*Constant.SIM_WAN)&&(requestList.size()!=0)){
				Request currentRequest=requestList.get(0);
				if(currentRequest.getReqType()==Constant.ARRIVAL){
					requestNum++;		
					if(requestNum%(5*Constant.SIM_WAN)==0){
						System.out.println("第"+requestNum+"个，\t已经阻塞的个数为：  "+totalBlockNum);
					}
					totalRate+=currentRequest.getRequestRate();
					
					Request departRequest=new Request(currentRequest.getNodepair(), currentRequest.getRequestRate(), currentRequest.getArrivalTime(), currentRequest.getDepartTime(), Constant.DEPARTURE);
					insertRequest(requestList, departRequest);
					//为当前的request 进行RSA
					DynamicRSA dyRSA=new DynamicRSA();
					dyRSA.SWPbasedRSA(currentRequest, optLayer);
					
					
					if(currentRequest.getWorkRoute().getLinklist().size()==0){
						totalBlockNum++;
						totalBlockRate+=currentRequest.getRequestRate();
					}else{
						departRequest.setSlotNum(currentRequest.getSlotNum());
						departRequest.setStartIndex(currentRequest.getStartIndex());
						departRequest.setWorkRoute(currentRequest.getWorkRoute());
					}
					
					
					
					//新建达到
					int newArrivalRate=randRate.nextInt(391)+10;//10到400之间
					double newArrivalTime=currentRequest.getArrivalTime()-1/erlangLoad*Math.log(randTime.nextDouble());
					double newDepartTime=newArrivalTime-Math.log(randTime.nextDouble());
					Request newArrival=new Request(currentRequest.getNodepair(), newArrivalRate, newArrivalTime, newDepartTime, Constant.ARRIVAL);
					requestList.remove(0);
					insertRequest(requestList, newArrival);
				}else{
					if(currentRequest.getWorkRoute().getLinklist().size()!=0){
						currentRequest.releaseResource();
					}
					
					
					requestList.remove(0);
					
				}
			}
			
			
			System.out.println("阻塞请求总数为："+totalBlockNum+"没阻塞的："+(requestNum-totalBlockNum-1));
			System.out.println("阻塞率为："+((double)100*totalBlockRate/totalRate)+"%");
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
