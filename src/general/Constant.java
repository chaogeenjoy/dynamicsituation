package general;

public class Constant {
	public final static int UNVISITED=0;
	public final static int VISITEDONCE=1;
	public final static int VISITEDTWICE=2;
	public final static int UNORDER=0;          //�ڵ�Ի�δ��˳������
	public final static int ORDERED=1;          //�ڵ���Ѿ���˳������
	public final static int WAVE_PRE_FIBER=40;  //WDM��ÿ�����˴������󲨳���
	public final static int CAPA_OF_WAVE=100;    //ÿ��������������40Gb/s 
	public final static int F=320;////ÿ��������·�ܹ����е�Frequency Slots����Ŀ
	public final static int FREE=0;
	public final static int BUSY=1;
	public final static int WORK=0;
	public final static int PROTECTION=1;
	public final static int NUM_OF_ACTNODEPAIRS=100; 
	public final static int WAVE_UNASSIGNMENT=0;
	public final static int WAVE_ASSIGNMENTED=1;
	public final static int AVER_DEMAND=100;   //�ڵ��֮���ƽ������
	public final static int SPAN_DIST_EDFA=80; //�������ڵ�EDFA���80km
	public final static int ENCONS_OF_ROUTERPORT=1000; //ÿ��·�ɶ˿ں���1000W
	public final static int ENCONS_OF_TRANSPONDER=73;  //ÿ���任������73W
	public final static int ENCONS_OF_EDFA=8;  //ÿ��EDFA����8W 
	//added by Charles Guo
	public final static int TOTAL_FS_PER_LINK=320;//ÿ��������·�ܹ����е�Frequency Slots����Ŀ
	public static final int MAXIMUM = 10000000;
	public final static char ARRIVAL=1;
	public final static char DEPARTURE=2;
	public static final int SIM_WAN = 10000;
	public static final String IP="IP Layer";
	public static final String OPTICAL="optical layer";
}