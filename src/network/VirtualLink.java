package network;

import java.util.ArrayList;

public class VirtualLink {
	private String name;
	private int nature; // 0- work 1-protection
	private double cost;
	private double length;
	private double capacity;//������
//	private double sumFlow;//�ܵ�ռ��
	private double remanCapacity;//ʣ������S
	private ArrayList<Link> phyLinkList = null;
	private int startIndex;
	private int FSNum;

	public VirtualLink(String name,double cost, double length, double capacity, double remanCapacity) {
		super();
		this.name=name;
		this.cost = cost;
		this.length = length;
		this.capacity = capacity;
		this.remanCapacity = remanCapacity;
//		this.startIndex = startIndex;
//		FSNum = fSNum;
		phyLinkList=new ArrayList<Link>();
	}

	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public int getNature() {
		return nature;
	}

	public void setNature(int nature) {
		this.nature = nature;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public double getCapacity() {
		return capacity;
	}

	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}
/*
	public double getSumFlow() {
		return sumFlow;
	}

	public void setSumFlow(double sumFlow) {
		this.sumFlow = sumFlow;
	}
*/
	public double getRemanCapacity() {
		return remanCapacity;
	}

	public void setRemanCapacity(double remanCapacity) {
		this.remanCapacity = remanCapacity;
	}

	public ArrayList<Link> getPhyLinkList() {
		return phyLinkList;
	}

	public void setPhyLinkList(ArrayList<Link> phyLinkList) {
		this.phyLinkList = phyLinkList;
	}

	
	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getFSNum() {
		return FSNum;
	}

	public void setFSNum(int fSNum) {
		FSNum = fSNum;
	}

	@Override
	public String toString() {
		return "VirtualLink [nature=" + nature + ", cost=" + cost + ", length=" + length + ", capacity=" + capacity
				+ ", remanCapacity=" + remanCapacity + "]";
	}

	
}
